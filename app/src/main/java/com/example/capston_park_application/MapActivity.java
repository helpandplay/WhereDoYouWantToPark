package com.example.capston_park_application;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle; //String을 쓰기위함
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Arrays; // 검색내용 저장하기 위한 어레이 배열
import java.util.ArrayList; // 위와 같음

//구글 지도 import
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.w3c.dom.Text;

public class MapActivity extends AppCompatActivity implements OnMapReadyCallback {
    public static Context context_MainScreen;
    public ArrayList<String> address = new ArrayList<String>(Arrays.asList("ABC", "DEF", "GHI", "JKL", "ABO", "APM"));
    // 검색기능 테스트를 빨리 하기위한 기본값 임시 배열. 추후 삭제 또는 값을 비워두고 파이어베이스의 값을 가져와서 저장하는 용도로 사용해도 됌
    DrawerLayout option_drawerLayout, favorite_drawerLayout, parkinglot_drawerLayout;
    View option_drawerView, favorite_drawerView, parkinglot_drawerView;

    private GoogleMap mMap;
    private FusedLocationProviderClient mFusedLocationClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) { //savedInstanceState = 세로, 가로 화면변경시 전역변수 초기화를 방지
        context_MainScreen = this;
        super.onCreate(savedInstanceState); // protected에선 onCreate를 super로 불러와야함
        setContentView(R.layout.activity_map);
        // 레이아웃의 search_view 즉, 검색창의 값을 가져옴
        // 검색 기능 테스트를 위해 일단 Textview 값을 가져옴 추후 삭제
        // 50번재줄의 값을 받아옴

        //DB 받아오는 예시코드
        // DataManager.java에서 return 타입 확인, 메소드명 확인하여 사용하세요.

        //dbtest Favorite 테스트완료
//        TextView dbtest = (TextView) findViewById(R.id.dbtest);
////        DataManager.setContext(this);
////        DataManager.insertFavoriteElement("idtest", "nametest");
////        DataManager.insertFavoriteElement("idtest2", "nametest2");
////        ArrayList<FavoriteDB> testFavorite = DataManager.ReadFavoriteList();
////        String result="";
////        for(int i=0; i<testFavorite.size(); i++){
////            result += testFavorite.get(i).parkingName + testFavorite.get(i).parkingID;
////        }
////        dbtest.setText(result);
////        DataManager.deleteFavoriteElement("nametest");

        //dbtest map 테스트 완료
//        TextView dbtest = (TextView) findViewById(R.id.dbtest);
//       DataManager.setContext(this);
//        String scope = DataManager.ReadSearchScope();
//        ArrayList<FavoriteDB> testFavorite = DataManager.ReadFavoriteList();
//        String result="";
//        for(int i=0; i<testFavorite.size(); i++){
//            result += testFavorite.get(i).parkingName + testFavorite.get(i).parkingID;
//        }
//        dbtest.setText(scope);
//        DataManager.deleteFavoriteElement("nametest");

        option_drawerLayout = (DrawerLayout) findViewById(R.id.option_drawer_view);//activity_main의 option 드로워기능
        option_drawerView = (View) findViewById(R.id.option_drawer);//option_drawer의 드로워 모양
        favorite_drawerLayout = (DrawerLayout) findViewById(R.id.favorite_drawer_view);//activity_main의 favorite 드로워기능
        favorite_drawerView = (View) findViewById(R.id.favorite_drawer);//favorite_drawer의 드로워 모양
        parkinglot_drawerLayout = (DrawerLayout) findViewById(R.id.parkinglot_drawer_view);//activity_main의 parkinglot_deatil 드로워기능
        parkinglot_drawerView = (View) findViewById(R.id.parkinglot_drawer);//parkinglot_deatil의 드로워 모양

        ///////////////////슬라이드로 닫거나 열지 못하게 막음///////////////////
        option_drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_OPEN);
        option_drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
        favorite_drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_OPEN);
        favorite_drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
        parkinglot_drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_OPEN);
        parkinglot_drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
        //////////////////////////////////////////////////////////////////////
        ///////////////////각각 open, close 버튼 변수 저장///////////////////
        final ImageButton btn_open = (ImageButton) findViewById(R.id.btn_option_open);
        final Button btn_close = (Button) findViewById(R.id.btn_option_close);
        final ImageButton favorite_open = (ImageButton) findViewById(R.id.btn_favorite_open);
        final Button favorite_close = (Button) findViewById(R.id.btn_favorite_close);
        final Button btn_parkinglot_open = (Button) findViewById(R.id.btn_parkinglot_detail_open);
        final Button parkinglot_close = (Button) findViewById(R.id.btn_parkinglot_detail_close);
        ////////////////////////////////////////////////////////////////////

        ///////////////////옵션, 즐겨찾기, 주차장디테일 버튼 클릭시 드로워 이벤트 들///////////////////
        btn_open.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {   //옵션 버튼 open 클릭시
                btn_open.setVisibility(View.INVISIBLE);
                option_drawerLayout.setVisibility(View.VISIBLE);
                option_drawerLayout.openDrawer(option_drawerView);
                btn_parkinglot_open.setVisibility(View.INVISIBLE);
                favorite_open.setVisibility(View.INVISIBLE);
                option_drawerLayout.bringToFront();
            }
        });
        btn_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {   //옵션 버튼 close 클릭시
                btn_open.setVisibility(View.VISIBLE);
                option_drawerLayout.setVisibility(View.GONE);
                btn_parkinglot_open.setVisibility(View.VISIBLE);
                favorite_open.setVisibility(View.VISIBLE);
                option_drawerLayout.closeDrawers();
            }
        });

        favorite_open.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {   //즐겨찾기 버튼 open 클릭시
                favorite_drawerLayout.setVisibility(View.VISIBLE);
                favorite_drawerLayout.openDrawer(favorite_drawerView);
                btn_parkinglot_open.setVisibility(View.INVISIBLE);
                favorite_drawerLayout.bringToFront();
            }
        });

        favorite_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {   //겨찾기 버튼 close 클릭시
                favorite_drawerLayout.setVisibility(View.GONE);
                btn_parkinglot_open.setVisibility(View.VISIBLE);
                favorite_drawerLayout.closeDrawers();
            }
        });

        parkinglot_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {   //주차장 디테일 버튼 close 클릭시
                btn_parkinglot_open.setVisibility(View.VISIBLE);
                parkinglot_drawerLayout.setVisibility(View.GONE);
                parkinglot_drawerLayout.closeDrawers();
            }
        });
        ///////////////////////////////////////////////////////////////////////////////////////////

        MapFragment mapFragment = (MapFragment) getFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        //GPS기능 on/off 문구와 현재 위치 표시 및 현재 위치로 이동
        /*if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            googleMap.setMyLocationEnabled(true);
        } else {
            checkLocationPermissionWithRationale();
        }*/
        MarkerClickEvent();
    }

    public void MarkerClickEvent(){
        LatLng latLng = new LatLng(36.355422, 127.421316);
        mMap.addMarker(new MarkerOptions().position(latLng).title("한남대"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
        mMap.moveCamera(CameraUpdateFactory.zoomTo(15));

        ///////////////////parkinglot_detail.xml 의 id값들///////////////////
        final TextView parkinglot_name = findViewById(R.id.parkinglot_name);
        final TextView parkinglot_address = findViewById(R.id.parkinglot_address_new);
        final TextView parkinglot_distance = findViewById(R.id.parkinglot_distance);
        final TextView parkinglot_time = findViewById(R.id.parkinglot_time);
        //  final TextView parkinglot_test = findViewById(databaseList())
        ////////////////////////////////////////////////////////////////////

        final ListView listView = findViewById(R.id.parkinglot_listView);
        final View view = findViewById(R.id.parkinglot_drag);

        ///////////////////마커 클릭시 이벤트처리///////////////////
        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                ///////////////////클릭시 마커가 가진 정보로 변경///////////////////
                //지도에 위도경도값을 가진 주차장 마커가 생길경우
                //마커 클릭시 해당 위도경도를 가진 주차장의 이름, 주소, 운영시간등 값을
                //setText() 괄호안에 넣으면 됌
                parkinglot_name.setText("한남대");
                parkinglot_address.setText("");
                parkinglot_time.setText("");
                parkinglot_distance.setText("");

                //view.bringToFront(); 구글맵 앞에 위치시키면 앱이 튕김
                //view.performClick(); 마커클릭해서 리스트뷰 나오면 앱이 튕김
                parkinglot_drawerLayout.setVisibility(View.VISIBLE);
                parkinglot_drawerLayout.openDrawer(parkinglot_drawerView);
                parkinglot_drawerLayout.bringToFront();
                return false;
            }
        });
    }
    public static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;

    private void checkLocationPermissionWithRationale() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)) {
                new AlertDialog.Builder(this)
                        .setTitle("위치정보")
                        .setMessage("이 앱을 사용하기 위해서는 위치정보에 접근이 필요합니다. 위치정보 접근을 허용하여 주세요.")
                        .setPositiveButton("확인", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                ActivityCompat.requestPermissions(MapActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, MY_PERMISSIONS_REQUEST_LOCATION);
                            }
                        }).create().show();
            } else {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, MY_PERMISSIONS_REQUEST_LOCATION);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_LOCATION: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                        GoogleMap googleMap;
                        mMap.setMyLocationEnabled(true);
                    }
                } else {
                    Toast.makeText(this, "permission denied", Toast.LENGTH_LONG).show();
                }
                return;
            }
        }
    }
}