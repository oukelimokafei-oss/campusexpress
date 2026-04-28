package com.example.campusexpress.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.campusexpress.bean.Express;

import java.util.ArrayList;
import java.util.List;

public class ExpressDao {
    private DBHelper dbHelper;

    public ExpressDao(Context context) {
        dbHelper = new DBHelper(context);
    }

    // 添加快递
    public long addExpress(Express express) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(DBHelper.COLUMN_TRACKING_NUMBER, express.getTrackingNumber());
        values.put(DBHelper.COLUMN_COMPANY, express.getCompany());
        values.put(DBHelper.COLUMN_PICKUP_CODE, express.getPickupCode());
        values.put(DBHelper.COLUMN_EXPECTED_TIME, express.getExpectedTime());
        values.put(DBHelper.COLUMN_STATUS, express.getStatus());
        values.put(DBHelper.COLUMN_CREATE_TIME, express.getCreateTime());
        values.put(DBHelper.COLUMN_PICKUP_TIME, express.getPickupTime());

        long id = db.insert(DBHelper.TABLE_EXPRESS, null, values);
        db.close();
        return id;
    }

    // 批量添加快递
    public int addExpressList(List<Express> list) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        int successCount = 0;
        db.beginTransaction();
        try {
            for (Express express : list) {
                ContentValues values = new ContentValues();
                values.put(DBHelper.COLUMN_TRACKING_NUMBER, express.getTrackingNumber());
                values.put(DBHelper.COLUMN_COMPANY, express.getCompany());
                values.put(DBHelper.COLUMN_PICKUP_CODE, express.getPickupCode());
                values.put(DBHelper.COLUMN_EXPECTED_TIME, express.getExpectedTime());
                values.put(DBHelper.COLUMN_STATUS, express.getStatus());
                values.put(DBHelper.COLUMN_CREATE_TIME, express.getCreateTime());
                values.put(DBHelper.COLUMN_PICKUP_TIME, express.getPickupTime());
                long id = db.insert(DBHelper.TABLE_EXPRESS, null, values);
                if (id > 0) successCount++;
            }
            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }
        db.close();
        return successCount;
    }

    // 获取所有快递（用于备份）
    public List<Express> getAllExpresses() {
        List<Express> list = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.query(DBHelper.TABLE_EXPRESS, null, null, null,
                null, null, DBHelper.COLUMN_ID + " ASC");

        while (cursor.moveToNext()) {
            Express express = new Express();
            express.setId(cursor.getInt(cursor.getColumnIndexOrThrow(DBHelper.COLUMN_ID)));
            express.setTrackingNumber(cursor.getString(cursor.getColumnIndexOrThrow(DBHelper.COLUMN_TRACKING_NUMBER)));
            express.setCompany(cursor.getString(cursor.getColumnIndexOrThrow(DBHelper.COLUMN_COMPANY)));
            express.setPickupCode(cursor.getString(cursor.getColumnIndexOrThrow(DBHelper.COLUMN_PICKUP_CODE)));
            express.setExpectedTime(cursor.getString(cursor.getColumnIndexOrThrow(DBHelper.COLUMN_EXPECTED_TIME)));
            express.setStatus(cursor.getInt(cursor.getColumnIndexOrThrow(DBHelper.COLUMN_STATUS)));
            express.setCreateTime(cursor.getLong(cursor.getColumnIndexOrThrow(DBHelper.COLUMN_CREATE_TIME)));
            express.setPickupTime(cursor.getLong(cursor.getColumnIndexOrThrow(DBHelper.COLUMN_PICKUP_TIME)));
            list.add(express);
        }
        cursor.close();
        db.close();
        return list;
    }

    // 清空所有数据
    public void clearAllData() {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.delete(DBHelper.TABLE_EXPRESS, null, null);
        db.close();
    }

    // 获取所有待取件快递
    public List<Express> getPendingExpresses() {
        List<Express> list = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String selection = DBHelper.COLUMN_STATUS + " = ?";
        String[] selectionArgs = {"0"};
        Cursor cursor = db.query(DBHelper.TABLE_EXPRESS, null, selection, selectionArgs,
                null, null, DBHelper.COLUMN_CREATE_TIME + " DESC");

        while (cursor.moveToNext()) {
            Express express = new Express();
            express.setId(cursor.getInt(cursor.getColumnIndexOrThrow(DBHelper.COLUMN_ID)));
            express.setTrackingNumber(cursor.getString(cursor.getColumnIndexOrThrow(DBHelper.COLUMN_TRACKING_NUMBER)));
            express.setCompany(cursor.getString(cursor.getColumnIndexOrThrow(DBHelper.COLUMN_COMPANY)));
            express.setPickupCode(cursor.getString(cursor.getColumnIndexOrThrow(DBHelper.COLUMN_PICKUP_CODE)));
            express.setExpectedTime(cursor.getString(cursor.getColumnIndexOrThrow(DBHelper.COLUMN_EXPECTED_TIME)));
            express.setStatus(cursor.getInt(cursor.getColumnIndexOrThrow(DBHelper.COLUMN_STATUS)));
            express.setCreateTime(cursor.getLong(cursor.getColumnIndexOrThrow(DBHelper.COLUMN_CREATE_TIME)));
            express.setPickupTime(cursor.getLong(cursor.getColumnIndexOrThrow(DBHelper.COLUMN_PICKUP_TIME)));
            list.add(express);
        }
        cursor.close();
        db.close();
        return list;
    }

    // 获取已取件历史
    public List<Express> getHistoryExpresses() {
        List<Express> list = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String selection = DBHelper.COLUMN_STATUS + " = ?";
        String[] selectionArgs = {"1"};
        Cursor cursor = db.query(DBHelper.TABLE_EXPRESS, null, selection, selectionArgs,
                null, null, DBHelper.COLUMN_PICKUP_TIME + " DESC");

        while (cursor.moveToNext()) {
            Express express = new Express();
            express.setId(cursor.getInt(cursor.getColumnIndexOrThrow(DBHelper.COLUMN_ID)));
            express.setTrackingNumber(cursor.getString(cursor.getColumnIndexOrThrow(DBHelper.COLUMN_TRACKING_NUMBER)));
            express.setCompany(cursor.getString(cursor.getColumnIndexOrThrow(DBHelper.COLUMN_COMPANY)));
            express.setPickupCode(cursor.getString(cursor.getColumnIndexOrThrow(DBHelper.COLUMN_PICKUP_CODE)));
            express.setExpectedTime(cursor.getString(cursor.getColumnIndexOrThrow(DBHelper.COLUMN_EXPECTED_TIME)));
            express.setStatus(cursor.getInt(cursor.getColumnIndexOrThrow(DBHelper.COLUMN_STATUS)));
            express.setCreateTime(cursor.getLong(cursor.getColumnIndexOrThrow(DBHelper.COLUMN_CREATE_TIME)));
            express.setPickupTime(cursor.getLong(cursor.getColumnIndexOrThrow(DBHelper.COLUMN_PICKUP_TIME)));
            list.add(express);
        }
        cursor.close();
        db.close();
        return list;
    }

    // 更新快递状态（标记已取件）
    public boolean markAsPicked(int id) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(DBHelper.COLUMN_STATUS, 1);
        values.put(DBHelper.COLUMN_PICKUP_TIME, System.currentTimeMillis());
        int rows = db.update(DBHelper.TABLE_EXPRESS, values, DBHelper.COLUMN_ID + " = ?",
                new String[]{String.valueOf(id)});
        db.close();
        return rows > 0;
    }

    // 删除快递
    public boolean deleteExpress(int id) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        int rows = db.delete(DBHelper.TABLE_EXPRESS, DBHelper.COLUMN_ID + " = ?",
                new String[]{String.valueOf(id)});
        db.close();
        return rows > 0;
    }

    // 添加快递单号唯一性检查方法
    public boolean isTrackingNumberExists(String trackingNumber) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String selection = DBHelper.COLUMN_TRACKING_NUMBER + " = ? AND " + DBHelper.COLUMN_STATUS + " = ?";
        String[] selectionArgs = {trackingNumber, "0"}; // 只检查待取件的快递，已取件的允许重复添加
        Cursor cursor = db.query(DBHelper.TABLE_EXPRESS, new String[]{DBHelper.COLUMN_ID},
                selection, selectionArgs, null, null, null);
        boolean exists = cursor.getCount() > 0;
        cursor.close();
        db.close();
        return exists;
    }
}
