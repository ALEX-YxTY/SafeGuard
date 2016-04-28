package com.exercise.yxty.safeguard.fragment;

import android.graphics.drawable.Drawable;
import android.view.View;

import com.exercise.yxty.safeguard.R;
import com.exercise.yxty.safeguard.beans.LockAppInfo;

import java.util.List;

/**
 * Created by Administrator on 2016/2/26.
 */
public class UnlockAppFragment2 extends appFragment {
    @Override
    public List<LockAppInfo> getAppInfos() {
        List<LockAppInfo> appList = this.dataCommunication.getUnlockAppInfos();
        return appList;
    }

    @Override
    public String setBulletinText(int appCount) {
        return "未上锁程序（"+appCount+")个";
    }

    @Override
    protected void changeDateBase(LockAppInfo lockAppInfo) {
        this.lockAppDAO.add(lockAppInfo.getPackageName());
    }

    @Override
    protected Drawable getDrawable() {
        return getResources().getDrawable(R.drawable.image_lock_selector);
    }

}
