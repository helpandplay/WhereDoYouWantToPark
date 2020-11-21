package com.example.capston_park_application;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;

public class LoadingActivity extends AppCompatActivity {

    // 파이어베이스를 사용하지 않고 하드코딩 데이터를 사용한다면 false로 변경
    // 파이어베이스와 통신하여 주차장 리스트를 받아온다면 true로 변경
    private static final boolean useFireBaseDB = true;

    // 디버그 콘솔을 출력할 것인지 선택
    private static final boolean doPrintDebug = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 레이아웃 지정 : /res/layout/activity_loading.xml
        setContentView(R.layout.activity_loading);
        ImageView loadingimage = findViewById(R.id.loading_image);
        Glide.with(LoadingActivity.this).load(R.drawable.loadingbackgroundimage).into(loadingimage);
        // 데이터 메니저 객체의 초기화를 시작합니다.
        // 초기화는 파이어베이스의 데이터와, 로컬에 저장된 즐겨찾기 목록을 불러오는 단계입니다.
        DataManager.setContext(this);

        // 비동기 작업을 위해 AsyncTesk 를 상속한 DataManager 객체 생성 후 실행
        DataManager dm = new DataManager(useFireBaseDB, doPrintDebug, this);
        dm.execute("");
    }

    // DataManager 초기화 도중 Firebase Status가 1일 때 호출되는 메소드
    public void Init_Maintance(String msg){
        // TODO : "서버가 점검중입니다" 와, 메시지를 출력하는 확인창을 띄운다. 확인을 누르면 앱이 종료된다.
        TextView PrintingLog = (TextView) findViewById(R.id.Loading_TextView_PrintLog);
        PrintingLog.setText("서버가 점검 중 입니다. " + msg);
    }

    // DataManager 초기화 도중 Firebase Status가 0도 1도 아닐 때 호출되는 메소드
    public void Init_ExceptionError(FireBaseStatus status){
        // TODO : "알 수 없는 오류 발생" 과 에러 코드, 메시지를 출력하는 확인창을 띄운다. 확인을 누르면 앱이 종료된다.
        TextView PrintingLog = (TextView) findViewById(R.id.Loading_TextView_PrintLog);
        PrintingLog.setText("알 수 없는 오류가 발생했습니다. 코드 : " + status.Code + " 메시지 : " + status.Message);
    }

    // DataManager 가 DB에서 데이터를 불러올 때 호출되는 메소드(Status = 0)
    public void Init_DBFetching(){
        // TODO : 텍스트뷰.setText("데이터를 불러오는 중입니다."); 등 띄우기
        TextView PrintingLog = (TextView) findViewById(R.id.Loading_TextView_PrintLog);
        PrintingLog.setText("데이터를 불러오는 중 입니다.");
    }

    // DataManager 객체의 초기화가 완료되었을 때 호출되는 메소드
    public void Init_OK(){
        TextView PrintingLog = (TextView) findViewById(R.id.Loading_TextView_PrintLog);
        // String message = "주차장 갯수 : " + DataManager.List_ParkingLot.size() + "\n"+ "isinit : " + DataManager.isInit();
        String message = "로딩 완료!";
        PrintingLog.setText(message);

        // 로딩이 완료되면 Map Activity로 이동
        Intent it = new Intent(getApplicationContext(), MapActivity.class);
        startActivity(it);
        finish();

    }
}
