package com.exercise.yxty.safeguard.db;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;

import com.exercise.yxty.safeguard.beans.BlackListInfo;
import com.exercise.yxty.safeguard.beans.LockAppInfo;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2016/1/31.
 */
public class LockAppDAO {

    private LockAppDBopenHelper helper;
    private ContentResolver contentResolver;

    public LockAppDAO(Context context) {
        helper = new LockAppDBopenHelper(context);
        contentResolver = context.getContentResolver();
    }

    //返回增加条目是否成功
    public boolean add(String packageName) {
        SQLiteDatabase db = helper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("name", packageName);
        long id = db.insert("lockApps", null, values);
        db.close();
        if (id == -1) {
            return false;
        } else {
            //通知内容ContentObserver
            contentResolver.notifyChange(Uri.parse("com.exercise.yxty.safeguard.lockapp"), null);
            return true;
        }
    }

    //返回删除条目是否成功
    public boolean delete(String packageName) {
        SQLiteDatabase db = helper.getWritableDatabase();
        int row = db.delete("lockApps", "name = ?", new String[]{packageName});
        db.close();
        if (row == 0) {
            return false;
        } else {
            //通知内容ContentObserver
            contentResolver.notifyChange(Uri.parse("com.exercise.yxty.safeguard.lockapp"), null);
            return true;
        }
    }

    //判断该app是否已经被锁定
    public boolean isLocked(String packageName) {
        boolean result;
        SQLiteDatabase db = helper.getReadableDatabase();
        Cursor cursor = db.query("lockApps", null, "name = ?", new String[]{packageName}, null, null, null);
        if (cursor.getCount() > 0) {
            result = true;
        } else {
            result = false;
        }
        db.close();
        return result;
    }

    public int totalCount() {
        SQLiteDatabase db = helper.getReadableDatabase();
        Cursor cursor = db.query("lockApps", null, null, null, null, null, null);
        db.close();
        return cursor.getCount();
    }

    public List<String> findAll() {
        SQLiteDatabase db = helper.getReadableDatabase();
        Cursor cursor = db.query("lockApps", null, null, null, null, null, null);
        List<String> list = new ArrayList<>();
        while (cursor.moveToNext()) {
            list.add(cursor.getString(0));
        }
        cursor.close();
        db.close();
        return list;
    }

}
