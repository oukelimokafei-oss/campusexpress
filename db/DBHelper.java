package com.example.campusexpress.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "express.db";
    private static final int DATABASE_VERSION = 1;

    public static final String TABLE_EXPRESS = "express";
    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_TRACKING_NUMBER = "tracking_number";
    public static final String COLUMN_COMPANY = "company";
    public static final String COLUMN_PICKUP_CODE = "pickup_code";
    public static final String COLUMN_EXPECTED_TIME = "expected_time";
    public static final String COLUMN_STATUS = "status";
    public static final String COLUMN_CREATE_TIME = "create_time";
    public static final String COLUMN_PICKUP_TIME = "pickup_time";

    public DBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createTable = "CREATE TABLE " + TABLE_EXPRESS + " (" +
                COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_TRACKING_NUMBER + " TEXT NOT NULL, " +
                COLUMN_COMPANY + " TEXT NOT NULL, " +
                COLUMN_PICKUP_CODE + " TEXT, " +
                COLUMN_EXPECTED_TIME + " TEXT, " +
                COLUMN_STATUS + " INTEGER DEFAULT 0, " +
                COLUMN_CREATE_TIME + " INTEGER, " +
                COLUMN_PICKUP_TIME + " INTEGER" +
                ")";
        db.execSQL(createTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_EXPRESS);
        onCreate(db);
    }
}
