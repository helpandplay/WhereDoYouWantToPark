package com.example.capston_park_application;

import android.util.Log;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;

// Firebase와 통신하기 위한 코드들을 작성하는 곳 입니다.

class FireBase {

    public static FireBaseStatus getStatus() {
        final FireBaseStatus res = new FireBaseStatus();
        DatabaseReference StatusDB = FirebaseDatabase.getInstance().getReference().child("status");
        StatusDB.addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        HashMap<String, String> hmap = (HashMap<String, String>) dataSnapshot.getValue();
                        res.Code = hmap.get("code");
                        res.Message = hmap.get("message");
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Log.e("","Error while reading data");
                    }
                });
        return res;
    }

    // Firebase DB로부터 주차장 리스트를 받아오는 메소드
    public static ArrayList<ParkingLot> getData() {
        final ArrayList<ParkingLot> Result = new ArrayList<ParkingLot>();
        DatabaseReference fBaseDB = FirebaseDatabase.getInstance().getReference();

        fBaseDB.child("parkinglotV2").addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        // Foreach라서 반복 횟수 추적을 위해 int형을 하나 추가합니다.
                        int i = 0;

                        // 파이어베이스로부터 데이터들을 받아와 반복문을 돌립니다.
                        for (DataSnapshot ss : dataSnapshot.getChildren()) {
                            // 예외 탐지를 위한 try - catch 문
                            try {
                                // 데이터 하나를 받아옵니다.
                                ParkingLot data = ss.getValue(ParkingLot.class);
                                // 데이터가 문제가 없다면 ArrayList에 넣습니다.
                                if(!data.hasMissingData())
                                {
                                    // 중복 주차장 제거
                                    boolean flag = true;
                                    for(ParkingLot pl : Result){
                                        if(pl.getID_ParkingLot().equals(data.getID_ParkingLot())){
                                            flag = false;
                                            break;
                                        }
                                    }
                                    if(flag) Result.add(data);

                                }
                            } catch (Exception e) {
                                // 예외가 발생하면 로그와 스택트레이스를 띄웁니다.
                                Log.e("", "예외 발생 (인덱스 : " + i + " )");
                                e.printStackTrace();
                            }
                            i++;
                        }

                        // 반복문이 끝나면 불러온 데이터를 확인하기 위해 로그를 찍어봅니다.
                        Log.d("", "데이터 다운로드 완료 ! (" + i + " 번 의 요청 전송됨)");
                        Log.d("", "파이어베이스로부터 " + DataManager.List_ParkingLot.size() + "개의 주차장 데이터 로드됨");
                    }
                    // 예외처리
                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Log.e("", "getUser:onCancelled", databaseError.toException());
                    }
                });
        return Result;
    }

}