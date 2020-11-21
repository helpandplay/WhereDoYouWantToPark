package com.example.capston_park_application;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle; //String을 쓰기위함
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

//구글 지도 import
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import com.google.android.gms.tasks.OnSuccessListener;

public class MapActivity extends AppCompatActivity implements OnMapReadyCallback {
    public static Context context_MainScreen;
    SearchView searchview;
    View option_drawerView, option_drawerlayout, favorite_drawerView, favorite_drawerlayout, search_layout, parkinglot_layout;
    ImageButton option_open, option_close, favorite_open, location , zoomin, zoomout;
    Button favorite_close;
    RecyclerView Favorite_RecyclerView;

    Marker selectedMarker;
    View marker_root_view;
    TextView tv_marker;

    private GoogleMap mMap;
    private FusedLocationProviderClient fusedLocationClient;
    private int ViewDistance = 2500;
    private boolean mLocationPermission = false;
    private final int Location_Permission = 1;
    @Override
    protected void onCreate(Bundle savedInstanceState) { //savedInstanceState = 세로, 가로 화면변경시 전역변수 초기화를 방지
        context_MainScreen = this;
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        final ImageButton mylocation = (ImageButton)findViewById(R.id.mylocation);
        zoomin = findViewById(R.id.zoomin);
        zoomout = findViewById(R.id.zoomout);
        option_open = findViewById(R.id.btn_option_open);
        option_close = findViewById(R.id.btn_option_close);
        favorite_open = findViewById(R.id.btn_favorite_open);
        favorite_close = findViewById(R.id.btn_favorite_close);

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        // DB 를 사용하기 위해 DataManager의 Context 설정
        DataManager.setContext(this.getBaseContext());

        // 핀 표시 거리 변경
        ViewDistance = Integer.parseInt(DataManager.ReadSearchScope());

        //주소 검색기능
        final InputMethodManager tmpkeyboard = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        searchview = findViewById(R.id.searchBar);
        //EditText searchEditText = searchview.findViewById(androidx.appcompat.R.id.search);
        int searchIcon = searchview.getContext().getResources().getIdentifier("android:id/search_mag_icon", null, null);
        int textcolor = searchview.getContext().getResources().getIdentifier("android:id/search_src_text", null, null);
        int searchclose = searchview.getContext().getResources().getIdentifier("android:id/search_close_btn", null, null);
        TextView searchtext = searchview.findViewById(textcolor);
        ImageView magIcon = searchview.findViewById(searchIcon);
        ImageView cls_btn = searchview.findViewById(searchclose);
        magIcon.setColorFilter(Color.BLACK);
        cls_btn.setColorFilter(Color.BLACK);
        searchtext.setTextColor(Color.BLACK);
        searchtext.setTextSize(17);
        searchtext.setHint("주소를 검색해주세요!");
        searchtext.setHintTextColor(Color.GRAY);
        searchview.setFocusable(false);
        searchview.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                searchview.onActionViewExpanded();
            }
        });
        searchview.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                String searchlocation = searchview.getQuery().toString();
                Address address;
                List<Address> addressList = null;
                if(searchlocation != null || !searchlocation.equals("")){
                    Geocoder geocoder = new Geocoder(MapActivity.this);
                    try{
                        addressList = geocoder.getFromLocationName(searchlocation, 1);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    if(addressList != null){
                        if(addressList.size()==0) { //주소 검색을 잘못해 geocoder로 부터 값을 전달받지 못했을경우 ex) seoul 이 아닌 soeul 등
                            Toast locationToast = Toast.makeText(getApplicationContext(), "해당 주소는 존재하지 않습니다", Toast.LENGTH_SHORT);
                            locationToast.show();
                            searchview.setQuery("", false);
                            searchview.clearFocus();
                        }
                        else { //정상적인 주소검색으로 geocoder로 부터 값을 받을경우
                            address = addressList.get(0);
                            LatLng latLng = new LatLng(address.getLatitude(),address.getLongitude());
                            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng,16));//숫자값 높으면 줌인 낮으면 줌아웃 1~21
                            searchview.setQuery("", false);
                            searchview.clearFocus();
                        }
                    }
                }
                return false;
            }
            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
        cls_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tmpkeyboard.hideSoftInputFromWindow(searchview.getWindowToken(), 0);
                searchview.setQuery("", false);
                searchview.clearFocus();
            }
        });
        //카메라 줌인 줌아웃//
        zoomin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mMap.animateCamera(CameraUpdateFactory.zoomIn());
            }
        });
        zoomout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mMap.animateCamera(CameraUpdateFactory.zoomOut());
            }
        });
        ////////////////////
        final Animation translateDown = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.translate_down);
        final Animation translateLeft = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.translate_left);
        final Animation translateRight = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.translate_right);

        parkinglot_layout = (View)findViewById(R.id.parkinglot_layout);
        option_drawerView = (View)findViewById(R.id.option_drawer);
        option_drawerlayout = (View)findViewById(R.id.option_drawer_layout);
        favorite_drawerView = (View)findViewById(R.id.favorite_drawer);
        favorite_drawerlayout = (View)findViewById(R.id.favorite_drawer_layout);
        search_layout = (View)findViewById(R.id.relative);

        // 즐겨찾기 리스트 새로고침
        Favorite_RecyclerView = (RecyclerView) findViewById(R.id.RecyclerView_Favorite);
        RefreshFavorite();


        ///////////////////옵션, 즐겨찾기 클릭시 드로워 이벤트 들///////////////////
        option_open.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //옵션 버튼 open 클릭시 옵션창을 제외한 모든 창 안보이게 설정
                option_drawerView.bringToFront();
                option_drawerView.setVisibility(View.VISIBLE);
                search_layout.setVisibility(View.INVISIBLE);
                parkinglot_layout.setVisibility(View.INVISIBLE);
                mylocation.setVisibility(View.INVISIBLE);
                zoomin.setVisibility(View.INVISIBLE);
                zoomout.setVisibility(View.INVISIBLE);
                option_drawerView.startAnimation(translateLeft);
                //창이 나오면 터치이벤트가 뒤에있는 레이아웃에게 전달안되게 하는 함수//
                option_drawerView.setOnTouchListener(new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(View v, MotionEvent event) {
                        return true;
                    }
                });

                Button Btn_add = findViewById(R.id.Option_BTN_Add);
                Button Btn_dec = findViewById(R.id.Option_BTN_Dec);
                final TextView tv = findViewById(R.id.Option_TextView_nowDistance);

                tv.setText(ViewDistance + "m");

                Btn_add.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v){
                        if(ViewDistance >= 5000){
                            Toast.makeText(MapActivity.this, "더 이상 거리를 늘릴 수 없습니다.", Toast.LENGTH_SHORT).show();
                        }
                        else{
                            ViewDistance += 200;
                            tv.setText(ViewDistance + "m");
                            DataManager.UpdateSearchScope(ViewDistance);
                            LatLng mPosition = mMap.getCameraPosition().target;
                            MarkerGenerator(DataManager.getParkinglotInRange(mPosition, ViewDistance));
                        }
                    }});

                Btn_dec.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v){
                        if(ViewDistance <= 200){
                            Toast.makeText(MapActivity.this, "더 이상 거리를 줄일 수 없습니다.", Toast.LENGTH_SHORT).show();
                            LatLng mPosition = mMap.getCameraPosition().target;
                            MarkerGenerator(DataManager.getParkinglotInRange(mPosition, ViewDistance));
                        }
                        else{
                            ViewDistance -= 200;
                            tv.setText(ViewDistance + "m");
                            DataManager.UpdateSearchScope(ViewDistance);
                        }
                    }});
            }
        });
        option_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //옵션 버튼 close 클릭시 안보이던 창 보이게
                option_drawerView.startAnimation(translateRight);
                option_drawerView.setVisibility(View.GONE);
                search_layout.setVisibility(View.VISIBLE);
                mylocation.setVisibility(View.VISIBLE);
                zoomin.setVisibility(View.VISIBLE);
                zoomout.setVisibility(View.VISIBLE);
            }
        });

        favorite_open.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RefreshFavorite();
                //즐겨찾기 버튼 open 클릭시 옵션창을 제외한 모든 창 안보이게
                favorite_drawerView.bringToFront();
                favorite_drawerView.setVisibility(View.VISIBLE);
                search_layout.setVisibility(View.INVISIBLE);
                parkinglot_layout.setVisibility(View.INVISIBLE);
                mylocation.setVisibility(View.INVISIBLE);
                zoomin.setVisibility(View.INVISIBLE);
                zoomout.setVisibility(View.INVISIBLE);
                favorite_drawerView.startAnimation(translateLeft);
                //창이 나오면 터치이벤트가 뒤에있는 레이아웃에게 전달안되게 하는 함수//
                favorite_drawerView.setOnTouchListener(new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(View v, MotionEvent event) {
                        return true;
                    }
                });


            }
        });
        favorite_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //즐겨찾기기 버튼 close 클릭시 안보던 창 보이게
                favorite_drawerView.setVisibility(View.GONE);
                search_layout.setVisibility(View.VISIBLE);
                mylocation.setVisibility(View.VISIBLE);
                zoomin.setVisibility(View.VISIBLE);
                zoomout.setVisibility(View.VISIBLE);
                favorite_drawerView.startAnimation(translateRight);
            }
        });
        // gps버튼 클릭시 내 위치로 부드럽게 이동
        mylocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkLocationPermission()) {
                    fusedLocationClient.getLastLocation()
                            .addOnSuccessListener(MapActivity.this, new OnSuccessListener<Location>() {
                                @Override
                                public void onSuccess(Location mylocation) {
                                    if (mylocation != null) {
                                        mMap.animateCamera(CameraUpdateFactory.newLatLng(new LatLng(mylocation.getLatitude(),mylocation.getLongitude())));
                                        if (parkinglot_layout.getVisibility() == View.VISIBLE) {
                                            parkinglot_layout.setVisibility(View.INVISIBLE);
                                            parkinglot_layout.startAnimation(translateDown);
                                        }
                                    }
                                }
                            });
                }
            }
        });
        MapFragment mapFragment = (MapFragment) getFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        // 지도가 생성될 때 위치권한 물어보기
        // 권한설정이 되 있을 시 현재 위치를 표시해 줌
        // 위치권한이 없을 때 토스트로 메세지 출력
        getLocationPermission();
        MarkerClickEvent();

        // 카메라 이동 후 정지 시 발생하는 이벤트 리스너
        mMap.setOnCameraIdleListener(new GoogleMap.OnCameraIdleListener() {
            @Override
            public void onCameraIdle() {
                // Cleaning all the markers.
                if (mMap != null) {
                    mMap.clear();
                }
                LatLng mPosition = mMap.getCameraPosition().target;
                MarkerGenerator(DataManager.getParkinglotInRange(mPosition, ViewDistance));
            }
        });

        setCustomMarkerView();
    }
    private void setCustomMarkerView() {
        marker_root_view = LayoutInflater.from(this).inflate(R.layout.marker_layout, null);
        tv_marker = marker_root_view.findViewById(R.id.tv_marker);
    }

    // 마커 클릭 이벤트 추가
    public void MarkerClickEvent(){
        // 앱 처음 실행시 화면 이동할 좌표(한남대)
        LatLng latLng = new LatLng(36.355422, 127.421316);
        // mMap.addMarker(new MarkerOptions().position(latLng).title("한남대"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
        mMap.moveCamera(CameraUpdateFactory.zoomTo(16));

        final Animation translateUp = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.translate_up);
        final Animation translateDown = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.translate_down);

        final TextView parkinglot_name = findViewById(R.id.parkinglot_name);
        final TextView parkinglot_address_new = findViewById(R.id.parkinglot_address_new);
        final TextView parkinglot_address_old = findViewById(R.id.parkinglot_address_old);
        final TextView parkinglot_weekly = findViewById(R.id.parkinglot_weekly);
        final TextView parkinglot_sat = findViewById(R.id.parkinglot_sat);
        final TextView parkinglot_weekend = findViewById(R.id.parkinglot_weekend);
        final TextView parkinglot_weekly_time = findViewById(R.id.parkinglot_weekly_time);
        final TextView parkinglot_weekly_time_close = findViewById(R.id.parkinglot_weekly_time_close);
        final TextView parkinglot_sat_time = findViewById(R.id.parkinglot_sat_time);
        final TextView parkinglot_sat_time_close = findViewById(R.id.parkinglot_sat_time_close);
        final TextView parkinglot_weekend_time = findViewById(R.id.parkinglot_weekend_time);
        final TextView parkinglot_weekend_time_close = findViewById(R.id.parkinglot_weekend_time_close);
        final TextView parkinglot_capacity = findViewById(R.id.parkinglot_capacity);
        final ImageView parkinglot_favoriteimage = findViewById(R.id.parkignlot_favorite_button);
        final ImageButton parkinglot_P_Button = findViewById(R.id.goto_kakaomap);

        final Button closebutton = findViewById(R.id.tableclose);

        ///////////////////마커 클릭시 이벤트처리///////////////////
        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                // 마커의 이름(주차장 이름)을 가져오고
                String title = marker.getTitle();
                // 주차장 객체 하나 만들고
                ParkingLot pl = null;
                // 무식하게 for문돌려서 리스트에서 주차장 이름으로 주차장 객체를 찾는다.
                for(ParkingLot t : DataManager.List_ParkingLot){
                    if(t.getName_ParkingLot().equals(title)){
                        pl = t;
                        break;
                    }
                }
                // 정보 넣기, 예외설정 필요

                ////////임시로 만듬/////////
                String operate_days = pl.getOperate_Days();
                if(operate_days.contains("평일")){
                    parkinglot_weekly.setText("평일");
                    parkinglot_weekly_time.setText(pl.getTime_Open_Weekly());
                    parkinglot_weekly_time_close.setText("~  " + pl.getTime_Close_Weekly());
                    if(TextUtils.isEmpty(pl.getTime_Close_Weekly())){
                        parkinglot_weekly_time_close.setText("~");
                    }
                }

                if(operate_days.contains("토요일")){
                    parkinglot_sat.setText("토요일");
                    parkinglot_sat_time.setText(pl.getTime_Open_Sat());
                    parkinglot_sat_time_close.setText("~  " + pl.getTime_Close_Sat());
                }

                if(operate_days.contains("공휴일")){
                    parkinglot_weekend.setText("공휴일");
                    parkinglot_weekend_time.setText(pl.getTime_Open_Weekend());
                    parkinglot_weekend_time_close.setText("~  " + pl.getTime_Close_Weekend());
                }

                parkinglot_name.setText(pl.getName_ParkingLot());
                parkinglot_address_old.setText(pl.getAddress_old());
                parkinglot_address_new.setText(pl.getAddress_new());
                parkinglot_capacity.setText(pl.getCapacity());

                String temp_address_new = pl.getAddress_new();
                String temp_address_old = pl.getAddress_old();

                //신주소 정보 없을때
                if(TextUtils.isEmpty(temp_address_new)){
                    parkinglot_address_new.setText("정보없음");
                }
                //구주소 정보 없을때
                if(TextUtils.isEmpty(temp_address_old)){
                    parkinglot_address_new.setText("정보없음");
                }

                // 주차장 상세정보의 즐겨찾기 아이콘 관련 처리
                if(!DataManager.isFavorite(pl.getID_ParkingLot())){
                    parkinglot_favoriteimage.setImageResource(R.drawable.unfavorite);
                }
                else{
                    parkinglot_favoriteimage.setImageResource(R.drawable.favorite_bright);
                }

                final ParkingLot finalPl = pl;
                parkinglot_favoriteimage.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        // 즐겨찾기가 아닌 경우
                        if(!DataManager.isFavorite(finalPl.getID_ParkingLot())){
                            // DB에 즐겨찾기 추가
                            DataManager.insertFavoriteElement(finalPl.getID_ParkingLot(), finalPl.getName_ParkingLot());
                            // 별을 빛나게
                            parkinglot_favoriteimage.setImageResource(R.drawable.favorite_bright);
                            Toast.makeText(MapActivity.this, "주차장 " + finalPl.getName_ParkingLot() + " 을 즐겨찾기에 추가하였습니다", Toast.LENGTH_LONG).show();

                        }
                        // 즐겨찾기인 경우
                        else{
                            // DB에서 제거
                            DataManager.deleteFavoriteElement(finalPl.getName_ParkingLot());
                            // 별 불끄기
                            parkinglot_favoriteimage.setImageResource(R.drawable.unfavorite);
                            Toast.makeText(MapActivity.this, "주차장 " + finalPl.getName_ParkingLot() + "을  즐겨찾기에서 삭제하였습니다", Toast.LENGTH_LONG).show();
                        }

                    }
                });

                parkinglot_P_Button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {   //주차장 디테일 버튼 close 클릭시
                        // TODO : 길찾기 아이콘(주차장 상세 정보 열리면 즐겨찾기 별 옆에 있는 하얀 동그라미에 검은색 P) onClick 이벤트 만들기

                    }
                });


                parkinglot_layout.setVisibility(View.VISIBLE);
                parkinglot_layout.startAnimation(translateUp);

                closebutton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {   //주차장 디테일 버튼 close 클릭시
                        parkinglot_layout.setVisibility(View.INVISIBLE);
                        parkinglot_layout.startAnimation(translateDown);

                        // 닫으면 즐겨찾기 리스트 갱신
                        RefreshFavorite();
                    }
                });
                return false;
            }
        });
    }
    // 리스트로부터 지도 핀 생성하는 메소드
    private void MarkerGenerator(ArrayList<ParkingLot> list){
        for(ParkingLot pl : list){
            MarkerOptions mo = new MarkerOptions();
            mo.position(new LatLng(
                    Double.parseDouble(pl.getLatitude()),
                    Double.parseDouble(pl.getLongittude()))).
                    title(pl.getName_ParkingLot());
            mo.icon(BitmapDescriptorFactory.fromBitmap(createDrawableFromView(this, marker_root_view))); // 커스텀 핀 아이콘으로 변경
            mMap.addMarker(mo);
            if(pl.getCost_Basic().equals("0")) {
                tv_marker.setText("무료");
            }
            else if(pl.getCost_Basic().isEmpty()){
                tv_marker.setText("정보없음");
            }
            else {
                tv_marker.setText("기본 요금\n" + pl.getCost_Basic() + "원");
            }
        }
    }

    // 커스텀핀 설정
    private Bitmap createDrawableFromView(Context context, View view) {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        ((Activity) context).getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        view.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        view.measure(displayMetrics.widthPixels, displayMetrics.heightPixels);
        view.layout(0, 0, displayMetrics.widthPixels, displayMetrics.heightPixels);
        view.buildDrawingCache();
        Bitmap bitmap = Bitmap.createBitmap(view.getMeasuredWidth(), view.getMeasuredHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        view.draw(canvas);
        return bitmap;
    }

    // 위치정보 권한 받기
    private void getLocationPermission() {
        if (ContextCompat.checkSelfPermission(this.getApplicationContext(),
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) { // 이미 권한 승인 되있을 때
            setMapUI(); // 내 위치 표시
            mLocationPermission = true;
        } else { // 권한이 설정되있지 않을 때 권한 물어보기
            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    Location_Permission);
        }
    }
    // requestPermissions 호출 후 권한을 모두 받았는지 확인
    // 추후 권한 추가 시 메소드 변경하여 사용가능
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String permissions[],
                                           @NonNull int[] grantResults) {
        mLocationPermission = false;
        switch (requestCode) {
            case Location_Permission : {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    setMapUI(); // 내 위치 표시
                    mLocationPermission = true;
                } else {
                    checkLocationPermission();
                }
                return;
            }
        }
    }
    // 내 위치 표시
    private void setMapUI(){
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            mMap.setMyLocationEnabled(true);
            mMap.isMyLocationEnabled();
            mMap.getUiSettings().setMyLocationButtonEnabled(false);
        }
    }

    // Location권한을 받지 못했을 때 권한이 없다고 Toast메세지 출력
    private boolean checkLocationPermission() {
        if (mLocationPermission){
            // 권한이 true 일때
        } else {
            // 권한이 false 일때
            Toast testToast = Toast.makeText(this.getApplicationContext(),
                    "위치권한이 없습니다.\n앱 재실행 후 위치권한설정 해주세요.",
                    Toast.LENGTH_SHORT);
            testToast.show();
        }
        return mLocationPermission;
    }

    // 화면 중앙 좌표 구하기
    private LatLng getCenterPosition() {
        return new LatLng(
                mMap.getCameraPosition().target.latitude,
                mMap.getCameraPosition().target.longitude
        );
    }

    // 즐겨찾기 리사이클러뷰 세팅
    private void RefreshFavorite(){
        ArrayList<FavoriteDB> flist = DataManager.ReadFavoriteList();
        Log.d("RefreshFavorite", "즐겨찾기 리스트 길이 : " + flist.size());

        // 리사이클러뷰에 LinearLayoutManager 객체 지정.
        Favorite_RecyclerView.setLayoutManager(new LinearLayoutManager(this)) ;

        // 리사이클러뷰에 SimpleTextAdapter 객체 지정.
        SimpleTextAdapter adapter = new SimpleTextAdapter(flist, null, this) ;
        Favorite_RecyclerView.setAdapter(adapter) ;
        Log.d("RefreshFavorite", "어뎁터 아이템 카운트 : " + adapter.getItemCount());


    }

    // 즐겨찾기 드로워 닫고 선택한 주차장 위치로 이동하는 메소드, Adapter Class에서 호출함
    public void closeFavoriteDrawer(ParkingLot pl){
        final Animation translateRight = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.translate_right);
        final ImageButton mylocation = (ImageButton)findViewById(R.id.mylocation);

        //즐겨찾기기 버튼 close 클릭시 안보던 창 보이게
        favorite_drawerView.setVisibility(View.GONE);
        search_layout.setVisibility(View.VISIBLE);
        mylocation.setVisibility(View.VISIBLE);
        zoomin.setVisibility(View.VISIBLE);
        zoomout.setVisibility(View.VISIBLE);
        favorite_drawerView.startAnimation(translateRight);

        // 카메라 이동하기
        mMap.animateCamera(CameraUpdateFactory.newLatLng(new LatLng(
                Double.parseDouble(pl.getLatitude()),
                Double.parseDouble(pl.getLongittude()))));
    }

}

