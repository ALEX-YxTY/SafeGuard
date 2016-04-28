package com.exercise.yxty.safeguard.service;

import android.app.ActivityManager;
import android.app.PendingIntent;
import android.app.Service;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Intent;
import android.os.IBinder;
import android.text.format.Formatter;
import android.widget.RemoteViews;

import com.exercise.yxty.safeguard.R;
import com.exercise.yxty.safeguard.utils.SystemInfoUtils;
import com.exercise.yxty.safeguard.widget.NewAppWidget;

import java.util.Timer;
import java.util.TimerTask;

public class WidgetService extends Service {
    public WidgetService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onCreate() {
        super.onCreate();

        //进程管理器
        ActivityManager mAM = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
        //widget管理类
        final AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(this);
        //新建定时器
        Timer timer = new Timer();
        //TimeTask类内建单线程
        TimerTask timerTask = new TimerTask() {
            @Override
            public void run() {
                int count = SystemInfoUtils.getRunningProcessNumber(getApplicationContext());
                long availMem = SystemInfoUtils.getAvalMemory(getApplicationContext());
                RemoteViews remoteViews = new RemoteViews(getPackageName(), R.layout.new_app_widget);
                //remoteView设定textView方式与本地view不一样
                remoteViews.setTextViewText(R.id.tv_process_count, "运行进程：" + count + "个");
                remoteViews.setTextViewText(R.id.tv_memory_remain, "可用内存：" +
                        Formatter.formatFileSize(getApplicationContext(), availMem));
                //得到组件名
                ComponentName name = new ComponentName(getApplicationContext(),NewAppWidget.class);
                //设置点击工作
                Intent intent = new Intent();
                intent.setAction("safeguard.process.clear");
                //建立延时intent（PendingIntent）,requestCode 暂时无用，设为0，flag也为0，不过滤
                PendingIntent pendingIntent = PendingIntent.getBroadcast(getApplicationContext(),
                        0, intent, 0);
                remoteViews.setOnClickPendingIntent(R.id.bt_clear_process, pendingIntent);

                //通过组件名更新view
                appWidgetManager.updateAppWidget(name, remoteViews);
            }
        };
        //设置定时开始，延时0毫秒后执行，间隔10秒
        timer.schedule(timerTask, 0, 10000);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}
