package com.exercise.yxty.safeguard.widget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.text.format.Formatter;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.TextView;
import android.widget.Toast;

import com.exercise.yxty.safeguard.R;
import com.exercise.yxty.safeguard.service.WidgetService;
import com.exercise.yxty.safeguard.utils.SystemInfoUtils;

/**
 * Implementation of App Widget functionality.
 */
public class NewAppWidget extends AppWidgetProvider {


    //启动时执行一次update,收到ACTION_APPWIDGET_UPDATE广播后执行
    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {

    }

    @Override
    public void onEnabled(Context context) {
        super.onEnabled(context);
        Intent intent = new Intent(context, WidgetService.class);
        //启动部件更新服务
        context.startService(intent);
    }

    @Override
    public void onDisabled(Context context) {
        super.onDisabled(context);
        Intent intent = new Intent(context, WidgetService.class);
        //关闭部件更新服务
        context.stopService(intent);
    }
}

