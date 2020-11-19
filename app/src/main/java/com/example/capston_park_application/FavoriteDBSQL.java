package com.example.capston_park_application;

public class FavoriteDBSQL{
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