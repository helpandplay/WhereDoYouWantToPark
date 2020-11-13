package com.example.capston_park_application;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import androidx.annotation.Nullable;

//SQL 문



//SQLite OpenHelper 이걸로 db에 접근할 수 있다.
public class ParkingLotDBHelper extends SQLiteOpenHelper {
    private String TBL_NAME;
    private static final int DB_VERSION = 1;
    private static final String DBFILE_CONTACT = "parkinglot.db";

    public ParkingLotDBHelper(Context context, String TBL_NAME) {
        super(context, DBFILE_CONTACT, null, DB_VERSION);
        this.TBL_NAME = TBL_NAME;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String sql;
        if(TBL_NAME == "Favorite"){
            sql = FavoriteDBSQL.CREATE_TBL;
        }else if(TBL_NAME == "Scope"){
            sql = ScopeDBSQL.CREATE_TBL;
        }else{
            Log.w("", "DB생성 실패"+TBL_NAME+"을 찾을 수 없습니다.");
            return;
        }
        db.execSQL(sql);

    }
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        String sql;
        if(TBL_NAME == "Favorite"){
            sql = FavoriteDBSQL.DROP_TBL;
        }else{
            sql = ScopeDBSQL.DROP_TBL;
        }
        db.execSQL(sql);
        onCreate(db);
    }
}