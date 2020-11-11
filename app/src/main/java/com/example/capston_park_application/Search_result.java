package com.example.capston_park_application;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.Arrays;

import android.os.Bundle;
import android.widget.SearchView; // 검색창 위젯
import android.widget.TextView; // 검색 테스트를 위해 사용했음 추후 사용안하게 될거같음

public class Search_result extends AppCompatActivity {
    public ArrayList<String> address = new ArrayList<String>(Arrays.asList("ABC", "DEF", "GHI", "JKL", "ABO", "APM"));
    //////테스트를 위한 배열, 추후 주차장 정보를 여기 저장한 후 검색 가능//////

    protected void onCreate(Bundle SavedInstanceState){
        super.onCreate(SavedInstanceState);
        setContentView(R.layout.activity_main);
        SearchView searchView = findViewById(R.id.searchBar); // 레이아웃의 search_view바

        final TextView resultTextView = findViewById(R.id.textView); // 검색 기능 테스트를 위해 일단 Textview 값을 가져옴 추후 삭제
        resultTextView.setText(getResult()); // 50번재줄의 값을 받아옴 // 즉, 검색창의 값을 가져옴
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() { // 레이아웃 검색창에 검색기능을 구현함, 없으면 돋보기는 그냥 이미지파일
            @Override
            public boolean onQueryTextSubmit(String query) { // Submit은 단어 검색을 완료했을때 값을 보여줌 ex) ABC를 타이핑후 검색해야 ABC 값을 보여줌
                return false;
            }
            @Override
            public boolean onQueryTextChange(String newText) { // Change는 단어 검색을 하고있을때도 값을 보여줌 ex) A만 쳐도 ABC, ADT ,ANS 등을 보여줌
                resultTextView.setText(search(newText));
                return true;
            }
        });
    }

    private String search(String query){ //검색값 필터링
        // 30번째줄의 newText 즉 검색창의 문자가 바뀔때마다 해당 문자를 포함한 배열을 보여줌
        StringBuilder save_string = new StringBuilder(); //문자열 저장을 위한 함수, 임시 수행
        for(int i = 0; i < address.size(); i++) { // 12번째줄의 사이즈값만큼 반복문 수행
            String dummy_address = address.get(i); // dummy_address 에 주소값을 일시저장
            if(dummy_address.toLowerCase().contains(query.toLowerCase())){ // 입력받은 값이랑 12번째줄 저장된 값이랑 같으면
                save_string.append(dummy_address); //주소값 저장 (toLowerCase를 쓴 이유는 목록과 입력값 둘다 소문자로 바꿔서 비교하기 위함
                if (i != address.size()-1) { // 마지막줄 전까지 칸내림
                    save_string.append("\n");
                }
            }
        }
        return save_string.toString(); //StringBuilder에 리턴
    }
    private String getResult(){ //검색 결과 표시
        StringBuilder save_string = new StringBuilder(); //문자열 저장을 위한 함수, 임시 수행
        for(int i = 0; i < address.size(); i++){ // 12번째줄의 사이즈값만큼 반복문 수행
            String dummy_address = address.get(i); //dummy_address 에 12번째줄의 주소들을 일시저장
            save_string.append(dummy_address); //더미 문자열에 주소 임시 저장
            if(i != address.size()-1) { // 마지막줄 전까지 칸내림
                save_string.append("\n");
            }
        }
        return save_string.toString(); //StringBuilder에 리턴
    }
}