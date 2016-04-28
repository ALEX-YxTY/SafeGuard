package com.exercise.yxty.safeguard.activity;

import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;

import com.exercise.yxty.safeguard.R;
import com.exercise.yxty.safeguard.db.AntivirusDAO;
import com.exercise.yxty.safeguard.utils.MD5Utils;

import java.util.List;

public class AntiVirusActivity extends AppCompatActivity {

    ProgressBar pb;
    LinearLayout llText;
    ScrollView svText;
    TextView textInitial;
    ImageView ivScan;
    RotateAnimation animation;

    PackageManager mPM;
    List<PackageInfo> packageInfoList;
    VirusThread thread;

    //发现病毒数
    int virus = 0;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        initialView();
    }

    private void initialView() {
        setContentView(R.layout.activity_anti_virus);
        textInitial = (TextView) findViewById(R.id.tv_initial);
        pb = (ProgressBar) findViewById(R.id.pb);
        svText = (ScrollView) findViewById(R.id.sv_text);
        llText = (LinearLayout) findViewById(R.id.ll_text);
        ivScan = (ImageView) findViewById(R.id.iv_scan);

    }

    private void checkForVirus() {
        mPM = getPackageManager();
        packageInfoList = mPM.getInstalledPackages(0);
        pb.setMax(packageInfoList.size());

        animation = new RotateAnimation(0f, 360f, Animation.RELATIVE_TO_SELF,
                0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        animation.setDuration(500);
        animation.setRepeatCount(-1);
        ivScan.startAnimation(animation);
        thread = new VirusThread();
        thread.start();
    }

    public void start(View view) {
        checkForVirus();
    }

    public void pause(View view) {
        try {
            if (thread != null) {
                if ((boolean)thread.isRunning) {
                    thread.onPause();
                } else {
                    thread.onResume();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private class VirusThread extends Thread {

        //运行，停止标记,true启动，false暂停
        Object isRunning = true;

        @Override
        public void run() {
            int progress = 0;
            super.run();
            for (final PackageInfo packageInfo : packageInfoList) {
                //在子线程中调用包含wait的方法
                pauseThread();
                String dir = packageInfo.applicationInfo.sourceDir;
                final String desc = AntivirusDAO.isVirus(MD5Utils.getFileMd5(dir));
                Log.i("test", "子线程");
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        TextView tv = new TextView(AntiVirusActivity.this);
                        if (desc != null) {
                            tv.setText(packageInfo.applicationInfo.loadLabel(mPM) + "   有病毒");
                            tv.setTextColor(Color.RED);
                            llText.addView(tv, 0);
                            virus++;
                        } else {
                            tv.setText(packageInfo.applicationInfo.loadLabel(mPM) + "   安全");
                            tv.setTextColor(Color.BLACK);
                            llText.addView(tv, 0);
                        }
//                            svText.fullScroll(ScrollView.FOCUS_DOWN);
                    }
                });
                progress++;
                if (progress%2 == 0) {
                    pb.setProgress(progress);
                }
            }
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (virus == 0) {
                        textInitial.setText("扫描结束，没有发现病毒");
                        textInitial.setTextColor(Color.BLACK);
                    } else {
                        textInitial.setText("扫描结束，共发现"+virus+"个病毒，请及时杀除");
                        textInitial.setTextColor(Color.RED);
                    }
                    ivScan.clearAnimation();
                }
            });

        }

        public void onPause() {
            isRunning = false;
            ivScan.clearAnimation();
            /**
             * 不能在此处用wait，因为此时该方法被调用在主线程，wait的是主线程
             * wait方法是将调用此方法的对象所在的线程暂停，并释放对象锁
             * Thread子类的方法，只有在run内部的才是在子线程中运行的
             */
//            isRunning.wait();

        }

        public void onResume() {
            synchronized (isRunning) {
                //notify方法是唤醒在此对象上wait的线程
                //notify方法也需要在锁定的对象上执行
                isRunning.notify();
                isRunning = true;
                ivScan.startAnimation(animation);
            }
        }

        //让线程wait方法，在子线程调用
        private void pauseThread () {
            synchronized (isRunning) {
                if (!(boolean) isRunning) {
                    try {
                        isRunning.wait();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

}
