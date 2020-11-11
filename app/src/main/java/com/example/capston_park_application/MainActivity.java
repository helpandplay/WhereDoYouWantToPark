package com.example.capston_park_application;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;


public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        DataManager.Init(false, true);

        Button Btn_Parkinglot_Detail = (Button) findViewById(R.id.btn_parkinglot_detail_open);
        Btn_Parkinglot_Detail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(), "" + DataManager.isInit(), Toast.LENGTH_SHORT).show();
                Intent it = new Intent(getApplicationContext(), MainScreen.class);
                startActivity(it);
                finish();
            }
        });

    }

}