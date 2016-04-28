package com.exercise.yxty.safeguard.beans;

import android.graphics.drawable.Drawable;

/**
 * Created by Administrator on 2016/2/12.
 */
public class CacheInfo {
    private Drawable icon;
    private String appName;
    private String packageName;
    long cacheSize;

    public CacheInfo() {
    }

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
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

    public long getCacheSize() {
        return cacheSize;
    }

    public void setCacheSize(long cacheSize) {
        this.cacheSize = cacheSize;
    }

    @Override
    public String toString() {
        return "CacheInfo{" +
                "appName='" + appName + '\'' +
                ", cacheSize=" + cacheSize +
                '}';
    }
}
