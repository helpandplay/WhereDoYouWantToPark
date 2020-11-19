package com.example.capston_park_application;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle; //String을 쓰기위함
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Arrays; // 검색내용 저장하기 위한 어레이 배열
import java.util.ArrayList; // 위와 같음

//구글 지도 import
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomsheet.BottomSheetDialog;

public class MapActivity extends AppCompatActivity implements OnMapReadyCallback {
    public static Context context_MainScreen;

    View option_drawerView, option_drawerlayout, favorite_drawerView, favorite_drawerlayout, search_layout, parkinglot_layout;

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

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        final Animation translateLeft = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.translate_left);
        final Animation translateRight = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.translate_right);
        // TODO : 데이터 준비
        // ViewDistance = DataMAnager?.getDistance???
        parkinglot_layout = (View)findViewById(R.id.parkinglot_layout);
        option_drawerView = (View)findViewById(R.id.option_drawer);
        option_drawerlayout = (View)findViewById(R.id.option_drawer_layout);
        favorite_drawerView = (View)findViewById(R.id.favorite_drawer);
        favorite_drawerlayout = (View)findViewById(R.id.favorite_drawer_layout);
        search_layout = (View)findViewById(R.id.relative);

        ///////////////////버튼 변수 저장///////////////////
        final ImageButton mylocation = (ImageButton)findViewById(R.id.mylocation);
        final ImageButton zoomin = (ImageButton)findViewById(R.id.zoomin);
        final ImageButton zoomout = (ImageButton)findViewById(R.id.zoomout);
        final ImageButton option_open = (ImageButton) findViewById(R.id.btn_option_open);
        final ImageButton option_close = (ImageButton) findViewById(R.id.btn_option_close);
        final ImageButton favorite_open = (ImageButton) findViewById(R.id.btn_favorite_open);
        final Button favorite_close = (Button) findViewById(R.id.btn_favorite_close);
        final ImageButton location = (ImageButton) findViewById(R.id.mylocation);
        ////////////////////////////////////////////////////

        ///////////////////옵션, 즐겨찾기 클릭시 드로워 이벤트 들///////////////////
        option_open.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //옵션 버튼 open 클릭시 옵션창을 제외한 모든 창 안보이게
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
        location.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkLocationPermission()) {
                    fusedLocationClient.getLastLocation()
                            .addOnSuccessListener(MapActivity.this, new OnSuccessListener<Location>() {
                                @Override
                                public void onSuccess(Location location) {
                                    if (location != null) {
                                        mMap.animateCamera(CameraUpdateFactory.newLatLng(new LatLng(location.getLatitude(),location.getLongitude())));
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
    }

    // 마커 클릭 이벤트 추가
    public void MarkerClickEvent(){
        // 앱 처음 실행시 화면 이동할 좌표(한남대)
        LatLng latLng = new LatLng(36.355422, 127.421316);
        // mMap.addMarker(new MarkerOptions().position(latLng).title("한남대"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
        mMap.moveCamera(CameraUpdateFactory.zoomTo(14));

        final TextView parkinglot_name = findViewById(R.id.parkinglot_name);
        final TextView parkinglot_address_new = findViewById(R.id.parkinglot_address_new);
        final TextView parkinglot_address_old = findViewById(R.id.parkinglot_address_old);
        //final TextView parkinglot_distance = findViewById(R.id.parkinglot_distance);

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

        final Animation translateUp = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.translate_up);
        final Animation translateDown = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.translate_down);
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
                //parkinglot_distance.setText("");

                //신주소 정보 없을때
                if(TextUtils.isEmpty(temp_address_new)){
                    parkinglot_address_new.setText("정보없음");
                }
                //구주소 정보 없을때
                if(TextUtils.isEmpty(temp_address_old)){
                    parkinglot_address_new.setText("정보없음");
                }
                parkinglot_layout.setVisibility(View.VISIBLE);
                parkinglot_layout.startAnimation(translateUp);

                closebutton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {   //주차장 디테일 버튼 close 클릭시
                        parkinglot_layout.setVisibility(View.INVISIBLE);
                        parkinglot_layout.startAnimation(translateDown);
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
            mMap.addMarker(mo);
        }
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
}