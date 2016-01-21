package com.exercise.yxty.safeguard.activity;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.exercise.yxty.safeguard.R;
import com.exercise.yxty.safeguard.views.SettingItemView;


/**
 * Created by Administrator on 2016/1/20.
 */
public class SettingActivity extends Activity {

    private SettingItemView siv_updae;
    private SharedPreferences sp;
    private SharedPreferences.Editor et;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.setting);

        sp = getSharedPreferences("config", MODE_PRIVATE);
        et = sp.edit();
        siv_updae = (SettingItemView) findViewById(R.id.siv_update);

        if (sp.getBoolean("update", true)) {
            siv_updae.checked(true);
            siv_updae.setDesc("自动更新已开启");
        } else {
            siv_updae.checked(false);
            siv_updae.setDesc("自动更新已关闭");
        }

        siv_updae.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (siv_updae.isChecked()) {
                    siv_updae.checked(false);
                    siv_updae.setDesc("自动更新已关闭");
                    et.putBoolean("update", false);
                    et.commit();
                 } else {
                    siv_updae.checked(true);
                    siv_updae.setDesc("自动更新已开启");
                    et.putBoolean("update", true);
                    et.commit();
                }
            }
        });

    }
}
