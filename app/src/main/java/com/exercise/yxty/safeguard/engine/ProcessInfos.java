package com.exercise.yxty.safeguard.engine;

import android.app.ActivityManager;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Debug;
import android.text.format.Formatter;

import com.exercise.yxty.safeguard.R;
import com.exercise.yxty.safeguard.beans.ProcessInfo;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2016/2/17.
 */
public class ProcessInfos {

    //因为有些进程有多个程序，此处只用第一个程序作为进程显示，保证进程总数与各分项数目相等
    public static List<ProcessInfo> getProcessInfos(Context context) {
        ActivityManager mAM = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        PackageManager mPM = context.getPackageManager();
        List<ProcessInfo> processInfos = new ArrayList<>();
        //得到所有进程的信息列表
        List<ActivityManager.RunningAppProcessInfo> processInfoList = mAM.getRunningAppProcesses();
        for (ActivityManager.RunningAppProcessInfo processInfo : processInfoList) {
            ProcessInfo process = new ProcessInfo();
            //拿到进程名称及pid号
            int pid = processInfo.pid;
            //获取进程的内存信息
            Debug.MemoryInfo[] memoryInfo = mAM.getProcessMemoryInfo(new int[]{pid});
            //获取进程占用内存的大小 单位byte
            long memoryInByte = memoryInfo[0].getTotalPrivateDirty() * 1024;
            //设置process的内存及pid
            process.setMemorySeize(memoryInByte);
            process.setPid(pid);
            try {
                String packageName = processInfo.pkgList[0];
                PackageInfo packageInfo = mPM.getPackageInfo(packageName, 0);

                //设置process的icon、appName、packageName、userProcess
                process.setPackageName(packageInfo.packageName);
                process.setAppName((String) packageInfo.applicationInfo.loadLabel(mPM));
                process.setIcon(packageInfo.applicationInfo.loadIcon(mPM));
                if ((packageInfo.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) != 0) {
                    process.setIsUserProcess(false);
                } else {
                    process.setIsUserProcess(true);
                }
                //默认不选中
                process.setChecked(false);

            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
            }
            //完成processInfo设置，添加进列表
            processInfos.add(process);

        }
        return processInfos;
    }
}
