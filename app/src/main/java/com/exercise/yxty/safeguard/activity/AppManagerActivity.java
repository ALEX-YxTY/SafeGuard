package com.exercise.yxty.safeguard.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.text.format.Formatter;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.exercise.yxty.safeguard.R;
import com.exercise.yxty.safeguard.beans.AppInfo;
import com.exercise.yxty.safeguard.engine.AppInfos;

import java.util.ArrayList;
import java.util.List;

public class AppManagerActivity extends AppCompatActivity implements View.OnClickListener {

    List<AppInfo> userApp, systemApp, appInstalled;
    ListView lvApp;
    PopupWindow popupWindow;
    AppInfo appClicked;
    AppDeleteReceiver receiver;

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            lvApp.setAdapter(new MyAppAdapter());
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app_manager);

        receiver = new AppDeleteReceiver();
        IntentFilter filter = new IntentFilter(Intent.ACTION_PACKAGE_REMOVED);
        filter.addDataScheme("package");
        registerReceiver(receiver, filter);

        initialView();
        initialData();
    }

    private void initialData() {
        new Thread(){
            @Override
            public void run() {
                appInstalled = AppInfos.getAppInfos(AppManagerActivity.this);
                userApp = new ArrayList<>();
                systemApp = new ArrayList<>();
                for (AppInfo appInfo : appInstalled) {
                    if (appInfo.isUserApp()) {
                        userApp.add(appInfo);
                    } else {
                        systemApp.add(appInfo);
                    }
                }
                handler.sendEmptyMessage(0);
            }
        }.start();
    }

    private void initialView() {
        TextView tvRom = (TextView) findViewById(R.id.tv_rom_remain);
        TextView tvSd = (TextView) findViewById(R.id.tv_sd_remain);
        lvApp = (ListView) findViewById(R.id.lv_app);
        final TextView tvHover = (TextView) findViewById(R.id.tv_hover);

        //通过Environment类拿到rom及sd卡剩余空间long值
        long romFreeSpace = Environment.getDataDirectory().getFreeSpace();
        tvRom.setText("内存可用： "+ Formatter.formatFileSize(this, romFreeSpace));
        //判断sd卡状态
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_REMOVED)) {
            tvSd.setText("SD卡未插入");
        }else{
            long sdFreeSpace = Environment.getExternalStorageDirectory().getFreeSpace();
            //通过Formatter类实现size的转换
            tvSd.setText("SD卡可用： "+ Formatter.formatFileSize(this, sdFreeSpace));
        }

        //设置滚动监听并重写滚动方法，根据滚动情况设置悬浮栏内容
        lvApp.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {

            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                //滚动时弹出框取消
                popupWindowDismiss();

                if (userApp != null && systemApp != null) {
                    if (firstVisibleItem < userApp.size() + 1) {
                        tvHover.setText("用户程序（" + userApp.size() + "）");
                    } else {
                        tvHover.setText("系统程序（" + systemApp.size() + "）");
                    }
                } else {
                    tvHover.setText("载入中，请稍后...");
                }
            }
        });
        lvApp.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //点击时弹出框取消
                popupWindowDismiss();

                if (position != 0 && position != userApp.size() + 1) {
                    View popupView = View.inflate(AppManagerActivity.this, R.layout.popup_app_function, null);
                    appClicked = (AppInfo) lvApp.getItemAtPosition(position);
                    ImageView ivRun = (ImageView) popupView.findViewById(R.id.iv_run);
                    ImageView ivDetails = (ImageView) popupView.findViewById(R.id.iv_details);
                    ImageView ivShare = (ImageView) popupView.findViewById(R.id.iv_share);
                    ImageView ivDelete = (ImageView) popupView.findViewById(R.id.iv_delete);

                    ivRun.setOnClickListener(AppManagerActivity.this);
                    ivDetails.setOnClickListener(AppManagerActivity.this);
                    ivShare.setOnClickListener(AppManagerActivity.this);
                    ivDelete.setOnClickListener(AppManagerActivity.this);

                    //-2表示包裹内容
                    popupWindow = new PopupWindow(popupView, -2, -2);
                    //用transparent设置popupWindow背景，才能设置出现动画
                    popupWindow.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                    //条目高度
                    int height = view.getHeight();
                    //有偏移量的popup显示方法
                    popupWindow.showAsDropDown(view, 200, -height);
                    //从资源文件加载animation的xml文件
                    Animation animation = AnimationUtils.loadAnimation(AppManagerActivity.this,
                            R.anim.anim_popup_app);
                    //view可以播放anim 而不是popupWindow对象播放
                    popupView.startAnimation(animation);
                }
            }
        });
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_run:
                //启动程序
                Intent runIntent = getPackageManager().
                        getLaunchIntentForPackage(appClicked.getPackageName());
                startActivity(runIntent);
                break;
            case R.id.iv_share:
                //分享 action：SEND， type：text/plain, extra：SUBJECT和TEXT
                //Intent.createChooser(intent,"title") 创建一个以title为名的程序选择器
                Intent shareIntent = new Intent(Intent.ACTION_SEND);
                shareIntent.setType("text/plain");
                shareIntent.putExtra("android.intent.extra.SUBJECT", "分享软件");
                shareIntent.putExtra("android.intent.extra.TEXT", "嘿，我正在使用" + appClicked.getAppName() +
                        ", 非常好用，推荐你也来尝试下！");
                startActivity(Intent.createChooser(shareIntent,"分享"));
                break;
            case R.id.iv_delete:
                //删除程序，action：ACTION_DELETE，data：package：packageName
                Intent deleteIntent = new Intent(Intent.ACTION_DELETE,
                        Uri.parse("package:" + appClicked.getPackageName()));
                startActivity(deleteIntent);
                break;
            case R.id.iv_details:
                //获取程序详情，action：Settings.ACTION_DETAILS_SETTINGS，category:CATEGORY_DEFAULT,
                //data:package:packageName
                Intent detailsIntent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                detailsIntent.addCategory(Intent.CATEGORY_DEFAULT);
                detailsIntent.setData(Uri.parse("package:" + appClicked.getPackageName()));
                startActivity(detailsIntent);
                break;
            default:
                break;
        }
    }

    private class MyAppAdapter extends BaseAdapter {


        @Override
        public int getCount() {
            return 2 + userApp.size() + systemApp.size();
        }


        @Override
        public Object getItem(int position) {
            if (position > 0 && position < userApp.size() + 1) {
                return userApp.get(position - 1);
            }else if (position > userApp.size() + 1) {
                return systemApp.get(position - userApp.size() - 2);
            } else {
                return null;
            }
        }


        @Override
        public long getItemId(int position) {
            return position;
        }


        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (position == 0) {
                TextView textView = (TextView) View.inflate(AppManagerActivity.this, R.layout.hover_textview, null);
                textView.setText("用户程序（" + userApp.size() + "）");
                return textView;
            }else if (position == userApp.size() + 1) {
                TextView textView = (TextView) View.inflate(AppManagerActivity.this, R.layout.hover_textview, null);
                textView.setText("系统程序（" + systemApp.size() + "）");
                return textView;
            } else {
                ViewHolder viewHolder;
                AppInfo appInfo;
                //先判断position以决定数据源
                if (position < userApp.size() + 1) {
                    appInfo = userApp.get(position-1);
                }else {
                    appInfo = systemApp.get(position - userApp.size() - 2);
                }

                //converView是自动加载的，加载对象是上一个消失的view
                //使用前判断convertView的类型
                if (convertView == null || convertView instanceof TextView) {
                    viewHolder = new ViewHolder();
                    View view = View.inflate(AppManagerActivity.this, R.layout.app_list_item, null);
                    viewHolder.icon = (ImageView) view.findViewById(R.id.iv_app_icon);
                    viewHolder.name = (TextView) view.findViewById(R.id.tv_app_name);
                    viewHolder.storage = (TextView) view.findViewById(R.id.tv_app_storage);
                    viewHolder.size = (TextView) view.findViewById(R.id.tv_app_size);
                    view.setTag(viewHolder);
                    convertView = view;
                } else {
                    viewHolder = (ViewHolder) convertView.getTag();
                }
                viewHolder.icon.setBackground(appInfo.getIcon());
                viewHolder.name.setText(appInfo.getAppName());
                viewHolder.size.setText(appInfo.getSize());
                if (appInfo.isStorageInSd()) {
                    viewHolder.storage.setText("SD卡");
                } else {
                    viewHolder.storage.setText("手机内存");
                }
                return convertView;
            }
        }
    }

    static class ViewHolder {
        ImageView icon;
        TextView name;
        TextView storage;
        TextView size;

    }

    class AppDeleteReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            Log.i("test", "有程序被卸载了");
            initialData();
        }
    }

    @Override
    protected void onDestroy() {
        popupWindowDismiss();
        unregisterReceiver(receiver);
        super.onDestroy();
    }

    private void popupWindowDismiss() {
        if (popupWindow != null && popupWindow.isShowing()) {
            popupWindow.dismiss();
            popupWindow = null;
        }
    }
}
