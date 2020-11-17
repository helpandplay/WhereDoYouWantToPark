package com.example.capston_park_application;


import android.content.pm.LauncherActivityInfo;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class FavoriteAdapter extends RecyclerView.Adapter<FavoriteAdapter.ItemViewHolder> {

    private ArrayList<Favorite> favoriteData = new ArrayList<>();

    @NonNull
    @Override
    public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.favorite_item,parent,false);

        return new ItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ItemViewHolder holder, int position) {

        holder.onBind(favoriteData.get(position));
    }

    @Override
    public int getItemCount() {
        return favoriteData.size();
    }

    void addItem(Favorite data){
        favoriteData.add(data);
    }

    class ItemViewHolder extends RecyclerView.ViewHolder {

        private TextView tv_favorite_name;
        private TextView tv_favorite_address;

        ItemViewHolder(@NonNull View itemView) {
            super(itemView);

            tv_favorite_name = itemView.findViewById(R.id.tv_favorite_name);
            tv_favorite_address = itemView.findViewById(R.id.tv_favorite_address);
        }

        void onBind(Favorite data) {
            tv_favorite_name.setText(data.getName_ParkingLot());
            tv_favorite_address.setText(data.getAddress_new());

        }
    }
}
