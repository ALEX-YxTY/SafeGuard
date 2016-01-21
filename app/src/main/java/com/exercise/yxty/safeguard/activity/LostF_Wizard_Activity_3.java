package com.exercise.yxty.safeguard.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.exercise.yxty.safeguard.R;

/**
 * Created by Administrator on 2016/1/20.
 */
public class LostF_Wizard_Activity_3 extends Activity {

    Button btNext;
    Button btPreview;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.lost_found_wizard_3);
        btNext = (Button) findViewById(R.id.bt_next);
        btPreview = (Button) findViewById(R.id.bt_preview);


        btPreview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LostF_Wizard_Activity_3.this, LostF_Wizard_Activity_2.class));
                finish();
                overridePendingTransition(R.anim.anim_preview_in, R.anim.anim_preview_out);
            }
        });

        btNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LostF_Wizard_Activity_3.this, LostF_Wizard_Activity_4.class));
                finish();
                overridePendingTransition(R.anim.anim_in, R.anim.anim_out);
            }
        });
    }
}
