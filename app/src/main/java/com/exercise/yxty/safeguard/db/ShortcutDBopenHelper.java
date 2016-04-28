package com.exercise.yxty.safeguard.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Administrator on 2016/1/31.
 */
public class ShortcutDBopenHelper extends SQLiteOpenHelper {

    //对象创建方法，传入固定参数
    public ShortcutDBopenHelper(Context context) {
        super(context, "Shortcut.db", null, 1);
    }

    //创建时调用，生成数据库，如已生成则不调用
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table shortcut (_id integer primary key autoincrement, name varchar(20), number varchar(20))" );

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
