package com.exercise.yxty.safeguard.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.exercise.yxty.safeguard.R;

/**
 * Created by Administrator on 2016/1/20.
 */
public class LostFoundActivity extends Activity {

    SharedPreferences mPref;
    TextView safeContact;
    ImageView ivLock;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.lost_found);

        mPref = getSharedPreferences("config", MODE_PRIVATE);
        safeContact = (TextView) findViewById(R.id.tv_safecontact);
        ivLock = (ImageView) findViewById(R.id.iv_lock);


        if (!mPref.getBoolean("configed", false)) {
            startActivity(new Intent(LostFoundActivity.this, LostF_Wizard_Activity_1.class));
            this.finish();
        }
        String saveNum = mPref.getString("saveContact", null);
        if (!TextUtils.isEmpty(saveNum)) {
            safeContact.setText(saveNum);
        }

        if (mPref.getBoolean("LostFound", false)) {
            ivLock.setImageResource(R.drawable.lock);
        }else{
            ivLock.setImageResource(R.drawable.unlock);
        }
    }

    public void backToWizard(View view) {
        startActivity(new Intent(this, LostF_Wizard_Activity_1.class));
        finish();
    }
}
