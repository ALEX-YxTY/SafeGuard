package com.exercise.yxty.safeguard.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Layout;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;

import com.exercise.yxty.safeguard.R;

/**
 * Created by Administrator on 2016/1/20.
 */
public class LostF_Wizard_Activity_4 extends Activity {

    Button btNext;
    Button btPreview;
    LinearLayout ll_setting_click;
    CheckBox cb;
    SharedPreferences mPref;
    SharedPreferences.Editor mEd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.lost_found_wizard_4);
        btNext = (Button) findViewById(R.id.bt_next);
        btPreview = (Button) findViewById(R.id.bt_preview);
        ll_setting_click = (LinearLayout) findViewById(R.id.ll_setting_click);
        cb = (CheckBox) findViewById(R.id.cb);

        btPreview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LostF_Wizard_Activity_4.this, LostF_Wizard_Activity_3.class));
                finish();
                overridePendingTransition(R.anim.anim_preview_in, R.anim.anim_preview_out);

            }
        });

        btNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPref = getSharedPreferences("config", MODE_PRIVATE);
                mEd = mPref.edit();

                mEd.putBoolean("configed", true);
                mEd.commit();
                startActivity(new Intent(LostF_Wizard_Activity_4.this, LostFoundActivity.class));
                finish();
                overridePendingTransition(R.anim.anim_in, R.anim.anim_out);
            }
        });
    }
}
