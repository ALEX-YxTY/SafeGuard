package com.exercise.yxty.safeguard.service;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.PixelFormat;
import android.graphics.drawable.AnimationDrawable;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.annotation.Nullable;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.widget.ImageView;
import android.widget.TextView;

import com.exercise.yxty.safeguard.R;
import com.exercise.yxty.safeguard.activity.RocketLaunchActivity;

/**
 * Created by Administrator on 2016/1/29.
 */
public class CleanCacheService extends Service {

    WindowManager wm;
    SharedPreferences mPref;
    WindowManager.LayoutParams params;
    ImageView view;
    AnimationDrawable ad;


    int screenHeight, screenWidth;
    int viewWidth,viewHeight;
    int startX, startY;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mPref = getSharedPreferences("config", MODE_PRIVATE);
        showWidget();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (wm != null && view != null) {
            wm.removeView(view);
            view = null;
        }
    }

    private void showWidget() {

        wm = (WindowManager) this.getSystemService(Context.WINDOW_SERVICE);
        screenHeight = wm.getDefaultDisplay().getHeight();
        screenWidth = wm.getDefaultDisplay().getWidth();

        //设置layout属性
        params = new WindowManager.LayoutParams();
        params.height = WindowManager.LayoutParams.WRAP_CONTENT;
        params.width = WindowManager.LayoutParams.WRAP_CONTENT;
        params.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
                | WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON;
        params.format = PixelFormat.TRANSLUCENT;
        // 电话窗口。它用于电话交互（特别是呼入）。它置于所有应用程序之上，状态栏之下。
        params.type = WindowManager.LayoutParams.TYPE_PHONE;
        //设置中心在左上方，否则后面params.x与y无意义
        params.setTitle("CleanCache");

        params.gravity = Gravity.LEFT + Gravity.TOP;
        int lastX = mPref.getInt("RocketX", 0);
        int lastY = mPref.getInt("RocketY", 0);

        //设置初始left，top偏移量
        params.x = lastX;
        params.y = lastY;

        //拿到view对象
//        view = View.inflate(this, R.layout.toast_address, null);
        view = new ImageView(this);

        // view对象设置帧动画并播放
        view.setBackgroundResource(R.drawable.rocket_anim);
        ad = (AnimationDrawable) view.getBackground();


        //通过WindowManager.addView来显示view对象，按之前layout属性的布局
        wm.addView(view, params);

        //设置拖动侦听
        view.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                viewWidth = view.getWidth();
                viewHeight = view.getHeight();
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        startX = (int) event.getRawX();
                        startY = (int) event.getRawY();
                        ad.start();
                        break;
                    case MotionEvent.ACTION_MOVE:
                        int endX = (int) event.getRawX();
                        int endY = (int) event.getRawY();

                        int deltX = endX - startX;
                        int deltY = endY - startY;

                        params.x += deltX;
                        params.y += deltY;
                        //判断是否越界，如果越界，不作操作
                        if (params.x < 0) {
                            params.x = 0;
                        }

                        if (params.x > (screenWidth - viewWidth)) {
                            params.x = screenWidth - viewWidth;
                        }

                        if (params.y < 0) {
                            params.y = 0;
                        }

                        if (params.y > (screenHeight - viewHeight)) {
                            params.y = screenHeight - viewHeight;
                        }


                        wm.updateViewLayout(view, params);
                        startX = endX;
                        startY = endY;
                        break;
                    case MotionEvent.ACTION_UP:
                        if (startX > screenWidth / 4 && startX < (screenWidth * 3 / 4) &&
                                startY > (screenHeight * 3 / 4)) {
                            Intent intent = new Intent(CleanCacheService.this,
                                    RocketLaunchActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(intent);
                            sendRocket();
                        }
                        SharedPreferences.Editor ed = mPref.edit();
                        ed.putInt("RocketX", params.x);
                        ed.putInt("RocketY", params.y);
                        ed.commit();
                        ad.stop();
                        break;

                    default:
                        break;
                }
                return false;
            }
        });
    }

    private void sendRocket() {
        params.x = screenWidth / 2 - viewWidth / 2;
        wm.updateViewLayout(view, params);

        new Thread() {
            @Override
            public void run() {
                int pos = 380;// 移动总距离
                for (int i = 0; i <= 10; i++) {

                    // 等待一段时间再更新位置,用于控制火箭速度
                    try {
                        Thread.sleep(50);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                    int y = pos - 38 * i;

                    Message msg = Message.obtain();
                    msg.arg1 = y;


                    mHandler.sendMessage(msg);
                }
            }
        }.start();

    }

    private Handler mHandler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            // 更新火箭位置
            int y = msg.arg1;
            params.y = y;
            wm.updateViewLayout(view, params);
        };
    };
}
