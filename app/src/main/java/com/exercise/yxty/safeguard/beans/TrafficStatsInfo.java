package com.exercise.yxty.safeguard.beans;

import android.graphics.drawable.Drawable;

/**
 * Created by Administrator on 2016/2/12.
 */
public class TrafficStatsInfo {
    private Drawable icon;
    private String appName;
    private int uid;
    long download;
    long upload;

    public TrafficStatsInfo() {
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

    public int getUid() {
        return uid;
    }

    public void setUid(int uid) {
        this.uid = uid;
    }

    public long getDownload() {
        return download;
    }

    public void setDownload(long download) {
        this.download = download;
    }

    public long getUpload() {
        return upload;
    }

    public void setUpload(long upload) {
        this.upload = upload;
    }

    @Override
    public String toString() {
        return "TrafficStatsInfo{" +
                "icon=" + icon +
                ", appName='" + appName + '\'' +
                ", uid=" + uid +
                ", download=" + download +
                ", upload=" + upload +
                '}';
    }
}
