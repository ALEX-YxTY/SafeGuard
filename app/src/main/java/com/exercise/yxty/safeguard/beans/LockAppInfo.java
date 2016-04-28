package com.exercise.yxty.safeguard.beans;

import android.graphics.drawable.Drawable;

/**
 * Created by Administrator on 2016/2/12.
 */
public class LockAppInfo {
    private Drawable icon;
    private String appName;
    private String packageName;

    public LockAppInfo() {
    }

    public Drawable getIcon() {
        return icon;
    }

    public void setIcon(Drawable icon) {
        this.icon = icon;
    }

    public String getAppName() {
        return appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }


}
