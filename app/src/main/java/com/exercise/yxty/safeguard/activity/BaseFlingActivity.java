package com.exercise.yxty.safeguard.activity;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.widget.Toast;

/**
 * Created by Administrator on 2016/1/23.
 */
public abstract class BaseFlingActivity extends Activity {

    GestureDetector mGestureDetector;
    SharedPreferences mSp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mSp = getSharedPreferences("config", MODE_PRIVATE);
        mGestureDetector = new GestureDetector(this,
                new GestureDetector.SimpleOnGestureListener(){
                    @Override
                    public boolean onFling(MotionEvent e1, MotionEvent e2,
                                           float velocityX, float velocityY) {

                        if (Math.abs(velocityX) < 150) {
                            Toast.makeText(BaseFlingActivity.this, "滑的太慢了", Toast.LENGTH_SHORT)
                                    .show();
                            return true;

                        } else if (Math.abs(e1.getRawY() - e2.getRawY()) > 100) {
                            Toast.makeText(BaseFlingActivity.this, "不能这么滑", Toast.LENGTH_SHORT)
                                    .show();
                            return true;

                        } else {

                            if (e1.getRawX() - e2.getRawX() > 200) {
                                showNextPage();
                                return true;
                            }

                            if (e2.getRawX() - e1.getRawX() > 200) {
                                showPreviousPage();
                                return true;
                            }
                        }

                        return super.onFling(e1, e2, velocityX, velocityY);
                    }
                });
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        mGestureDetector.onTouchEvent(event);
        return super.onTouchEvent(event);
    }

    public abstract void showNextPage();

    public abstract void showPreviousPage();

}
