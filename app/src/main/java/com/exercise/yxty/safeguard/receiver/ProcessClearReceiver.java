package com.exercise.yxty.safeguard.receiver;

import android.app.ActivityManager;
import android.appwidget.AppWidgetManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import java.util.List;

public class ProcessClearReceiver extends BroadcastReceiver {
    public ProcessClearReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        ActivityManager mAM = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningAppProcessInfo> ProcessInfos = mAM.getRunningAppProcesses();
        for (ActivityManager.RunningAppProcessInfo processInfo : ProcessInfos) {
            String[] packageNames = processInfo.pkgList;
            for (String packageName : packageNames) {
                mAM.killBackgroundProcesses(packageName);
            }
        }

        Intent updateIntent = new Intent();
        updateIntent.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
        context.sendBroadcast(updateIntent);
        Toast.makeText(context, "清理完成", Toast.LENGTH_SHORT).show();
    }
}
