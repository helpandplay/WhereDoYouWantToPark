package com.example.capston_park_application;

public class ScopeDBSQL{
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