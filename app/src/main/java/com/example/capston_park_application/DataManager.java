package com.example.capston_park_application;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;

// 앱이 사용하는 데이터들을 다루는 DataManager를 작성하는 곳 입니다.

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
            FavoriteDB f_data = new FavoriteDB();
            f_data.parkingID = cursor.getString(cursor.getColumnIndex("ParkingLot_id"));
            f_data.parkingName = cursor.getString(cursor.getColumnIndex("ParkingLot_name"));
            list.add(f_data);
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
            Log.d("","즐겨찾기 요소 추가 성공. [이름 : " + parkingName + "]");
        }else{
            Log.e("","즐겨찾기 요소 추가 실패! [이름 : " + parkingName + "]");
        }
        db.close();
    }

    // 즐겨찾기인가?
    public static boolean isFavorite(String parkingID){
        ArrayList<FavoriteDB> list = ReadFavoriteList();
        for(FavoriteDB f_data : list){
            if(f_data.parkingID.equals(parkingID)){
                return true;
            }
        }
        return false;
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
    public static void UpdateSearchScope(int distance){
        String dis = Integer.toString(distance);
        ScopeDBHelper = new ParkingLotDBHelper(nowContext, scopeDBName);
        db =  ScopeDBHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("Scope_distance", dis);


        db.execSQL(ScopeDBSQL.DATA_UPDATE(dis));
        String dist = DataManager.ReadSearchScope();
        Log.d("DataManager","거리값 : " + dist);
        //아래 주석 지우지 마세요.
//        int result = db.update(scopeDBName, values, "Scope_distance=?", new String[]{dis});
//        if(result != 1){
//            Log.e("DataManager","UpdateSearchScope : 거리 설정 업데이트 실패" );
//            String dist = DataManager.ReadSearchScope();
//            Log.e("DataManager","거리값 : " + dist);
//        }else{
//            Log.d("DataManager","거리 설정 업데이트 성공" );
//        }
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
                    "37.402111",
                    "127.108640"));

            List_ParkingLot.add(new ParkingLot(
                    "00000002",
                    "한남대학교",
                    "대전 대덕구 한남로 70 한남대학교",
                    "대전 대덕구 오정동 133",
                    "300",
                    "042-1234-5678",
                    "36.35464605201017",
                    "127.42108643668588"));

            List_ParkingLot.add(new ParkingLot(
                    "00000003",
                    "대전신학대학교",
                    "대전 대덕구 한남로 70 한남대학교",
                    "대전 대덕구 오정동 133",
                    "300",
                    "042-1234-5678",
                    "36.35050334345364",
                    "127.42356233636478"));

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

    // 주차장 ID로 주차장 객체 얻기
    public static ParkingLot getParkingLotbyID(String ID){
        for(ParkingLot pl : List_ParkingLot){
            if(pl.getID_ParkingLot().equals(ID)){
                return pl;
            }
        }
        Log.e("DataManager", "널 리턴 발생");
        Log.e("DataManager", "from asParkingLotbyID, arg(ID) : " + ID);
        return null;
    }

    // 주차장 이름으로 주차장 객체 얻기
    public static ParkingLot getParkingLotbyName(String name){
        for(ParkingLot pl : List_ParkingLot){
            if(pl.getName_ParkingLot().equals(name)){
                return pl;
            }
        }
        Log.e("DataManager", "널 리턴 발생");
        Log.e("DataManager", "from asParkingLotbyName, arg(Name) : " + name);
        return null;
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