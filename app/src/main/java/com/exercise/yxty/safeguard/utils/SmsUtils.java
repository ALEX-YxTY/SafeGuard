package com.exercise.yxty.safeguard.utils;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;
import android.util.Xml;

import org.xmlpull.v1.XmlSerializer;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by Administrator on 2016/2/16.
 */
public class SmsUtils {

    //接口的引入是为了实现回调，此接口的方法将在正式方法中调用，将相关参数传入接口，通过回调，由用户自定义操作；
    public interface UpdateProgress {
        void onProgress(int progress,int total);
    }


    public static String smsBackup(Context context, UpdateProgress updateProgress) {
        //判断sd卡是否装载
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            //用contentResolver处理contentProvider
            ContentResolver cr = context.getContentResolver();
            //XmlSerializer的创建方法比较特别
            XmlSerializer xmlSerializer = Xml.newSerializer();
            //创建输出流，提供给xmlSerializer
            File file = new File(Environment.getExternalStorageDirectory(), "smsBackup.xml");
            try {
                FileOutputStream fos = new FileOutputStream(file);
                xmlSerializer.setOutput(fos, "utf-8");

                Uri uri = Uri.parse("content://sms/");//注意content要小写
                Cursor cursor = cr.query(uri, new String[]{"address",
                        "date", "type", "body"}, null, null, null);
                int count = cursor.getCount();
                int completed = 0;
                //开始写入xml
                xmlSerializer.startDocument("utf-8", true);
                xmlSerializer.startTag(null, "smsSet");
                xmlSerializer.attribute(null, "count", Integer.toString(count));
                while (cursor.moveToNext()) {
                    xmlSerializer.startTag(null, "sms");

                    xmlSerializer.startTag(null, "address");
                    xmlSerializer.text(cursor.getString(0));
                    xmlSerializer.endTag(null, "address");

                    xmlSerializer.startTag(null, "date");
                    xmlSerializer.text(cursor.getString(1));
                    xmlSerializer.endTag(null, "date");

                    xmlSerializer.startTag(null, "type");
                    xmlSerializer.text(cursor.getString(2));
                    xmlSerializer.endTag(null, "type");

                    xmlSerializer.startTag(null, "body");
                    //------------------
                    // 避免特殊字符不能通过Serializer生成，导致程序提前中断
                    // -----------------
                    try {
                        String seed = "sms";
                        String encrypted = CryptUtils.encrypt(seed, cursor.getString(3));
                        Log.i("test", encrypted);
                        xmlSerializer.text(encrypted);
                    } catch (IllegalArgumentException e) {
                        xmlSerializer.text("");
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    xmlSerializer.endTag(null, "body");

                    xmlSerializer.endTag(null, "sms");

                    completed++;
                    //使用回调方法
                    updateProgress.onProgress(completed,count);
                }
                xmlSerializer.endTag(null, "smsSet");
                xmlSerializer.endDocument();

                cursor.close();
                fos.flush();
                fos.close();

                return "备份完成";
            } catch (FileNotFoundException e) {
                return "文件错误";
            } catch (IOException e) {
                return "备份出错";
            }

        } else {
            return "SD卡未装载";
        }
    }

}
