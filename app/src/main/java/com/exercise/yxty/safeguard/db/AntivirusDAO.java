package com.exercise.yxty.safeguard.db;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

/**
 * Created by Administrator on 2016/1/27.
 */
public class AntivirusDAO {


    //判断此md5值是否是病毒
    public static String isVirus(String fileMd5) {

        String result = null;

        String path = "data/data/com.exercise.yxty.safeguard/files/antivirus.db";
        //获取数据库对象
        SQLiteDatabase db = SQLiteDatabase.openDatabase(path, null, SQLiteDatabase.OPEN_READONLY);
        Cursor cursor = db.query("datable", new String[]{"name", "desc"}, "md5 = ?", new String[]{fileMd5}, null, null, null);
        if (cursor.moveToNext()) {
            String name = cursor.getString(0);
            String desc = cursor.getString(1);
            result = name + "!" + desc;
        }
        return result;
    }

    public static void add(String fileMd5, String name, String desc) {
        String path = "data/data/com.exercise.yxty.safeguard/files/antivirus.db";
        SQLiteDatabase db = SQLiteDatabase.openDatabase(path, null, SQLiteDatabase.OPEN_READWRITE);
        if (isVirus(fileMd5) == null) {
            ContentValues values = new ContentValues();
            values.put("md5", fileMd5);
            values.put("type", 6);
            values.put("name", name);
            values.put("desc", desc);
            db.insert("datable", null, values);
            Log.i("test", "加入一条新数据");
        }
        db.close();
    }



}
