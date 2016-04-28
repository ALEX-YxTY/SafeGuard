package com.exercise.yxty.safeguard.beans;

import android.graphics.drawable.Drawable;
import android.widget.ImageView;

/**
 * Created by Administrator on 2016/2/12.
 */
public class AppInfo {
    private Drawable icon;
    private String appName;
    private String packageName;
    private String size;
    private boolean userApp;
    private boolean storageInSd;

    public AppInfo() {
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

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }

    public boolean isUserApp() {
        return userApp;
    }

    public void setUserApp(boolean userApp) {
        this.userApp = userApp;
    }

    public boolean isStorageInSd() {
        return storageInSd;
    }

    public void setStorageInSd(boolean storageInSd) {
        this.storageInSd = storageInSd;
    }

    @Override
    public String toString() {
        return "AppInfo: appName=" + appName + " ; " + " size=" + size ;
    }
}
