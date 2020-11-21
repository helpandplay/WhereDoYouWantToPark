package com.example.capston_park_application;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.location.Location;
import android.os.AsyncTask;
import android.util.Log;

import androidx.annotation.Nullable;

import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;

class DataManager extends AsyncTask<String, Integer, String> {

    private static SQLiteDatabase db;
    private static ParkingLotDBHelper FavoriteDBHelper;
    private static ParkingLotDBHelper ScopeDBHelper;
    private boolean useFireBaseDB;
    private boolean doPrintDebug;
    private static Context nowContext;
    private LoadingActivity la;
    private static String scopeDBName = "Scope";
    private static String favoriteDBName = "Favorite";
    public static String defaultSearchScope = "200";
    private static FireBaseStatus FireBaseStatus = null;

    // 초기화 되었는지를 나타내는 boolean 형 플래그
    private static boolean isInit = false;

    public static boolean isInit(){
        return isInit;
    }

    public DataManager(boolean useFireBaseDB, boolean doPrintDebug, LoadingActivity la){
        this.useFireBaseDB = useFireBaseDB;
        this.doPrintDebug = doPrintDebug;
        this.la = la;
    }

    // SQLite를 이용하기 위한 Context 저장
    //DB를 사용하려면 이 메소드를 호출해서 context를 업데이트 해줘야합니다.
    //ex. Activity를 상속받는 클래스에서 DataManager.setContext(this);
    public static void setContext(Context context){
        nowContext = context;
    }
    // 주차장 리스트
    public static ArrayList<ParkingLot> List_ParkingLot;

    //즐겨찾기 리스트를 불러오기
    //Context : 현재 화면의 context
    //ex. ArrayList<FavoriteDB> testFavorite = DataManager.ReadFavoriteList();
    //          String result="";
    //        for(int i=0; i<testFavorite.size(); i++){
    //            result += testFavorite.get(i).parkingName + testFavorite.get(i).parkingID;
    //        }
    public static ArrayList<FavoriteDB> ReadFavoriteList(){
        ArrayList<FavoriteDB> list = new ArrayList<FavoriteDB>();
        Cursor cursor;
        FavoriteDBHelper = new ParkingLotDBHelper(nowContext, favoriteDBName);
        db =  FavoriteDBHelper.getReadableDatabase();
        cursor = db.rawQuery(FavoriteDBSQL.DATA_READ, null);

        while(cursor.moveToNext()){
            FavoriteDB favoriteDB = new FavoriteDB();
            favoriteDB.parkingID=cursor.getString(cursor.getColumnIndex("ParkingLot_id"));
            favoriteDB.parkingName=cursor.getString(cursor.getColumnIndex("ParkingLot_name"));
            list.add(favoriteDB);
        }
        cursor.close();
        db.close();
        return list;
    }

    //즐겨찾기 요소를 삭제하기
    //parkingName : 리스트 상에서 보이는 주차장 이름
    //ex. DataManager.deleteFavoriteElement("주차장이름");
    public static void deleteFavoriteElement(String parkingName){
        FavoriteDBHelper = new ParkingLotDBHelper(nowContext, favoriteDBName);
        db =  FavoriteDBHelper.getWritableDatabase();
        int result = db.delete(favoriteDBName, "ParkingLot_name=?", new String[]{parkingName});
        if(result >0){
            Log.i("","삭제 완료");
        }else{
            Log.w("", "삭제 실패");
        }
        db.close();
    }

    //즐겨찾기 요소를 삽입하기
    //parkingID : csv에서 가져온 주차장 고유번호
    //parkingName : 호면에 보이는 주차장 이름
    //ex. DataManager.insertFavoriteElement("주자창ID", "주차장이름");
    public static void insertFavoriteElement(String parkingID, String parkingName){
        FavoriteDBHelper = new ParkingLotDBHelper(nowContext, favoriteDBName);
        db =  FavoriteDBHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("ParkingLot_id", parkingID);
        values.put("ParkingLot_name", parkingName);

        long result = db.insert(favoriteDBName, null, values);
        if(result >0){
            Log.d("","즐겨찾기 요소 추가 성공");
        }else{
            Log.e("","즐겨찾기 요소 추가 실패");
        }
        db.close();
    }

