package com.exercise.yxty.safeguard.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.exercise.yxty.safeguard.R;
import com.exercise.yxty.safeguard.service.AddressQueryService;
import com.exercise.yxty.safeguard.service.AppLockService;
import com.exercise.yxty.safeguard.service.CleanCacheService;
import com.exercise.yxty.safeguard.utils.SQLiteHelperUtil;
import com.exercise.yxty.safeguard.views.SettingChoiceView;
import com.exercise.yxty.safeguard.views.SettingItemView;


/**
 * Created by Administrator on 2016/1/20.
 */
public class SettingActivity extends Activity {

    private SharedPreferences sp;
    private SharedPreferences.Editor et;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.setting);

        sp = getSharedPreferences("config", MODE_PRIVATE);
        et = sp.edit();

        initialUpdate();
        initialShowAddress();
        initialChooseStyle();
        initialPosition();
        initialCleanCache();
        initialAntiVirus();
        initialAppLock();

        SQLiteHelperUtil helperUtil = new SQLiteHelperUtil(this, "ksdf.db", null, 1);
        helperUtil.getWritableDatabase();

    }

    //设置程序锁启动
    private void initialAppLock() {
        final SettingItemView sivLockApp = (SettingItemView) findViewById(R.id.siv_app_lock);
        //初始化勾选情况
        if (sp.getBoolean("LockApps", false)) {
            sivLockApp.checked(true);
        } else {
            sivLockApp.checked(false);
        }

        sivLockApp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (sivLockApp.isChecked()) {
                    sivLockApp.checked(false);
                    et.putBoolean("LockApps", false);
                    et.commit();
                    stopService(new Intent(SettingActivity.this, AppLockService.class));;
                } else {
                    sivLockApp.checked(true);
                    et.putBoolean("LockApps", true);
                    et.commit();
                    startService(new Intent(SettingActivity.this, AppLockService.class));
                }
            }
        });
    }

    //设置病毒库更新
    private void initialAntiVirus() {
        final SettingItemView sivAntiVirus = (SettingItemView) findViewById(R.id.siv_antivirus_update);
        //初始化勾选情况
        if (sp.getBoolean("AntiVirusUpdate", false)) {
            sivAntiVirus.checked(true);
        } else {
            sivAntiVirus.checked(false);
        }

        sivAntiVirus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (sivAntiVirus.isChecked()) {
                    sivAntiVirus.checked(false);
                    et.putBoolean("AntiVirusUpdate", false);
                    et.commit();
                } else {
                    sivAntiVirus.checked(true);
                    et.putBoolean("AntiVirusUpdate", true);
                    et.commit();
                }
            }
        });
    }


    //设置自动更新
    private void initialUpdate() {
        final SettingItemView sivUpdaet = (SettingItemView) findViewById(R.id.siv_update);
        //初始化勾选情况
        if (sp.getBoolean("update", true)) {
            sivUpdaet.checked(true);
        } else {
            sivUpdaet.checked(false);
        }
        //设置监听
        sivUpdaet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (sivUpdaet.isChecked()) {
                    sivUpdaet.checked(false);
                    et.putBoolean("update", false);
                    et.commit();
                } else {
                    sivUpdaet.checked(true);
                    et.putBoolean("update", true);
                    et.commit();
                }
            }
        });


    }

    //设置电话归属地显示
    private void initialShowAddress() {
        final SettingItemView sivAddress = (SettingItemView) findViewById(R.id.siv_address);
        //初始化勾选情况

        if (sp.getBoolean("showCallAddress", false)) {
            sivAddress.checked(true);
        } else {
            sivAddress.checked(false);
        }
        //设置监听
        sivAddress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (sivAddress.isChecked()) {
                    sivAddress.checked(false);
                    stopService(new Intent(SettingActivity.this, AddressQueryService.class));
                    et.remove("showCallAddress").commit();

                } else {
                    sivAddress.checked(true);
                    startService(new Intent(SettingActivity.this, AddressQueryService.class));
                    et.putBoolean("showCallAddress", true).commit();
                }
            }
        });
    }

    //设置归属地提示框风格
    private void initialChooseStyle() {
        final SettingChoiceView scvStyle = (SettingChoiceView) findViewById(R.id.scv_style);
//        ivChoice = (ImageView) findViewById(R.id.iv_choice);
        final String[] itemName = new String[]{"半透明", "活力橙", "卫士蓝", "金属灰", "苹果绿"};

        //初始化显示
        int style = sp.getInt("addressStyle", 0);
        scvStyle.setTitle("归属地提示框风格");
        scvStyle.setSubTitle(itemName[style]);

        scvStyle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int styleChoice = sp.getInt("addressStyle", 0);
                //显示单选Dialog
                AlertDialog.Builder builder = new AlertDialog.Builder(SettingActivity.this);
                builder.setIcon(R.drawable.dialog_logo);
                builder.setTitle("归属地提示窗风格");
                builder.setSingleChoiceItems(itemName, styleChoice, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        et.putInt("addressStyle", which).commit();
                        scvStyle.setSubTitle(itemName[which]);
                        dialog.dismiss();
                    }
                });
                builder.setNegativeButton("返回", null);
                builder.show();
            }
        });
    }

    //设置归属地提示框位置
    private void initialPosition() {
        SettingChoiceView scvPosition = (SettingChoiceView) findViewById(R.id.scv_positon);
        scvPosition.setTitle("归属地提示框位置");
        scvPosition.setSubTitle("设置归属地提示框的显示位置");

        scvPosition.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SettingActivity.this, SettingDragActivity.class));
            }
        });

    }

    //设置缓存清理悬浮窗
    private void initialCleanCache() {
        final SettingItemView sivCleanCache = (SettingItemView) findViewById(R.id.siv_clean_cache);
        if (sp.getBoolean("cleanCache", true)) {
            sivCleanCache.checked(true);
        } else {
            sivCleanCache.checked(false);
        }

        sivCleanCache.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (sivCleanCache.isChecked()) {
                    sivCleanCache.checked(false);
                    stopService(new Intent(SettingActivity.this, CleanCacheService.class));
                    et.putBoolean("cleanCache", false);
                    et.commit();
                } else {
                    sivCleanCache.checked(true);
                    startService(new Intent(SettingActivity.this, CleanCacheService.class));
                    et.putBoolean("cleanCache", true);
                    et.commit();
                }
            }
        });
    }
}


