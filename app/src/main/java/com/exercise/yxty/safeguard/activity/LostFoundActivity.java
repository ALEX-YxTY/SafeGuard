package com.exercise.yxty.safeguard.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import com.exercise.yxty.safeguard.R;

/**
 * Created by Administrator on 2016/1/20.
 */
public class LostFoundActivity extends Activity {

    SharedPreferences mPref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.lost_found);

        mPref = getSharedPreferences("config", MODE_PRIVATE);
        if (!mPref.getBoolean("configed", false)) {
            startActivity(new Intent(LostFoundActivity.this, LostF_Wizard_Activity_1.class));
            this.finish();
        }
    }
}
