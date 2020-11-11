package com.example.capston_park_application; // 본인 프로젝트이름에 맞게 변경

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;


public class MainActivity extends AppCompatActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) { //savedInstanceState = 세로, 가로 화면변경시 전역변수 초기화를 방지
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // 다른 클래스 함수 호출을 여기에 적으면 버튼들 정상 작동
    }
}