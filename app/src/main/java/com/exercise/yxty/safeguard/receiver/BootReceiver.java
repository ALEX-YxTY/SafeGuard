package com.exercise.yxty.safeguard.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.telephony.SmsMessage;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.exercise.yxty.safeguard.db.BlackListDAO;
import com.exercise.yxty.safeguard.service.AppLockService;
import com.exercise.yxty.safeguard.service.LostFoundService;

import org.w3c.dom.Text;

/**
 * Created by Administrator on 2016/1/21.
 */
public class BootReceiver extends BroadcastReceiver {

    final int COMMAND_ALARM = 0;
    final int COMMAND_LOCATION = 1;
    final int COMMAND_WIPE_DATA = 2;
    final int COMMAND_LOCK_SCREEN = 3;


    SharedPreferences mSp;
    BlackListDAO blackListDAO;


    @Override
    public void onReceive(Context context, Intent intent) {

        blackListDAO = new BlackListDAO(context);

        String action = intent.getAction();
        if (action.equals("android.intent.action.BOOT_COMPLETED")) {
            mSp = context.getSharedPreferences("config", Context.MODE_PRIVATE);
            //开启找回服务
            if (mSp.getBoolean("LostFound", false)) {
//            TelephonyManager tm = (TelephonyManager) context.getSystemService(
//                    context.TELEPHONY_SERVICE);
//            String simCurrent = tm.getSimSerialNumber();
//            String simSaved = mSp.getString("simNumber", null);
//            if (simCurrent.equals(simSaved)) {
                Log.i("test", "sim卡无问题 + " + intent.getAction());
//            }
            }
            //根据配置开启程序锁服务
            if (mSp.getBoolean("LockApps", false)) {
                context.startService(new Intent(context, AppLockService.class));
            }
        } else if (action.equals("android.provider.Telephony.SMS_RECEIVED")) {
            Object[] ob = (Object[]) intent.getExtras().get("pdus");
            for (Object o : ob) {
                SmsMessage sms = SmsMessage.createFromPdu((byte[]) o);
                String number = sms.getOriginatingAddress();
                String messageContent = sms.getMessageBody();
                String mode = blackListDAO.find(number);
                if (messageContent.equals("#*alarm*#")) {
                    Intent serviceIntent = new Intent(context, LostFoundService.class);
                    serviceIntent.putExtra("command", COMMAND_ALARM);
                    context.startService(serviceIntent);

                    abortBroadcast();
                } else if (messageContent.equals("#*alarm*#")) {

                    abortBroadcast();

                } else if (messageContent.equals("#*wipedata*#") ||
                        messageContent.equals("#*lockscreen*#")) {

                    abortBroadcast();

                }else if(!TextUtils.isEmpty(mode) && (Integer.parseInt(mode)) >= 2) {
                    abortBroadcast();
                }

            }
        }

    }
}
