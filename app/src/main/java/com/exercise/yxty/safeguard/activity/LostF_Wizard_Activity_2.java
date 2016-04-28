package com.exercise.yxty.safeguard.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;

import com.exercise.yxty.safeguard.R;
import com.exercise.yxty.safeguard.views.SettingItemView;

/**
 * Created by Administrator on 2016/1/20.
 */
public class LostF_Wizard_Activity_2 extends BaseFlingActivity {

    SettingItemView sivSim;
    Button btNext;
    Button btPreview;
    SharedPreferences.Editor mED;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.lost_found_wizard_2);
        btNext = (Button) findViewById(R.id.bt_next);
        btPreview = (Button) findViewById(R.id.bt_preview);
        sivSim = (SettingItemView) findViewById(R.id.siv_sim);
        mED = mSp.edit();
        String simNum = mSp.getString("simNumber", null);
        if (!TextUtils.isEmpty(simNum)) {
            sivSim.checked(true);
            sivSim.setDesc("SIM卡已经绑定");
        }


        btPreview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPreviousPage();
            }
        });

        btNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showNextPage();
            }
        });

    }

    public void bindSim(View view) {
        if (!sivSim.isChecked()) {
            TelephonyManager tm = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
            String simNum = tm.getSimSerialNumber();
            mED.putString("simNumber", simNum).commit();
            sivSim.checked(true);
            sivSim.setDesc("SIM卡已经绑定");
        } else {
            mED.remove("simNumber").commit();
            sivSim.checked(false);
            sivSim.setDesc("SIM卡没有绑定");
        }
    }

    @Override
    public void showNextPage() {
        startActivity(new Intent(LostF_Wizard_Activity_2.this, LostF_Wizard_Activity_3.class));
        finish();
        overridePendingTransition(R.anim.anim_in, R.anim.anim_out);
    }

    @Override
    public void showPreviousPage() {
        startActivity(new Intent(LostF_Wizard_Activity_2.this, LostF_Wizard_Activity_1.class));
        finish();
        overridePendingTransition(R.anim.anim_preview_in, R.anim.anim_preview_out);

    }


}
