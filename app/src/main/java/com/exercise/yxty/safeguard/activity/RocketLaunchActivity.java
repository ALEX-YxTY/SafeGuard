package com.exercise.yxty.safeguard.activity;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.view.animation.AlphaAnimation;
import android.widget.ImageView;

import com.exercise.yxty.safeguard.R;

/**
 * Created by Administrator on 2016/1/30.
 */
public class RocketLaunchActivity extends Activity {

    ImageView ivBottom;
    ImageView ivTop;
    AlphaAnimation aa;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.rocket_launch);

        initialView();
    }

    private void initialView() {
        ivBottom = (ImageView) findViewById(R.id.iv_bottom);
        ivTop = (ImageView) findViewById(R.id.iv_top);

        aa = new AlphaAnimation(0.3f, 1.0f);
        aa.setDuration(800);
        aa.setFillAfter(true);

        ivBottom.startAnimation(aa);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                ivTop.startAnimation(aa);
            }
        }, 300);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                RocketLaunchActivity.this.finish();
            }
        }, 1300);
    }
}
