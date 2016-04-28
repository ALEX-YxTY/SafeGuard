package com.exercise.yxty.safeguard.service;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.graphics.PixelFormat;
import android.os.Environment;
import android.os.IBinder;
import android.os.RemoteException;
import android.support.annotation.Nullable;
import android.telecom.TelecomManager;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import com.android.internal.telephony.ITelephony;
import com.exercise.yxty.safeguard.R;
import com.exercise.yxty.safeguard.db.AddressDAO;
import com.exercise.yxty.safeguard.db.BlackListDAO;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;

/**
 * Created by Administrator on 2016/1/27.
 */
public class AddressQueryService extends Service {

    TelephonyManager tm;
    MyListener listen;
    SharedPreferences mPref;
    OutCallReceiver mCallReciver;
    WindowManager wm;
    View view;
    WindowManager.LayoutParams params;
    BlackListDAO blackListDAO;
    int[] bgs;

    //记录初始坐标值及变化坐标
    int startX, startY, endX, endY;
    //记录偏移量
    int deltX, deltY;
    //记录图片宽高
    int viewWidth, viewHeight;
    //记录屏幕宽高
    int screenWidth, screenHeight;


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        mPref = getSharedPreferences("config", MODE_PRIVATE);
        blackListDAO = new BlackListDAO(this);
        bgs = new int[]{R.drawable.call_locate_white,
                R.drawable.call_locate_orange, R.drawable.call_locate_blue,
                R.drawable.call_locate_gray, R.drawable.call_locate_green};

        //监听来电 使用TelephonyManager
        tm = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
        listen = new MyListener();
        tm.listen(listen, PhoneStateListener.LISTEN_CALL_STATE);

        //注册去电监听广播
        mCallReciver = new OutCallReceiver();
        IntentFilter intentFilter = new IntentFilter(Intent.ACTION_NEW_OUTGOING_CALL);
        intentFilter.setPriority(2147483647);
        registerReceiver(mCallReciver, intentFilter);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        tm.listen(listen, PhoneStateListener.LISTEN_NONE);
        unregisterReceiver(mCallReciver);

    }


    //来电监听
    class MyListener extends PhoneStateListener {
        @Override
        public void onCallStateChanged(int state, String incomingNumber) {
            super.onCallStateChanged(state, incomingNumber);
            switch (state) {
                case TelephonyManager.CALL_STATE_RINGING:
                    String mode = blackListDAO.find(incomingNumber);
                    Log.i("test", incomingNumber + ":" + mode);
                    if (mode.equals("1") || mode.equals("3")) {
                        endCall();
                    } else {
                        String address = AddressDAO.sqlAddress(incomingNumber);
                        Log.i("test", incomingNumber);
                        showAddressWidget(address);
                    }
                    break;
                default:
                    if (wm != null && view != null) {
                        wm.removeView(view);
                        view = null;
                    }
                    break;
            }

        }
    }

    private void endCall() {
        try {
            //通过类加载器加载ServiceManager
            Class<?> clazz = getClassLoader().loadClass("android.os.ServiceManager");
            //通过反射得到当前的方法
            Method method = clazz.getDeclaredMethod("getService", String.class);
            Log.i("test", "拿到method方法");

            IBinder iBinder = (IBinder) method.invoke(null, TELEPHONY_SERVICE);
            Log.i("test", "反射调用方法");

            ITelephony iTelephony = ITelephony.Stub.asInterface(iBinder);
            Log.i("test", "拿到iTelephony对象");

            iTelephony.endCall();
            Log.i("test", "调用方法");

        } catch (Exception e) {
            e.printStackTrace();
            Log.i("test", "出错了");
        }
    }

    //去电广播
    class OutCallReceiver extends BroadcastReceiver {


        @Override
        public void onReceive(Context context, Intent intent) {
            String number = getResultData();
            showAddressWidget(AddressDAO.sqlAddress(number));
            Log.i("test", number);
            Log.i("test", AddressDAO.sqlAddress(number));
        }
    }

    //显示widget悬浮窗
    private void showAddressWidget(String address) {

        wm = (WindowManager) this.getSystemService(Context.WINDOW_SERVICE);
        screenHeight = wm.getDefaultDisplay().getHeight();
        screenWidth = wm.getDefaultDisplay().getWidth();

        //设置layout属性
        params = new WindowManager.LayoutParams();
        params.height = WindowManager.LayoutParams.WRAP_CONTENT;
        params.width = WindowManager.LayoutParams.WRAP_CONTENT;
        params.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
                | WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON;
        params.format = PixelFormat.TRANSLUCENT;
        // 电话窗口。它用于电话交互（特别是呼入）。它置于所有应用程序之上，状态栏之下。
        params.type = WindowManager.LayoutParams.TYPE_PHONE;
        //设置中心在左上方，否则后面params.x与y无意义
        params.setTitle("Toast");

        params.gravity = Gravity.LEFT + Gravity.TOP;
        int lastX = mPref.getInt("AddressHintX", 0);
        int lastY = mPref.getInt("AddressHintY", 0);

        //设置初始left，top偏移量
        params.x = lastX;
        params.y = lastY;

        //拿到view对象
        view = View.inflate(this, R.layout.toast_address, null);

        int style = mPref.getInt("addressStyle", 0);

        // 根据存储的样式更新背景
        view.setBackgroundResource(bgs[style]);

        TextView tvText = (TextView) view.findViewById(R.id.tv_number);
        tvText.setText(address);

        //通过WindowManager.addView来显示view对象，按之前layout属性的布局
        wm.addView(view, params);

        //设置拖动侦听
        view.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                viewWidth = view.getWidth();
                viewHeight = view.getHeight();
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        startX = (int) event.getRawX();
                        startY = (int) event.getRawY();
                        break;
                    case MotionEvent.ACTION_MOVE:
                        endX = (int) event.getRawX();
                        endY = (int) event.getRawY();

                        deltX = endX - startX;
                        deltY = endY - startY;

                        params.x += deltX;
                        params.y += deltY;
                        //判断是否越界，如果越界，不作操作
                        if (params.x < 0) {
                            params.x = 0;
                        }

                        if (params.x > (screenWidth - viewWidth)) {
                            params.x = screenWidth - viewWidth;
                        }

                        if (params.y < 0) {
                            params.y = 0;
                        }

                        if (params.y > (screenHeight - viewHeight)) {
                            params.y = screenHeight - viewHeight;
                        }


                        wm.updateViewLayout(view, params);
                        startX = endX;
                        startY = endY;
                        break;
                    case MotionEvent.ACTION_UP:
                        SharedPreferences.Editor ed = mPref.edit();
                        ed.putInt("AddressHintX", params.x);
                        ed.putInt("AddressHintY", params.y);
                        ed.commit();
                        break;

                    default:
                        break;
                }
                return false;
            }
        });
    }

}