    //지도 거리 디폴트 값 설정
    //호출할 필요 없습니다. SQLite 로딩 시 자동으로 호출됩니다.
    private static void insertDefaultSearchScope(){
        ScopeDBHelper = new ParkingLotDBHelper(nowContext, scopeDBName);
        db =  ScopeDBHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("Scope_distance", "200");
        long result = db.insert(scopeDBName, null, values);
        if(result >0){
            Log.d("","거리설정 디폴트값 추가 성공");
        }else{
            Log.e("","거리설정 디폴트값 추가 실패");
        }
    }

    //지도 거리 설정값 불러오기
    //Context : 현재 화면의 context
    //ex . String scope = DataManager.ReadSearchScope();
    public static String ReadSearchScope(){
        String result;
        Cursor cursor;
        ScopeDBHelper = new ParkingLotDBHelper(nowContext, scopeDBName);
        db =  ScopeDBHelper.getReadableDatabase();
        cursor = db.rawQuery(ScopeDBSQL.DATA_READ, null);

        if(!cursor.moveToFirst()){
            insertDefaultSearchScope();
        }
        cursor = db.rawQuery(ScopeDBSQL.DATA_READ, null);
        cursor.moveToFirst();
        result = cursor.getString(cursor.getColumnIndex("Scope_distance"));
        Log.i("", "거리 설정 가져오기 완료");

        cursor.close();
        db.close();
        return result;
    }

    //지도 설정값 바꾸기
    //ex . DataManager.UpdateSearchScope("설정한거리");
    public static void UpdateSearchScope(String distance){
        ScopeDBHelper = new ParkingLotDBHelper(nowContext, scopeDBName);
        db =  ScopeDBHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("Scope_distance", distance);

        int result = db.update(scopeDBName, values, "Scope_distance=?", new String[]{distance});
        if(result != 1){
            Log.e("","거리 설정 업데이트 실패" );
        }else{
            Log.d("","거리 설정 업데이트 성공" );
        }
        db.close();
    }

    public static FireBaseStatus Init_CheckFirebase(boolean doPrintDebug){
        FireBaseStatus res = null;

        return res;
    }

    public static void Init_DB(boolean useFireBaseDB, boolean doPrintDebug) {

        isInit = false;

        // 리스트 초기화
        List_ParkingLot = new ArrayList<ParkingLot>();
//        List_Favorite = new ArrayList<Favorite>();


        // 파이어베이스에서 주차장 데이터를 가져옵니다.
        if(useFireBaseDB){
            List_ParkingLot = FireBase.getData();
        }

        // FireBase 사용하지 않고 하드코딩된 데이터 이용 시 사용
        else { //  => useFireBaseDB == false
            List_ParkingLot.add(new ParkingLot(
                    "00000001",
                    "카카오 판교오피스",
                    "경기 성남시 분당구 판교역로 235 에이치스퀘어 엔동",
                    "경기도 삼평동 681 에이치스퀘어 N동 6층",
                    "123",
                    "02-1234-5678",
                    "127.108640",
                    "37.402111"));

            List_ParkingLot.add(new ParkingLot(
                    "00000002",
                    "한남대학교",
                    "대전 대덕구 한남로 70 한남대학교",
                    "대전 대덕구 오정동 133",
                    "300",
                    "042-1234-5678",
                    "127.42114766",
                    "36.35445367"));

        }

        // 디버그를 위해 리스트의 첫 번째 데이터를 출력
        if (DataManager.List_ParkingLot.size() >= 1 && doPrintDebug) {
            ParkingLot d = DataManager.List_ParkingLot.get(0);
            Log.d("", "0번 주차장 ID : " + d.getID_ParkingLot());
            Log.d("", "이름 : " + d.getName_ParkingLot());
            Log.d("", "수용량 : " + d.getCapacity());
            Log.d("", "위도 : " + d.getLatitude());
            Log.d("", "경도 : " + d.getLongittude());
        }


        // 초기화가 한 번이라도 진행되었다는 것을 나타내기 위해 플레그를 세웁니다.
        isInit = true;
    }

    // 주어진 위도 경도로부터 주어진 거리 보다 가까운 주차장들의 리스트만 반환하는 메소드 입니다.
    public static ArrayList<ParkingLot> getParkinglotInRange(LatLng ll, int LimitMeter){
        return getParkinglotInRange(ll.latitude, ll.longitude, LimitMeter);
    }

