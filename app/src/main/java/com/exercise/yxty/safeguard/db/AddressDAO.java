package com.exercise.yxty.safeguard.db;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;

/**
 * Created by Administrator on 2016/1/27.
 */
public class AddressDAO {


    //查询号码 范围地址
    public static String sqlAddress(String num) {

        final int MOBILE = 1;
        final int TELEPHONE = 2;

        String path = "data/data/com.exercise.yxty.safeguard/files/address.db";
        String address = "未知号码";

        //获取数据库对象
        SQLiteDatabase db = SQLiteDatabase.openDatabase(path, null, SQLiteDatabase.OPEN_READONLY);

        //正则判断num情况，正则使用.match()的匹配方法
        //手机号码：1开头，第二个数字：3-8，一共11个数字
        if (num.matches("^1[3-8]\\d{9}$")) {
            address = query(db, num, MOBILE);
        }else if (num.matches("^\\d+$")) {
            switch (num.length()) {
                case 3:
                    address = "报警电话";
                    break;
                case 4:
                    address = "模拟器";
                    break;
                case 5:
                    address = "服务电话";
                    break;
                case 7:
                case 8:
                    address = "本地电话";
                    break;
                //可能是长途号10-12位
                default:
                    address = query(db, num, TELEPHONE);
                    break;
            }
        }
        //不要忘记关闭database！
        db.close();
        return address;
    }

    private static String query(SQLiteDatabase db, String string, int type) {

        String area = "未知号码";
        String areaNum;

        switch (type) {
            //手机
            case 1:
                //substring方法得到String[start, end-1]
                areaNum= string.substring(0, 7);
                //sql语句可以连写
                Cursor cursor = db.rawQuery("select location from data2 " +
                                "where id = (select outkey from data1 where id = ?)",
                        new String[]{areaNum});
                //判断是不是空cursor
                if (cursor.moveToNext()) {
                    area = cursor.getString(0);
                }
                cursor.close();
                break;
            //长途座机
            case 2:
                if (string.length() >= 3 && string.length() <=12) {
                    if (string.startsWith("0")) {
                        areaNum = string.substring(1, 3);
                        Cursor cursorTel = db.rawQuery("select location from tel_location where _id = ?",
                                new String[]{areaNum});
                        if (cursorTel.moveToNext()) {
                            area = cursorTel.getString(0);
                        } else {
                            if (string.length() >= 4) {
                                cursorTel.close();
                                areaNum = string.substring(1, 4);
                                cursorTel = db.rawQuery("select location from tel_location where _id = ?",
                                        new String[]{areaNum});
                                if (cursorTel.moveToNext()) {
                                    area = cursorTel.getString(0);
                                }
                                cursorTel.close();
                            }
                        }
                    }
                }
        }
        return area;
    }

}
