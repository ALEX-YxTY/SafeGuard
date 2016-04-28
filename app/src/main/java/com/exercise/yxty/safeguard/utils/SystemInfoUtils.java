package com.exercise.yxty.safeguard.utils;

import android.app.ActivityManager;
import android.content.Context;

import java.util.List;

/**
 * Created by Administrator on 2016/2/24.
 */
public class SystemInfoUtils {

    //拿到当前进程总数
    public static int getRunningProcessNumber(Context context) {
        ActivityManager mAM = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningAppProcessInfo> runningAppProcessInfos =
                mAM.getRunningAppProcesses();
        int count = runningAppProcessInfos.size();
        return count;
    }

    //获取当前可用内存
    public static long getAvalMemory(Context context) {
        ActivityManager mAM = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        ActivityManager.MemoryInfo memoryInfo = new ActivityManager.MemoryInfo();
        mAM.getMemoryInfo(memoryInfo);
        return  memoryInfo.availMem;
    }

    //获取当前总内存
    public static long getTotalMemory(Context context) {
        ActivityManager mAM = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        ActivityManager.MemoryInfo memoryInfo = new ActivityManager.MemoryInfo();
        mAM.getMemoryInfo(memoryInfo);
        return  memoryInfo.totalMem;
    }
}