    public static ArrayList<ParkingLot> getParkinglotInRange(double InputLatitude, double InputLongitude, int LimitMeter){
        ArrayList<ParkingLot> Result = new ArrayList<ParkingLot>();

        for(int i = 0; i < List_ParkingLot.size(); i++) {
            ParkingLot parkinglot = List_ParkingLot.get(i);
            double distanceMeter = distance(
                    InputLatitude,
                    InputLongitude,
                    Double.parseDouble(parkinglot.getLatitude()),
                    Double.parseDouble(parkinglot.getLongittude()),
                    "meter");
            if(distanceMeter <= LimitMeter) {
                Result.add(parkinglot);
            }
        }
        return Result;
    }

    /**
     * 두 지점간의 거리 계산
     *
     * @param lat1 지점 1 위도
     * @param lon1 지점 1 경도
     * @param lat2 지점 2 위도
     * @param lon2 지점 2 경도
     * @param unit 거리 표출단위
     * @return
     * CODE FROM : https://fruitdev.tistory.com/189
     */
    private static double distance(double lat1, double lon1, double lat2, double lon2, String unit) {

        double theta = lon1 - lon2;
        double dist = Math.sin(deg2rad(lat1)) * Math.sin(deg2rad(lat2)) + Math.cos(deg2rad(lat1)) * Math.cos(deg2rad(lat2)) * Math.cos(deg2rad(theta));

        dist = Math.acos(dist);
        dist = rad2deg(dist);
        dist = dist * 60 * 1.1515;

        if (unit == "kilometer") {
            dist = dist * 1.609344;
        } else if(unit == "meter"){
            dist = dist * 1609.344;
        }

        return (dist);
    }
    // This function converts decimal degrees to radians
    private static double deg2rad(double deg) {
        return (deg * Math.PI / 180.0);
    }

    // This function converts radians to decimal degrees
    private static double rad2deg(double rad) {
        return (rad * 180 / Math.PI);
    }


    // 비동기 작업을 위한 AsyncTask<String, Boolean, String> 코드들
    // onPreExecute : 비동기 선행 준비 메소드
    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    // doInBackground : 시작되면 백그라운드에서 실행될 메소드
    @Override
    protected String doInBackground(String... urls) {
        // 하드코딩 데이터 사용 시
        if(!useFireBaseDB){
            publishProgress(0);
            DataManager.Init_DB(false, this.doPrintDebug);
            publishProgress(1);
        }

        // FireBase DB 사용 시
        else{
            // DB 상태 채크
            FireBaseStatus = FireBase.getStatus();
            while(true){
                try{
                    // DB 상태가 로딩될 때 까지 대기
                    Log.d("","DB 상태 확인 중 . . .");
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                // DB 상태가 로딩되면 break
                if(FireBaseStatus != null && FireBaseStatus.Code != null && FireBaseStatus.Message != null) {
                    PrintFirebaseStatus();
                    break;
                }
            }

            // DB가 정상상태(0) 일 경우
            if(FireBaseStatus.Code.equals("0")){
                publishProgress(0);
                DataManager.Init_DB(true, this.doPrintDebug);
                while(true){
                    try {
                        // 주차장리스트를 전부 불러올 때 까지 대기
                        Log.d("","DB 다운로드 중 . . . ");
                        Thread.sleep(500);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    if(DataManager.List_ParkingLot.size() > 0){
                        publishProgress(1);
                        break;
                    }
                }
            }
            else {
                publishProgress(null);
            }
        }
        return "";
    }

    // onPostExecute : 초기화가 정상적으로 완료됐을 시
    @Override
    protected void onPostExecute(String res) {
    }

    // 중간중간 진행상황을 UI에 업데이트 해주는 메소드
    @Override
    protected void onProgressUpdate(Integer... value){
        if(value != null && value.length == 1){
            // 매게변수가 0이면 DB로부터 데이터 불러오는 중
            if(value[0] == 0){
                la.Init_DBFetching();
            }
            // 매게변수가 1이면 로딩 완료
            else if(value[0] == 1){
                la.Init_OK();
            }
            else {
                Log.e("", "Exception");
            }
        }
        // FirebaseStatus 코드가 1이면 점검중
        else if(FireBaseStatus.Code.equals("1")){
            la.Init_Maintance(FireBaseStatus.Message);
        }
        // 기타 예외 상황 발생 시
        else{
            la.Init_ExceptionError(FireBaseStatus);
        }
    }


    // 로그에 FireBaseStatus 객체 찍어보는 메소드
    private void PrintFirebaseStatus(){
        if(doPrintDebug){
            Log.d("", "Firebase Status Code : " + FireBaseStatus.Code);
            Log.d("", "Firebase Status Message : " + FireBaseStatus.Message);
        }
    }
}




class FireBase {

    public static FireBaseStatus getStatus() {
        final FireBaseStatus res = new FireBaseStatus();
        DatabaseReference StatusDB = FirebaseDatabase.getInstance().getReference().child("status");
        StatusDB.addListenerForSingleValueEvent(
                new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                HashMap<String, String> hmap = (HashMap<String, String>) dataSnapshot.getValue();
                res.Code = hmap.get("code");
                res.Message = hmap.get("message");
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e("","Error while reading data");
            }
        });
        return res;
    }

