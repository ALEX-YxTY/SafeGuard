package com.exercise.yxty.safeguard.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.exercise.yxty.safeguard.beans.BlackListInfo;
import com.exercise.yxty.safeguard.beans.ContactInfo;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2016/1/31.
 */
public class ShortcutDAO {

    private ShortcutDBopenHelper helper;

    public ShortcutDAO(Context context) {
        helper = new ShortcutDBopenHelper(context);

    }

    //返回增加条目是否成功
    public boolean add(String name, String number) {
        SQLiteDatabase db = helper.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("name", name);
        cv.put("number", number);
        Long id = db.insert("shortcut", null, cv);
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
        int rows = db.delete("shortcut", "number = ?", new String[]{number});
        db.close();
        if (rows == 0) {
            return false;
        } else {
            return true;
        }
    }



    //返回表中所有元素并封装入javaBean
    public List<ContactInfo> findAll() {
        SQLiteDatabase db = helper.getReadableDatabase();
        List<ContactInfo> contactInfos = new ArrayList<>();
        Cursor cursor = db.query("shortcut", new String[]{"name", "number"}, null,
                null, null, null, null);
        while (cursor.moveToNext()) {
            ContactInfo cInfo = new ContactInfo();
            cInfo.setContactName(cursor.getString(cursor.getColumnIndex("name")));
            cInfo.setContactNumer(cursor.getString(cursor.getColumnIndex("number")));
            contactInfos.add(cInfo);
        }
        cursor.close();
        db.close();
        return contactInfos;
    }

    /**
     * 分批加载：
     *
     * @param limits 每批加载数量
     * @param startIndex 每次加载的起始偏移量
     * @return 跟新后的list
     */
    public List<ContactInfo> findByPage(int limits, int startIndex) {
        SQLiteDatabase db = helper.getReadableDatabase();
        List<ContactInfo> contactInfos = new ArrayList<>();
        //sql语法的limit 查询出limit数量，如果数量不足则返回实际的数量
        Cursor cursor = db.rawQuery("select name,number from shortcut limit ? offset ?",
                new String[]{String.valueOf(limits), String.valueOf(startIndex)});
        while (cursor.moveToNext()) {
            ContactInfo cInfo = new ContactInfo();
            cInfo.setContactName(cursor.getString(cursor.getColumnIndex("name")));
            cInfo.setContactNumer(cursor.getString(cursor.getColumnIndex("number")));
            contactInfos.add(cInfo);
        }
        cursor.close();
        db.close();
        return contactInfos;
    }

    /**
     *
     * @return 返回表中的总条目数
     */
    public int count() {
        SQLiteDatabase db = helper.getReadableDatabase();
        Cursor cursor = db.rawQuery("select count(*) from shortcut", null);
        cursor.moveToFirst();
        int count = cursor.getInt(0);
        cursor.close();
        db.close();
        return count;
    }
}
