package com.example.capston_park_application;

import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;

import android.content.Context;
import android.os.Bundle; //String을 쓰기위함
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

import java.util.Arrays; // 검색내용 저장하기 위한 어레이 배열
import java.util.ArrayList; // 위와 같음

// 구글 지도 import
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class MapActivity extends AppCompatActivity implements OnMapReadyCallback {
    public static Context context_MainScreen;
    public ArrayList<String> address = new ArrayList<String>(Arrays.asList("ABC", "DEF", "GHI", "JKL", "ABO", "APM"));
    // 검색기능 테스트를 빨리 하기위한 기본값 임시 배열. 추후 삭제 또는 값을 비워두고 파이어베이스의 값을 가져와서 저장하는 용도로 사용해도 됌
    DrawerLayout option_drawerLayout, favorite_drawerLayout, parkinglot_drawerLayout;
    View option_drawerView, favorite_drawerView, parkinglot_drawerView;

    private GoogleMap mMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) { //savedInstanceState = 세로, 가로 화면변경시 전역변수 초기화를 방지
        context_MainScreen = this;
        super.onCreate(savedInstanceState); // protected에선 onCreate를 super로 불러와야함
        setContentView(R.layout.activity_map);
        // 레이아웃의 search_view 즉, 검색창의 값을 가져옴
        // 검색 기능 테스트를 위해 일단 Textview 값을 가져옴 추후 삭제
        // 50번재줄의 값을 받아옴

        option_drawerLayout = (DrawerLayout)findViewById(R.id.option_drawer_view);//activity_main의 option 드로워기능
        option_drawerView = (View)findViewById(R.id.option_drawer);//option_drawer의 드로워 모양
        favorite_drawerLayout = (DrawerLayout)findViewById(R.id.favorite_drawer_view);//activity_main의 favorite 드로워기능
        favorite_drawerView = (View)findViewById(R.id.favorite_drawer);//favorite_drawer의 드로워 모양
        parkinglot_drawerLayout = (DrawerLayout)findViewById(R.id.parkinglot_drawer_view);//activity_main의 parkinglot_deatil 드로워기능
        parkinglot_drawerView = (View)findViewById(R.id.parkinglot_drawer);//parkinglot_deatil의 드로워 모양

        ///////////////////슬라이드로 닫거나 열지 못하게 막음///////////////////
        option_drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_OPEN);
        option_drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
        favorite_drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_OPEN);
        favorite_drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
        parkinglot_drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_OPEN);
        parkinglot_drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
        //////////////////////////////////////////////////////////////////////
        ///////////////////각각 open, close 버튼 변수 저장///////////////////
        final ImageButton btn_open = (ImageButton)findViewById(R.id.btn_option_open);
        final Button btn_close = (Button)findViewById(R.id.btn_option_close);
        final ImageButton favorite_open = (ImageButton)findViewById(R.id.btn_favorite_open);
        final Button favorite_close = (Button)findViewById(R.id.btn_favorite_close);
        final Button btn_parkinglot_open = (Button)findViewById(R.id.btn_parkinglot_detail_open);
        final Button parkinglot_close = (Button)findViewById(R.id.btn_parkinglot_detail_close);
        ////////////////////////////////////////////////////////////////////

        ///////////////////옵션, 즐겨찾기, 주차장디테일 버튼 클릭시 드로워 이벤트 들///////////////////
        btn_open.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {   //옵션 버튼 open 클릭시
                btn_open.setVisibility(View.INVISIBLE);
                option_drawerLayout.setVisibility(View.VISIBLE);
                option_drawerLayout.openDrawer(option_drawerView);
                btn_parkinglot_open.setVisibility(View.INVISIBLE);
                option_drawerLayout.bringToFront();
            }
        });
        btn_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {   //옵션 버튼 close 클릭시
                btn_open.setVisibility(View.VISIBLE);
                option_drawerLayout.setVisibility(View.GONE);
                btn_parkinglot_open.setVisibility(View.VISIBLE);
                option_drawerLayout.closeDrawers();
            }
        });

        favorite_open.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {   //즐겨찾기 버튼 open 클릭시
                favorite_open.setVisibility(View.INVISIBLE);
                favorite_drawerLayout.setVisibility(View.VISIBLE);
                favorite_drawerLayout.openDrawer(favorite_drawerView);
                btn_parkinglot_open.setVisibility(View.INVISIBLE);
                favorite_drawerLayout.bringToFront();
            }
        });

        favorite_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {   //겨찾기 버튼 close 클릭시
                favorite_open.setVisibility(View.VISIBLE);
                favorite_drawerLayout.setVisibility(View.GONE);
                btn_parkinglot_open.setVisibility(View.VISIBLE);
                favorite_drawerLayout.closeDrawers();
            }
        });
        btn_parkinglot_open.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {   //주차장 디테일 버튼 open 클릭시
                btn_parkinglot_open.setVisibility(View.INVISIBLE);
                parkinglot_drawerLayout.setVisibility(View.VISIBLE);
                parkinglot_drawerLayout.openDrawer(parkinglot_drawerView);
                parkinglot_drawerLayout.bringToFront();
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

        // Add a marker in Sydney and move the camera
        LatLng test = new LatLng(37, 127);
        mMap.addMarker(new MarkerOptions()
                .position(test)
                .title("Testest"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(test));
    }
}