    // Firebase DB로부터 주차장 리스트를 받아오는 메소드
    public static ArrayList<ParkingLot> getData() {
        final ArrayList<ParkingLot> Result = new ArrayList<ParkingLot>();
        DatabaseReference fBaseDB = FirebaseDatabase.getInstance().getReference();

        fBaseDB.child("parkinglotV2").addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        // Foreach라서 반복 횟수 추적을 위해 int형을 하나 추가합니다.
                        int i = 0;

                        // 파이어베이스로부터 데이터들을 받아와 반복문을 돌립니다.
                        for (DataSnapshot ss : dataSnapshot.getChildren()) {
                            // 예외 탐지를 위한 try - catch 문
                            try {
                                // 데이터 하나를 받아옵니다.
                                ParkingLot data = ss.getValue(ParkingLot.class);
                                // 데이터가 문제가 없다면 ArrayList에 넣습니다.
                                if(!data.hasMissingData())
                                    Result.add(data);
                            } catch (Exception e) {
                                // 예외가 발생하면 로그와 스택트레이스를 띄웁니다.
                                Log.e("", "예외 발생 (인덱스 : " + i + " )");
                                e.printStackTrace();
                            }
                            i++;
                        }

                        // 반복문이 끝나면 불러온 데이터를 확인하기 위해 로그를 찍어봅니다.
                        Log.d("", "데이터 다운로드 완료 ! (" + i + " 번 의 요청 전송됨)");
                        Log.d("", "파이어베이스로부터 " + DataManager.List_ParkingLot.size() + "개의 주차장 데이터 로드됨");
                    }
                    // 예외처리
                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Log.e("", "getUser:onCancelled", databaseError.toException());
                    }
                });
        return Result;
    }

}

// FavoriteDB 객체
class FavoriteDB{
    String parkingID="";
    String parkingName="";
}

// 주차장 데이터 객체
class ParkingLot {

    public boolean hasMissingData() {
        // 기타 추가할 필터가 있으면 if문을 추가하고 걸러지게 하고 싶다면 true를 반환하면 됩니다.

        // 위도 경도 중 하나라도 공백이면 true 반환
        return this.getLatitude().equals("") || this.getLongittude().equals("");
    }

    // Q : 어째서 ~~ 자료형은 ~~ 이 적절한데 String 인가요?
    // Q ex) 어째서 주차 구휙수(Capacity) 는 int형이 적절한데 String 형 인가요?
    //
    // A : 파이어베이스에서 자료형을 생성할 때 자료형이 문자열인 경우 형 변환을 시도합니다.
    //       근데 공백 ""을 형 변환 시도하면 예외가 발생합니다.
    //       try catch를 통해 "" 이면 0으로 저장 등의 예외처리를 하려 했으나 불가능하였습니다.
    //
    //       따라서 일단 String 형으로 전부 받도록 해놨습니다.
    //       자료형 예외처리를 하기 위해서는 별도의 메소드를 추가하거나,
    //       hasMissingData()에 필터링 규칙을 넣으세요.

