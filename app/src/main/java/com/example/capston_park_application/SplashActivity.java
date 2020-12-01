package com.example.capston_park_application;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

// 김규보 님이 제작한 클래스
// 스플레시 관련 코드 참고 : https://yoo-hyeok.tistory.com/31 [유혁의 엉터리 개발]
public class SplashActivity extends Activity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        Handler hd = new Handler();

        // 스플레시 표시하는 시간 조절하기(개발 중에는 짧게)
        // delayMillis : 스플레시 창 표시되는 시간(ms단위, 1000ms = 1s)
        hd.postDelayed(new splashhandler(), 1500);
    }

    private class splashhandler implements Runnable{
        public void run(){
            // 로딩이 끝난 후, ChoiceFunction 이동
            startActivity(new Intent(getApplication(), LoadingActivity.class));

            // 로딩페이지 Activity stack에서 제거
            SplashActivity.this.finish();
        }
    }

    //초반 플래시 화면에서 넘어갈때 뒤로가기 버튼 못누르게 함
    @Override
    public void onBackPressed() { }

}
