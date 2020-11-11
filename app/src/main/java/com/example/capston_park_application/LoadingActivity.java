package com.example.capston_park_application;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

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

        // 큰 비중 없는 텍스트뷰
        TextView TestLabel = (TextView) findViewById(R.id.Loading_TextView_TestTextView);

        // 테스트 로그를 출력하는 버튼
        Button Btn_PrintLog = (Button) findViewById(R.id.Loading_Button_PrintLog);
        // 위 버튼을 누르면 로그가 출력될 텍스트뷰
        final TextView PrintingLog = (TextView) findViewById(R.id.Loading_TextView_PrintLog);

        TestLabel.setText("로딩 엑티비티 시작");
        TestLabel.append("\nDataManager객체를 초기화합니다.");

        // 데이터 메니저 객체의 초기화를 시작합니다.
        // 초기화는 파이어베이스의 데이터와, 로컬에 저장된 즐겨찾기 목록을 불러오는 단계입니다.
        DataManager.Init(useFireBaseDB, doPrintDebug);



        // 테스트 버튼 클릭 이벤트
        Btn_PrintLog.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                String message = "테스트 출력\n";
                if(DataManager.isInit()){
                    message = "주차장 갯수 : " + DataManager.List_ParkingLot.size() + "\n";
                }
                else{
                    message = "DataManager 초기화 안됨.\n";
                }
                PrintingLog.setText(message);
            }
        });

    }

    private void checkInit(){
        try {
            Log.d("", "대기중 . . .");
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        if(DataManager.isInit()){



        }
        else{
            checkInit();
        }
    }

}