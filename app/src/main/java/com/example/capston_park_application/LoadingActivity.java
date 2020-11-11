package com.example.capston_park_application;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;




public class LoadingActivity extends AppCompatActivity {

    // 파이어베이스를 사용하지 않고 하드코딩 데이터를 사용한다면 false로 변경
    private static final boolean useFireBaseDB = true;
    private static final boolean doPrintDebug = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 레이아웃 지정 : /res/layout/activity_loading.xml
        setContentView(R.layout.activity_loading);


        // 데이터 메니저 객체의 초기화를 시작합니다.
        // 초기화는 파이어베이스의 데이터와, 로컬에 저장된 즐겨찾기 목록을 불러오는 단계입니다.
        DataManager.Init(useFireBaseDB, doPrintDebug);


        // 비동기 작업을 위해 AsyncTesk 를 상속한 DataManager 객체 생성 후 실행
        DataManager dm = new DataManager(useFireBaseDB, doPrintDebug, this);
        dm.execute("");
    }

    // DataManager 객체의 초기화가 완료되었을 때 호출되는 메소드
    public void InitChecker(){
        TextView PrintingLog = (TextView) findViewById(R.id.Loading_TextView_PrintLog);
        String message = "주차장 갯수 : " + DataManager.List_ParkingLot.size() + "\n"
                + "isinit : " + DataManager.isInit();
        PrintingLog.setText(message);

        // Map Activity 오류로 실행 불가능
        //      Caused by: java.lang.NullPointerException: Attempt to invoke virtual method 'void androidx.drawerlayout.widget.DrawerLayout.setDrawerLockMode(int)' on a null object reference
        //        at com.example.capston_park_application.MapActivity.onCreate(MapActivity.java:39)
        // Intent it = new Intent(getApplicationContext(), MapActivity.class);
        // startActivity(it);
        // finish();

    }
}
