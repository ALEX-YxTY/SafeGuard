package com.exercise.yxty.safeguard.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.exercise.yxty.safeguard.beans.BlackListInfo;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2016/1/31.
 */
public class BlackListDAO {

    private BlackListDBopenHelper helper;

    public BlackListDAO(Context context) {
        helper = new BlackListDBopenHelper(context);

    }

    //返回增加条目是否成功
    public boolean add(String number, String mode) {
        SQLiteDatabase db = helper.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("number", number);
        cv.put("mode", mode);
        Long id = db.insert("blacklist", null, cv);
        db.close();
        if (id == -1) {
            return false;
        }else{
            return true;
        }
    }

    //返回删除条目是否成功
    public boolean delete(String number) {
        SQLiteDatabase db = helper.getWritableDatabase();
        int rows = db.delete("blacklist", "number = ?", new String[]{number});
        db.close();
        if (rows == 0) {
            return false;
        } else {
            return true;
        }
    }

    //返回更改条目是否成功
    public boolean changeMode(String number, String mode) {
        SQLiteDatabase db = helper.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("mode", mode);
        int rows = db.update("blacklist", cv, "number = ?", new String[]{number});
        db.close();
        if (rows == 0) {
            return false;
        } else {
            return true;
        }
    }

    //返回所查找的number的mode
    public String  find(String number) {
        String mode = "";
        SQLiteDatabase db = helper.getReadableDatabase();
        Cursor cursor = db.query("blacklist", new String[]{"mode"}, "number =?",
                new String[]{number}, null, null, null);
        if (cursor.getCount() > 0) {
            cursor.moveToFirst();
             mode = cursor.getString(0);
        }
        cursor.close();
        db.close();
        return mode;

    }


    //返回表中所有元素并封装入javaBean
    public List<BlackListInfo> findAll() {
        SQLiteDatabase db = helper.getReadableDatabase();
        List<BlackListInfo> blackListInfos = new ArrayList<>();
        Cursor cursor = db.query("blacklist", new String[]{"mode", "number"}, null,
                null, null, null, null);
        while (cursor.moveToNext()) {
            BlackListInfo blInfo = new BlackListInfo();
            blInfo.setNumber(cursor.getString(cursor.getColumnIndex("number")));
            blInfo.setMode(cursor.getString(cursor.getColumnIndex("mode")));
            blackListInfos.add(blInfo);
        }
        cursor.close();
        db.close();
        return blackListInfos;
    }

    /**
     * 分批加载：
     *
     * @param limits 每批加载数量
     * @param startIndex 每次加载的起始偏移量
     * @return 跟新后的list
     */
    public List<BlackListInfo> findByPage(int limits, int startIndex) {
        SQLiteDatabase db = helper.getReadableDatabase();
        List<BlackListInfo> blackListInfos = new ArrayList<>();
        //sql语法的limit 查询出limit数量，如果数量不足则返回实际的数量
        Cursor cursor = db.rawQuery("select number,mode from blacklist limit ? offset ?",
                new String[]{String.valueOf(limits), String.valueOf(startIndex)});
        while (cursor.moveToNext()) {
            BlackListInfo blInfo = new BlackListInfo();
            blInfo.setNumber(cursor.getString(cursor.getColumnIndex("number")));
            blInfo.setMode(cursor.getString(cursor.getColumnIndex("mode")));
            blackListInfos.add(blInfo);
        }
        cursor.close();
        db.close();
        return blackListInfos;
    }

    /**
     *
     * @return 返回表中的总条目数
     */
    public int count() {
        SQLiteDatabase db = helper.getReadableDatabase();
        Cursor cursor = db.rawQuery("select count(*) from blacklist", null);
        cursor.moveToFirst();
        int count = cursor.getInt(0);
        cursor.close();
        db.close();
        return count;
    }
}
