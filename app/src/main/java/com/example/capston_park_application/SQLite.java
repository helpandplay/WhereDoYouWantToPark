package com.example.capston_park_application;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

// SQLite 기능을 사용하는 코드들을 작성하는 곳 입니다.

//SQLite OpenHelper 이걸로 db에 접근할 수 있다.
class ParkingLotDBHelper extends SQLiteOpenHelper {
    private String TBL_NAME;
    private static final int DB_VERSION = 1;
    private static final String DBFILE_CONTACT = "parkinglot.db";

    public ParkingLotDBHelper(Context context, String TBL_NAME) {
        super(context, DBFILE_CONTACT, null, DB_VERSION);
        this.TBL_NAME = TBL_NAME;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(FavoriteDBSQL.CREATE_TBL);
        db.execSQL(ScopeDBSQL.CREATE_TBL);
    }
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        String sql;
        if(TBL_NAME == "Favorite"){
            sql = FavoriteDBSQL.DROP_TBL;
        }else if(TBL_NAME == "Scope"){
            sql = ScopeDBSQL.DROP_TBL;
        }else{
            Log.w("", "DB업글 실패"+TBL_NAME+"을 찾을 수 없습니다.");
            return;
        }
        db.execSQL(sql);
        onCreate(db);
    }
}

// 즐겨찾기를 다루기 위한 객체
class FavoriteDBSQL{
    private FavoriteDBSQL(){}

    private static final String TBL_NAME = "Favorite";
    private static final String COL_NO = "Favorite_id";
    private static final String COL_PARKINGLOT_ID = "ParkingLot_id";
    private static final String COL_PARKINGLOT_NAME = "ParkingLot_name";

    public static String CREATE_TBL = "CREATE TABLE IF NOT EXISTS " + TBL_NAME + " " +
            "(" +
            COL_NO + " INTEGER PRIMARY KEY AUTOINCREMENT" + "," +
            COL_PARKINGLOT_ID + " TEXT NOT NULL" + "," +
            COL_PARKINGLOT_NAME + " TEXT NOT NULL" +
            ")" ;
    public static String DATA_READ = "SELECT * FROM " + TBL_NAME;
    //public static String DATA_INSERT = "INSERT INTO " + TBL_NAME + " " +
    //        "(" + COL_PARKINGLOT_ID + "," + COL_PARKINGLOT_NAME +") VALUES ";
    public static String DATA_DELETE(String paringID){
        return "DELETE FROM " + TBL_NAME + "WHERE " + COL_PARKINGLOT_ID + "=" +paringID;
    }
    public static String DROP_TBL = "DROP TABLE IF EXISTS " + TBL_NAME ;
}

// 주차장 핀 거리제한 정보를 다루기 위한 객체
class ScopeDBSQL{
    private ScopeDBSQL(){}

    private static final String TBL_NAME = "Scope";
    private static final String COL_NO = "Scope_id";
    private static final String COL_distance = "Scope_distance";

    public static String CREATE_TBL = "CREATE TABLE IF NOT EXISTS " + TBL_NAME + " " +
            "(" +
            COL_NO + " INTEGER PRIMARY KEY AUTOINCREMENT" + "," +
            COL_distance + " TEXT NOT NULL" +
            ")" ;
    public static String DATA_READ = "SELECT * FROM " + TBL_NAME;
    public static String DATA_INSERT = "INSERT INTO " + TBL_NAME + " " +
            "(" + COL_distance +") VALUES ";
    public static String DATA_UPDATE(String distance){
        return "UPDATE " + TBL_NAME + " SET " + COL_distance + "=" + distance + " WHERE "+COL_NO + "= 1";
    }
    public static String DROP_TBL = "DROP TABLE IF EXISTS " + TBL_NAME ;
}