package com.example.capston_park_application;

import android.os.Bundle;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Favorite_test extends AppCompatActivity {

    private FavoriteAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.favorite_drawer);

        init();
    }

    private void init() {
        RecyclerView recyclerView = findViewById(R.id.rv_favorite);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);

        adapter = new FavoriteAdapter();
        recyclerView.setAdapter(adapter);
    }

    private void getData(){
        List<String> favorite_name = Arrays.asList("");
        List<String> favorite_address = Arrays.asList("");

        for(int i = 0; i < favorite_name.size(); i++){
            Favorite_Data data = new Favorite_Data();
            data.setFavorite_name(favorite_name.get(i));
            data.setFavorite_address_new(favorite_address.get(i));

            adapter.addItem(data);
        }

        adapter.notifyDataSetChanged();
    }

}