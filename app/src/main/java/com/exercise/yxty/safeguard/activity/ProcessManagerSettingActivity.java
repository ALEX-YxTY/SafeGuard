package com.exercise.yxty.safeguard.activity;

import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.exercise.yxty.safeguard.R;

public class ProcessManagerSettingActivity extends AppCompatActivity {

    SharedPreferences mSp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_process_manager_setting);

        mSp = getSharedPreferences("config",MODE_PRIVATE);
        initialUI();
    }

    private void initialUI() {
        //显示系统进程设置
        RelativeLayout rlShowSys = (RelativeLayout) findViewById(R.id.rl_show_system_process);
        final CheckBox cbShow = (CheckBox) rlShowSys.findViewById(R.id.cb_show);
        cbShow.setChecked(mSp.getBoolean("showSystemProcess",true));
        rlShowSys.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (cbShow.isChecked()) {
                    mSp.edit().putBoolean("showSystemProcess", false).commit();
                    cbShow.setChecked(false);
                } else {
                    mSp.edit().remove("showSystemProcess").commit();
                    cbShow.setChecked(true);
                }
            }
        });

        //清理内存记录显示
        TextView tvClearTime = (TextView) findViewById(R.id.tv_clear_log_time);
        TextView tvClearLog = (TextView) findViewById(R.id.tv_clear_log);

        String clearDate = mSp.getString("lastClearDate", "");
        String clearLog = mSp.getString("lastClearLog", "");

        tvClearTime.setText("上次清理时间：" + clearDate);
        tvClearLog.setText("上次清理结果：" + clearLog);
    }
}
