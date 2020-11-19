package com.example.capston_park_application;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.kakao.sdk.common.KakaoSdk;
import com.kakao.sdk.navi.NaviClient;
import com.kakao.sdk.navi.model.CoordType;
import com.kakao.sdk.navi.model.KakaoNaviParams;
import com.kakao.sdk.navi.model.Location;
import com.kakao.sdk.navi.model.NaviOption;

public class navigation_test extends AppCompatActivity {
    Button button;
    TextView latitude, longitude, position;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.navigation_test);
        //Tmap
//        final TMapTapi tmaptapi = new TMapTapi(this);
//        tmaptapi.setSKTMapAuthentication ("ffa317ad1ab94a9ca5365c8a0305bd88");
//        final boolean isTmapApp = tmaptapi.isTmapApplicationInstalled();
        //kakao
        KakaoSdk.init(this, "10be2992b503fbe5718496039f7ddace");
        KakaoNaviParams kakaoNaviParams;
        //위도, 경도
        latitude = (TextView) findViewById(R.id.tv_latitude);
        longitude = (TextView) findViewById(R.id.tv_longitude);
        position = (TextView) findViewById(R.id.get_position);

        final String la = latitude.getText().toString();
        final String lo = longitude.getText().toString();
        position.setText(la + lo);

        //카카오 내비 오류코드 3
        //애뮬레이터라 gps 인식이 안되는거 같음
        button = (Button) findViewById(R.id.navigation_btn);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Uri uri = NaviClient.getInstance().navigateWebUrl(
                        new Location("카카오 판교오피스", "127.108640", "37.402111"),
                        new NaviOption(CoordType.WGS84)
                );
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(uri);
                navigation_test.this.startActivity(intent);
            }
        });
    }
}