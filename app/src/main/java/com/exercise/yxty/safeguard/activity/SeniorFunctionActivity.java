package com.exercise.yxty.safeguard.activity;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.Telephony;
import android.util.Log;
import android.util.Xml;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.exercise.yxty.safeguard.R;
import com.exercise.yxty.safeguard.utils.SmsUtils;

import org.xmlpull.v1.XmlSerializer;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by Administrator on 2016/1/27.
 */
public class SeniorFunctionActivity extends Activity {

    ProgressBar backupProgress;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.senior_function_activity);

        initialView();

    }

    private void initialView() {
        backupProgress = (ProgressBar) findViewById(R.id.pb_sms_backup);
    }

    //进入归属地查询页面
    public void addressQuery(View view) {
        startActivity(new Intent(SeniorFunctionActivity.this, AddressQueryActivity.class));
    }

    //开始备份短信
    public void smsBackup(View view) {
        //通过AsyncTask实现
        SmsbackupAsyncTask smsbackupAsyncTask = new SmsbackupAsyncTask();
        smsbackupAsyncTask.execute(new Context[]{this});
        //通过开线程及runOnUiThread的方法实现
//        new Thread() {
//            @Override
//            public void run() {
//                final String result = SmsUtils.smsBackup(SeniorFunctionActivity.this);
//                SeniorFunctionActivity.this.runOnUiThread(new Runnable() {
//                    @Override
//                    public void run() {
//                        Toast.makeText(SeniorFunctionActivity.this,result,Toast.LENGTH_SHORT).show();
//                    }
//                });
//            }
//        }.start();

    }

    private class SmsbackupAsyncTask extends AsyncTask<Context, Integer, String> {

        @Override
        protected void onPreExecute() {
            backupProgress.setVisibility(View.VISIBLE);
        }


        @Override
        protected void onPostExecute(String s) {
            Toast.makeText(SeniorFunctionActivity.this,s,Toast.LENGTH_SHORT).show();
            backupProgress.setVisibility(View.GONE);
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            backupProgress.setMax(values[1]);
            backupProgress.setProgress(values[0]);
        }

        @Override
        protected void onCancelled() {
            Toast.makeText(SeniorFunctionActivity.this,"备份已取消",Toast.LENGTH_SHORT).show();
            File file = new File(Environment.getExternalStorageDirectory(), "smsBackup.xml");
            file.delete();
            backupProgress.setVisibility(View.GONE);
        }

        @Override
        protected String doInBackground(Context... params) {
            //通过接口的引入实现回调
            String result = SmsUtils.smsBackup(params[0], new SmsUtils.UpdateProgress() {
                @Override
                public void onProgress(int progress, int total) {
                    publishProgress(new Integer[]{progress, total});
                }
            });

            return result;
        }
    }

    public void contactShortcut(View view) {
        startActivity(new Intent(SeniorFunctionActivity.this, ShortcutManagerActivity.class));
    }

    public void lockApp(View view) {
        SharedPreferences mSP = getSharedPreferences("config", MODE_PRIVATE);
        if (mSP.getString("lockAppPass", "undefined").equals("undefined")) {
            startActivity(new Intent(SeniorFunctionActivity.this, LockAppPasswordActivity.class));
        }else{
            startActivity(new Intent(SeniorFunctionActivity.this, LockAppActivity.class));
        }
    }
}