// 리사이클러뷰 어뎁터
// 코드참고 : https://recipes4dev.tistory.com/154
class SimpleTextAdapter extends RecyclerView.Adapter<SimpleTextAdapter.ViewHolder> {

    private ArrayList<ParkingLot> mData = null ;
    private Activity fa;
    private MapActivity ma;

    // 아이템 뷰를 저장하는 뷰홀더 클래스.
    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView Name;
        TextView addrOld;
        TextView addrNew;
        ImageView Favorite;
        ImageButton Guide;

        boolean FavoriteFlag = true;

        ViewHolder(View itemView) {
            super(itemView) ;

            // 뷰 객체에 대한 참조. (hold strong reference)
            Name = itemView.findViewById(R.id.RecyclerView_Favorite_ParkinglotName) ;
            addrOld = itemView.findViewById(R.id.RecyclerView_Favorite_TextView_AddressOld) ;
            addrNew = itemView.findViewById(R.id.RecyclerView_Favorite_TextView_AddressNew) ;
            Favorite = itemView.findViewById(R.id.RecyclerView_Favorite_ImageButton_ToggleFavorite);
            Guide = itemView.findViewById(R.id.RecyclerView_Favorite_ImageButton_goKakaoMap);
        }

    }

    // 생성자에서 데이터 리스트 객체를 전달받음
    // Activity a는 아직 안씀(null 받아옴)
    // 창 닫히고 카메라가 이동하는 이벤트를 발생하기 위해 MapActivity를 가지고 있음
    SimpleTextAdapter(ArrayList<FavoriteDB> list, Activity a, MapActivity ma)
    {
        this.ma = ma;
        fa = a;

        mData = new ArrayList<ParkingLot>();
        for(FavoriteDB f_data : list){
            ParkingLot pl = DataManager.getParkingLotbyID(f_data.parkingID);
            if(pl == null){
                Log.e("SimpleTextAdapter", "DataManager.getParkingLotbyID 에서 널 리턴 발생, skipping . . .");
            }
            else{
                if(mData.contains(pl)){
                    Log.d("SimpleTextAdapter", pl.getName_ParkingLot() + " 는 이미 있음, skipping . . .");
                }
                else{
                    mData.add(pl);
                }
            }
        }

    }

    // onCreateViewHolder() - 아이템 뷰를 위한 뷰홀더 객체 생성하여 리턴.
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext() ;
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) ;

        View view = inflater.inflate(R.layout.favorite_recyclerview_item, parent, false) ;
        SimpleTextAdapter.ViewHolder vh = new SimpleTextAdapter.ViewHolder(view) ;

        return vh ;
    }

    // onBindViewHolder() - position에 해당하는 데이터를 뷰홀더의 아이템뷰에 표시.
    @Override
    public void onBindViewHolder(final SimpleTextAdapter.ViewHolder holder, int position) {

        holder.Name.setText(mData.get(position).getName_ParkingLot()) ;

        // 구주소 정보 없을때
        if(TextUtils.isEmpty(mData.get(position).getAddress_old())){
            holder.addrOld.setText("정보없음");
        }
        else{
            holder.addrOld.setText(mData.get(position).getAddress_old()) ;
        }

        // 신주소 정보 없을때
        if(TextUtils.isEmpty(mData.get(position).getAddress_new())){
            holder.addrNew.setText("정보없음");
        }
        else{
            holder.addrNew.setText(mData.get(position).getAddress_new()) ;
        }

        holder.Favorite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 즐겨찾기 버튼 눌렀을 때
                ParkingLot pl = DataManager.getParkingLotbyName((String) holder.Name.getText());
                if(holder.FavoriteFlag){
                    // 즐겨찾기 상태라면 -> DB에서 지우고 불끈다
                    holder.FavoriteFlag = false;
                    holder.Favorite.setImageResource(R.drawable.unfavorite);

                    DataManager.deleteFavoriteElement((String) holder.Name.getText());
                    Toast.makeText(ma, "주차장 " + pl.getName_ParkingLot() + " 삭제합니다.\n 즐겨찾기 창을 닫으면 삭제됩니다", Toast.LENGTH_SHORT).show();
                }
                else{
                    // 즐겨찾기 해제 상태라면 -> DB에 추가하고 불켠다
                    holder.FavoriteFlag = true;
                    holder.Favorite.setImageResource(R.drawable.favorite_bright);

                    DataManager.insertFavoriteElement(pl.getID_ParkingLot(), pl.getName_ParkingLot());
                    Toast.makeText(ma, "주차장 " + pl.getName_ParkingLot() + " 삭제하지 않습니다", Toast.LENGTH_SHORT).show();
                }
            }
        });

        holder.Guide.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 내 위치 아이콘 눌렀을 때 실행할 이벤트
                ParkingLot pl = DataManager.getParkingLotbyName((String) holder.Name.getText());
                ma.closeFavoriteDrawer(pl);
            }
        });


    }

    // getItemCount() - 전체 데이터 갯수 리턴.
    @Override
    public int getItemCount() {
        return mData.size() ;
    }

    private void DeleteParkingLotData(){


    }
}