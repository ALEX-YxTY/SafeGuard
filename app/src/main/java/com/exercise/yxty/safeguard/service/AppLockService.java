package com.exercise.yxty.safeguard.service;

import android.app.ActivityManager;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageInfo;
import android.database.ContentObserver;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;

import com.exercise.yxty.safeguard.activity.LockAppPasswordActivity;
import com.exercise.yxty.safeguard.beans.LockAppInfo;
import com.exercise.yxty.safeguard.db.LockAppDAO;
import com.exercise.yxty.safeguard.engine.LockAppInfos;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;

public class AppLockService extends Service {

    //标识
    boolean flag;
    LockAppReceiver receiver;
    List<String> lockApps;
    LockAppObserver observer;
    //暂时保护程序名
    String safeTemporary;

    public AppLockService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onCreate() {
        super.onCreate();
        lockApps = initialList();
        flag = true;
        startWatching();
        safeTemporary = "";

        //注册广播
        receiver = new LockAppReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction(Intent.ACTION_SCREEN_ON);
        filter.addAction(Intent.ACTION_SCREEN_OFF);
        filter.addAction("com.exercise.yxty.safeguard.stopLock");

        registerReceiver(receiver, filter);

        //注册观察者 Uri要以content://开头
        observer = new LockAppObserver(new Handler());
        getContentResolver().registerContentObserver(
                Uri.parse("Content://com.exercise.yxty.safeguard.lockapp"), true, observer);

    }

    private void startWatching() {
        new Thread(){
            @Override
            public void run() {
                super.run();
                try {
                    ActivityManager mAM = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
                    flag = true;
                    while (flag) {
                        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
                            List<ActivityManager.RunningTaskInfo> runningTasks = mAM.getRunningTasks(1);
                            String packageName = runningTasks.get(0).topActivity.getPackageName();
                            //获取当前Activity名称
//                            String activityName = runningTasks.get(0).topActivity.getClassName();
                            if (packageName.equals("com.huawei.android.launcher")) {
                                safeTemporary = "";
                            }
                            if (!packageName.equals(safeTemporary) && lockApps.contains(packageName)) {
                                Intent intent = new Intent(AppLockService.this, LockAppPasswordActivity.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                intent.putExtra("packageName", packageName);
                                startActivity(intent);
                                Log.i("test", "开启程序锁");
                            }else {
                                Log.i("test", "当前package为：" + packageName + ",程序锁未启动");
                            }
                            sleep(500);
                        } else {
                            Log.i("test", "启用第二方法");
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }.start();

    }

    //初始化数据集,获取数据库中的加锁程序
    private List<String> initialList() {
        LockAppDAO dao = new LockAppDAO(this);
        List<String> lockApps = new ArrayList<>();
        List<PackageInfo> installedPackages = this.getPackageManager().getInstalledPackages(0);
        for (PackageInfo packageInfo : installedPackages) {
            if (dao.isLocked(packageInfo.packageName)) {
                lockApps.add(packageInfo.packageName);
                Log.i("test", packageInfo.packageName);
            }
        }
        return lockApps;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.i("test", "服务关闭了");
        onPause();
        unregisterReceiver(receiver);
    }

    //暂停子线程
    private void onPause() {
        flag = false;
    }

    //重启子线程
    private void onResume() {
        if (flag == false) {
            startWatching();
        }
    }

    //接受锁屏及开启屏幕的广播接收者
    private class LockAppReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(Intent.ACTION_SCREEN_OFF)) {
                onPause();
            } else if (intent.getAction().equals(Intent.ACTION_SCREEN_ON)) {
                onResume();
            } else {
                safeTemporary = intent.getStringExtra("safeTemporary");
            }
        }
    }

    private class LockAppObserver extends ContentObserver {

        public LockAppObserver(Handler handler) {
            super(handler);
        }

        @Override
        public void onChange(boolean selfChange) {
            super.onChange(selfChange);
            lockApps = initialList();
        }
    }
}
