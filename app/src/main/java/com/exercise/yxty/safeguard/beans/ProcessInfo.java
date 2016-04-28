package com.exercise.yxty.safeguard.beans;

import android.graphics.drawable.Drawable;

/**
 * Created by Administrator on 2016/2/17.
 */
public class ProcessInfo {
    private String appName;
    private String packageName;
    private long memorySeize;
    private Drawable icon;
    private int pid;
    private boolean isUserProcess;
    private boolean isChecked;

    public ProcessInfo() {
    }

    public boolean isChecked() {
        return isChecked;
    }

    public void setChecked(boolean isChecked) {
        this.isChecked = isChecked;
    }

    public int getPid() {
        return pid;
    }

    public void setPid(int pid) {
        this.pid = pid;
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

    public long getMemorySeize() {
        return memorySeize;
    }

    public void setMemorySeize(long memorySeize) {
        this.memorySeize = memorySeize;
    }

    public Drawable getIcon() {
        return icon;
    }

    public void setIcon(Drawable icon) {
        this.icon = icon;
    }

    public boolean isUserProcess() {
        return isUserProcess;
    }

    public void setIsUserProcess(boolean isUserProcess) {
        this.isUserProcess = isUserProcess;
    }

    @Override
    public String toString() {
        return "ProcessInfo{" +
                "appName='" + appName + '\'' +
                ", packageName='" + packageName + '\'' +
                ", memorySeize='" + memorySeize + '\'' +
                ", icon=" + icon +
                ", pid=" + pid +
                ", isUserProcess=" + isUserProcess +
                ", isChecked=" + isChecked + '}';
    }
}
