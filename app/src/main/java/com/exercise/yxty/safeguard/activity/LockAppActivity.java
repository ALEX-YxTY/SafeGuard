package com.exercise.yxty.safeguard.activity;

import android.content.pm.PackageManager;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.widget.Button;

import com.exercise.yxty.safeguard.R;
import com.exercise.yxty.safeguard.beans.LockAppInfo;
import com.exercise.yxty.safeguard.db.LockAppDAO;
import com.exercise.yxty.safeguard.engine.LockAppInfos;
import com.exercise.yxty.safeguard.fragment.LockAppFragment2;
import com.exercise.yxty.safeguard.fragment.UnlockAppFragment2;
import com.exercise.yxty.safeguard.fragment.appFragment;

import java.util.ArrayList;
import java.util.List;

//implement接口是为了向childFragment传递数据
public class LockAppActivity extends FragmentActivity implements appFragment.DataCommunicationInterface{

    FragmentManager mFM;
    UnlockAppFragment2 unlockAppFragment;
    LockAppFragment2 lockAppFragment;
    LockAppDAO lockAppDAO;

    Button btLeft;
    Button btRight;

    List<LockAppInfo> lockApps;
    List<LockAppInfo> unlockApps;
    //页面跳转标记
    int flag;

    //通过handler形式执行操作是为了避免子线程处理数据与主线程UI异步的问题
    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                //点击左边页面
                case 1:
                    FragmentTransaction transactionLeft = mFM.beginTransaction();
                    transactionLeft.replace(R.id.rl_fragment, unlockAppFragment, "unlock").commit();
                    break;
                //点击右边页面
                case 2:
                    FragmentTransaction transactionRight = mFM.beginTransaction();
                    transactionRight.replace(R.id.rl_fragment, lockAppFragment, "lock").commit();
                    break;
                //初次启动
                default:
                    initialView();
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lock_app);

        mFM = getSupportFragmentManager();
        lockAppDAO = new LockAppDAO(this);
        unlockAppFragment = new UnlockAppFragment2();
        lockAppFragment = new LockAppFragment2();
        initialData();
    }

    private void initialData() {

        //将获取数据放入子线程，减小UIthread负担，便于UI工作
        new Thread(){
            @Override
            public void run() {
                super.run();

                /**
                 * 初始化已上锁程序列表和未上锁程序列表
                 * 因为可能数据库保存程序已经删除，所以每次都要获取所有程序然后在与数据库比对
                 */
                List<LockAppInfo> totalApps = LockAppInfos.getAppInfos(LockAppActivity.this);
                lockApps = new ArrayList<>();
                unlockApps = new ArrayList<>();
                for (LockAppInfo appInfo : totalApps) {
                    if (lockAppDAO.isLocked(appInfo.getPackageName())) {
                        lockApps.add(appInfo);
                    } else {
                        unlockApps.add(appInfo);
                    }
                }

                //btLeft == null 表示尚未初始化，是第一次启动
                if (btLeft == null) {
                    handler.sendEmptyMessage(0);
                //表示要进入左页面
                }else if (flag == 1) {
                    handler.sendEmptyMessage(1);
                //表示要进入右页面
                }else if (flag == 2) {
                    handler.sendEmptyMessage(2);
                }
            }
        }.start();

    }

    private void initialView() {
        btLeft = (Button) findViewById(R.id.bt_left);
        btRight = (Button) findViewById(R.id.bt_right);
        //开启事务
        FragmentTransaction transaction = mFM.beginTransaction();
        transaction.replace(R.id.rl_fragment, unlockAppFragment, "unlock").commit();
    }

    public void clickLeft(View view) {
        if (!unlockAppFragment.isVisible()) {

            btLeft.setBackgroundResource(R.drawable.tab_left_pressed);
            btRight.setBackgroundResource(R.drawable.tab_right_default);
            //点击左页面
            flag = 1;

            //重新获取数据
            initialData();

        }
    }

    public void clickRight(View view) {
        if (!lockAppFragment.isVisible()) {

            btRight.setBackgroundResource(R.drawable.tab_right_pressed);
            btLeft.setBackgroundResource(R.drawable.tab_left_default);
            //点击右页面
            flag = 2;

            //重新获取数据
            initialData();

        }
    }


    //接口回传上锁程序方法
    @Override
    public List<LockAppInfo> getLockAppInfos() {
        return lockApps;
    }

    //接口回传未上锁程序方法
    @Override
    public List<LockAppInfo> getUnlockAppInfos() {
        return unlockApps;
    }
}