    //	주차장관리번호
    private String ID_ParkingLot;
    //	주차장명
    private String Name_ParkingLot;
    //	주차장구분
    private String Type_Operate;
    //	주차장유형
    private String Type_Parkinglot;
    //	소재지도로명주소
    private String Address_new;
    //	소재지지번주소
    private String Address_old;
    //	주차구획수 - int
    private String Capacity;
    //	급지구분 - int
    private String Type_Land;
    //	부제시행구분
    private String Type_Subtitle;
    //	운영요일
    private String Operate_Days;
    //	평일운영시작시각
    private String Time_Open_Weekly;
    //	평일운영종료시각
    private String Time_Close_Weekly;
    //	토요일운영시작시각
    private String Time_Open_Sat;
    //	토요일운영종료시각
    private String Time_Close_Sat;
    //	공휴일운영시작시각
    private String Time_Open_Weekend;
    //	공휴일운영종료시각
    private String Time_Close_Weekend;
    //	요금정보
    private String Type_Cost;
    //	주차기본시간
    private String Time_Basic;
    //	주차기본요금
    private String Cost_Basic;
    //	추가단위시간
    private String Time_Additional;
    //	추가단위요금
    private String Cost_Additional;
    //	1일주차권요금적용시간
    private String Time_Oneday;
    //	1일주차권요금
    private String Cost_Oneday;
    //	월정기권요금
    private String Cost_Monthly;
    //	결제방법
    private String Type_Payment;
    //	특기사항
    private String etc;
    //	관리기관명
    private String Name_Manager;
    //	전화번호
    private String Tel;
    //	위도 - double
    private String Latitude;
    //	경도 - double
    private String Longittude;
    //	데이터기준일자
    private String RegistratedDate;
    //	제공기관코드
    private String ID_Offer;
    //	제공기관명
    private String Name_Offer;

    public String getID_ParkingLot() {
        return ID_ParkingLot;
    }

    public String getName_ParkingLot() {
        return Name_ParkingLot;
    }

    public String getType_Operate() {
        return Type_Operate;
    }

    public String getType_Parkinglot() {
        return Type_Parkinglot;
    }

    public String getAddress_new() {
        return Address_new;
    }

    public String getAddress_old() {
        return Address_old;
    }

    // int
    public String getCapacity() {
        return Capacity;
    }

    // int
    public String getType_Land() {
        return Type_Land;
    }

    public String getType_Subtitle() {
        return Type_Subtitle;
    }

    public String getOperate_Days() {
        return Operate_Days;
    }

    public String getTime_Open_Weekly() {
        return Time_Open_Weekly;
    }

    public String getTime_Close_Weekly() {
        return Time_Close_Weekly;
    }

    public String getTime_Open_Sat() {
        return Time_Open_Sat;
    }

    public String getTime_Close_Sat() {
        return Time_Close_Sat;
    }

    public String getTime_Open_Weekend() {
        return Time_Open_Weekend;
    }

    public String getTime_Close_Weekend() {
        return Time_Close_Weekend;
    }

    public String getType_Cost() {
        return Type_Cost;
    }

    public String getTime_Basic() {
        return Time_Basic;
    }

    public String getCost_Basic() {
        return Cost_Basic;
    }

    public String getTime_Additional() {
        return Time_Additional;
    }

    public String getCost_Additional() {
        return Cost_Additional;
    }

    public String getTime_Oneday() {
        return Time_Oneday;
    }

    public String getCost_Oneday() {
        return Cost_Oneday;
    }

    public String getCost_Monthly() {
        return Cost_Monthly;
    }

    public String getType_Payment() {
        return Type_Payment;
    }

    public String getEtc() {
        return etc;
    }

    public String getName_Manager() {
        return Name_Manager;
    }

    public String getTel() {
        return Tel;
    }

    // double
    public String getLatitude() {
        return Latitude;
    }

    // double
    public String getLongittude() {
        return Longittude;
    }

    public String getRegistratedDate() {
        return RegistratedDate;
    }

    public String getID_Offer() {
        return ID_Offer;
    }

    public String getName_Offer() {
        return Name_Offer;
    }

    // 기본생성자
    // 이게 있어야 Firebase 라이브러리가 자료형을 생성할 수 있습니다.
    // 없으면 밑의 오류가 발생합니다.
    // com.google.firebase.database.DatabaseException:
    //     Class com.example.capston_park_application.ParkingLot does not define a no-argument constructor.
    //     If you are using ProGuard, make sure these constructors are not stripped.
    public ParkingLot(){ }

