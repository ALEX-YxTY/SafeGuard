package com.exercise.yxty.safeguard.engine;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.text.format.Formatter;

import com.exercise.yxty.safeguard.beans.AppInfo;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2016/2/12.
 */
public class AppInfos {

    public static List<AppInfo> getAppInfos(Context context) {
        PackageManager mPM = context.getPackageManager();
        // flag为辅助过滤标志，不过滤则设为0
        List<PackageInfo> packageInfos = mPM.getInstalledPackages(0);
        List<AppInfo> appInfos = new ArrayList<>();
        for (PackageInfo installpackage : packageInfos) {
            AppInfo appInfo = new AppInfo();

            Drawable icon = installpackage.applicationInfo.loadIcon(mPM);
            String packageName = installpackage.packageName;
            //获取程序名用applicationInfo.loadLabel 而不是 packageInfo.packageName
            String appName = (String) installpackage.applicationInfo.loadLabel(mPM);
            String dir = installpackage.applicationInfo.sourceDir;
            File file = new File(dir);
            //将文件大小转换为常规单位格式
            String size = Formatter.formatFileSize(context,file.length());

            appInfo.setIcon(icon);
            appInfo.setAppName(appName);
            appInfo.setPackageName(packageName);
            appInfo.setSize(size);

            //flag的具体属性@link ApplicationInfo类
            int flag = installpackage.applicationInfo.flags;
            //因为flag用二进制数表示，所以此处判断用取与操作
            //如果取与等于零，说明两者不相等
            if ((flag & ApplicationInfo.FLAG_SYSTEM)!=0) {
                appInfo.setUserApp(false);
            }else{
                appInfo.setUserApp(true);
            }

            if ((flag & ApplicationInfo.FLAG_EXTERNAL_STORAGE) != 0) {
                appInfo.setStorageInSd(true);
            } else {
                appInfo.setStorageInSd(false);
            }
            appInfos.add(appInfo);
        }

        return appInfos;
    }
}
