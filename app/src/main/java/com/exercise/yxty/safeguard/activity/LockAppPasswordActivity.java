package com.exercise.yxty.safeguard.activity;

import android.content.ComponentName;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.exercise.yxty.safeguard.R;
import com.exercise.yxty.safeguard.utils.MD5Utils;

import java.security.spec.ECField;

public class LockAppPasswordActivity extends AppCompatActivity implements View.OnClickListener {

    EditText edPassword;
    SharedPreferences mSP;

    String packageName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //取得启动输入密码页面的intent中的内容
        String name = getIntent().getStringExtra("packageName");
        if (name != null) {
            packageName = name;
        }
        mSP = getSharedPreferences("config", MODE_PRIVATE);
        initialUI();
    }

    private void initialUI() {
        setContentView(R.layout.activity_lock_app_password);
        edPassword = (EditText) findViewById(R.id.et_password);
        TextView tvTitle = (TextView) findViewById(R.id.Title);
        Button btEnter = (Button) findViewById(R.id.bt_enter);
        Button bt1 = (Button) findViewById(R.id.bt1);
        Button bt2 = (Button) findViewById(R.id.bt2);
        Button bt3 = (Button) findViewById(R.id.bt3);
        Button bt4 = (Button) findViewById(R.id.bt4);
        Button bt5 = (Button) findViewById(R.id.bt5);
        Button bt6 = (Button) findViewById(R.id.bt6);
        Button bt7 = (Button) findViewById(R.id.bt7);
        Button bt8 = (Button) findViewById(R.id.bt8);
        Button bt9 = (Button) findViewById(R.id.bt9);
        Button bt0 = (Button) findViewById(R.id.bt0);
        Button btClearAll = (Button) findViewById(R.id.bt_clear_all);
        Button btBack = (Button) findViewById(R.id.bt_back);

        bt0.setOnClickListener(this);
        bt1.setOnClickListener(this);
        bt2.setOnClickListener(this);
        bt3.setOnClickListener(this);
        bt4.setOnClickListener(this);
        bt5.setOnClickListener(this);
        bt6.setOnClickListener(this);
        bt7.setOnClickListener(this);
        bt8.setOnClickListener(this);
        bt9.setOnClickListener(this);
        btClearAll.setOnClickListener(this);
        btBack.setOnClickListener(this);
        btEnter.setOnClickListener(this);

        edPassword.setInputType(InputType.TYPE_NULL);
        if (mSP.getString("lockAppPass", "undefined").equals("undefined")) {
            tvTitle.setText("请设置程序锁密码");
        }

    }


    @Override
    public void onClick(View v) {
        String result;
        switch (v.getId()) {
            case R.id.bt0:
                result = edPassword.getText().toString() + "0";
                edPassword.setText(result);
                break;
            case R.id.bt1:
                result = edPassword.getText().toString() + "1";
                edPassword.setText(result);
                break;
            case R.id.bt2:
                result = edPassword.getText().toString() + "2";
                edPassword.setText(result);
                break;
            case R.id.bt3:
                result = edPassword.getText().toString() + "3";
                edPassword.setText(result);
                break;
            case R.id.bt4:
                result = edPassword.getText().toString() + "4";
                edPassword.setText(result);
                break;
            case R.id.bt5:
                result = edPassword.getText().toString() + "5";
                edPassword.setText(result);
                break;
            case R.id.bt6:
                result = edPassword.getText().toString() + "6";
                edPassword.setText(result);
                break;
            case R.id.bt7:
                result = edPassword.getText().toString() + "7";
                edPassword.setText(result);
                break;
            case R.id.bt8:
                result = edPassword.getText().toString() + "8";
                edPassword.setText(result);
                break;
            case R.id.bt9:
                result = edPassword.getText().toString() + "9";
                edPassword.setText(result);
                break;
            case R.id.bt_back:
                int length = edPassword.getText().length();
                result = edPassword.getText().toString().substring(0, length - 1);
                edPassword.setText(result);
                break;
            case R.id.bt_clear_all:
                result = "";
                edPassword.setText(result);
                break;
            case R.id.bt_enter:
                result = edPassword.getText().toString();
                if (mSP.getString("lockAppPass", "undefined").equals("undefined")) {
                    mSP.edit().putString("lockAppPass", MD5Utils.md5Encode(result)).commit();
                    this.finish();
                } else if (MD5Utils.md5Encode(result).equals(mSP.getString("lockAppPass", "undefined"))) {
                    Intent intent = new Intent();
                    intent.setAction("com.exercise.yxty.safeguard.stopLock");
                    if (packageName != null) {
                        intent.putExtra("safeTemporary", packageName);
                    }
                    sendBroadcast(intent);
                    this.finish();
                } else {
                    Toast.makeText(this, "密码错误", Toast.LENGTH_SHORT).show();
                    edPassword.setText("");
                }
        }
    }

    @Override
    public void onBackPressed() {

    }
}
