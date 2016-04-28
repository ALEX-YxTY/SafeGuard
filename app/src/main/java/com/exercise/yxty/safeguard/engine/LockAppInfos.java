package com.exercise.yxty.safeguard.engine;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;

import com.exercise.yxty.safeguard.beans.LockAppInfo;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2016/2/12.
 */
public class LockAppInfos {

    //取得所有非系统应用的lockappinfo的bean值
    public static List<LockAppInfo> getAppInfos(Context context) {
        PackageManager mPM = context.getPackageManager();
        // flag为辅助过滤标志，不过滤则设为0
        List<PackageInfo> packageInfos = mPM.getInstalledPackages(0);
        List<LockAppInfo> appInfos = new ArrayList<>();
        for (PackageInfo installedpackage : packageInfos) {


            //flag的具体属性@link ApplicationInfo类
            int flag = installedpackage.applicationInfo.flags;
            //因为flag用二进制数表示，所以此处判断用取与操作
            //如果取与等于零，说明两者不相等
            if ((flag & ApplicationInfo.FLAG_SYSTEM) == 0) {
                LockAppInfo appInfo = new LockAppInfo();
                Drawable icon = installedpackage.applicationInfo.loadIcon(mPM);
                String packageName = installedpackage.packageName;
                //获取程序名用applicationInfo.loadLabel 而不是 packageInfo.packageName
                String appName = (String) installedpackage.applicationInfo.loadLabel(mPM);


                appInfo.setIcon(icon);
                appInfo.setAppName(appName);
                appInfo.setPackageName(packageName);

                appInfos.add(appInfo);
            }
        }
        return appInfos;

    }
}