    // 수동 생성자
    public ParkingLot(
            String ID_ParkingLot,
            String Name_ParkingLot,
            String Type_Operate,
            String Type_Parkinglot,
            String Address_new,
            String Address_old,
            String Capacity,
            String Type_Land,
            String Type_Subtitle,
            String Operate_Days,
            String Time_Open_Weekly,
            String Time_Close_Weekly,
            String Time_Open_Sat,
            String Time_Close_Sat,
            String Time_Open_Weekend,
            String Time_Close_Weekend,
            String Type_Cost,
            String Time_Basic,
            String Cost_Basic,
            String Time_Additional,
            String Cost_Additional,
            String Time_Oneday,
            String Cost_Oneday,
            String Cost_Monthly,
            String Type_Payment,
            String etc,
            String Name_Manager,
            String Tel,
            String Latitude,
            String Longittude,
            String RegistratedDate,
            String ID_Offer,
            String Name_Offer) {

        this.ID_ParkingLot = ID_ParkingLot;
        this.Name_ParkingLot = Name_ParkingLot;
        this.Type_Operate = Type_Operate;
        this.Type_Parkinglot = Type_Parkinglot;
        this.Address_new = Address_new;
        this.Address_old = Address_old;
        this.Capacity = Capacity;
        this.Type_Land = Type_Land;
        this.Type_Subtitle = Type_Subtitle;
        this.Operate_Days = Operate_Days;
        this.Time_Open_Weekly = Time_Open_Weekly;
        this.Time_Close_Weekly = Time_Close_Weekly;
        this.Time_Open_Sat = Time_Open_Sat;
        this.Time_Close_Sat = Time_Close_Sat;
        this.Time_Open_Weekend = Time_Open_Weekend;
        this.Time_Close_Weekend = Time_Close_Weekend;
        this.Type_Cost = Type_Cost;
        this.Time_Basic = Time_Basic;
        this.Cost_Basic = Cost_Basic;
        this.Time_Additional = Time_Additional;
        this.Cost_Additional = Cost_Additional;
        this.Time_Oneday = Time_Oneday;
        this.Cost_Oneday = Cost_Oneday;
        this.Cost_Monthly = Cost_Monthly;
        this.Type_Payment = Type_Payment;
        this.etc = etc;
        this.Name_Manager = Name_Manager;
        this.Tel = Tel;
        this.Latitude = Latitude;
        this.Longittude = Longittude;
        this.RegistratedDate = RegistratedDate;
        this.ID_Offer = ID_Offer;
        this.Name_Offer = Name_Offer;
    }

    // 하드코딩용 생성자
    public ParkingLot(
            String ID_ParkingLot,
            String Name_ParkingLot,
            String Address_new,
            String Address_old,
            String Capacity,
            String Tel,
            String Latitude,
            String Longittude) {

        this.ID_ParkingLot = ID_ParkingLot;
        this.Name_ParkingLot = Name_ParkingLot;
        this.Type_Operate = "공영";
        this.Type_Parkinglot = "노외";
        this.Address_new = Address_new;
        this.Address_old = Address_old;
        this.Capacity = Capacity;
        this.Type_Land = "기타";
        this.Type_Subtitle = "미시행";
        this.Operate_Days = "평일";
        this.Time_Open_Weekly = "99:99";
        this.Time_Close_Weekly = "99:99";
        this.Time_Open_Sat = "99:99";
        this.Time_Close_Sat = "99:99";
        this.Time_Open_Weekend = "99:99";
        this.Time_Close_Weekend = "99:99";
        this.Type_Cost = "유료";
        this.Time_Basic = "100";
        this.Cost_Basic = "777";
        this.Time_Additional = "200";
        this.Cost_Additional = "778";
        this.Time_Oneday = "150";
        this.Cost_Oneday = "175";
        this.Cost_Monthly = "779";
        this.Type_Payment = "카드결제";
        this.etc = "기타";
        this.Name_Manager = "하드코딩 데이터";
        this.Tel = Tel;
        this.Latitude = Latitude;
        this.Longittude = Longittude;
        this.RegistratedDate = "2020-11-04";
        this.ID_Offer = "123456789";
        this.Name_Offer = "QyuBot";
    }


}

class FireBaseStatus {
    String Code;
    String Message;

    public FireBaseStatus(){

    }

    public FireBaseStatus(String code, String msg){
        this.Code = code;
        this.Message = msg;
    }

}