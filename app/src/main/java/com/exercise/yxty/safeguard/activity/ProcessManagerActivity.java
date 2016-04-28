package com.exercise.yxty.safeguard.activity;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.format.Formatter;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.exercise.yxty.safeguard.R;
import com.exercise.yxty.safeguard.beans.ProcessInfo;
import com.exercise.yxty.safeguard.engine.ProcessInfos;
import com.exercise.yxty.safeguard.utils.TimeUtils;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ProcessManagerActivity extends Activity {

    ListView lvProcess;
    List<ProcessInfo> userProcess;
    List<ProcessInfo> systemProcess;

    ProcessAdapter processAdapter;
    ActivityManager activityManager;
    SharedPreferences mSp;

    //用于记录进程数及剩余内存，总内存信息
    int runningProcess;
    long memoryLeft;
    long memoryAll;
    TextView tvProcess;
    TextView tvRam;

    @Override

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_process_manager);
        mSp = getSharedPreferences("config", MODE_PRIVATE);

        initialUI();

    }

    //返回该页面时根据设置内容刷新界面


    @Override
    protected void onRestart() {
        super.onRestart();

        //为了不看出重启进程，返回页面时不再重新获取数据，仅仅根据现有dataList做更新
        runningProcess = mSp.getBoolean("showSystemProcess", true) ?
                (userProcess.size() + systemProcess.size()) : userProcess.size();
        tvProcess.setText("运行中进程" + runningProcess + "个");
        processAdapter.notifyDataSetChanged();

//        initialData();
    }

    private void initialUI() {
        lvProcess = (ListView) findViewById(R.id.lv_process);
        tvProcess = (TextView) findViewById(R.id.tv_process_count);
        tvRam = (TextView) findViewById(R.id.tv_ram_remain);
        final TextView tvHover = (TextView) findViewById(R.id.tv_hover);

        //获取进程数及剩余内存信息
        activityManager = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
        tvProcess.setText("运行中进程" + " " + "个");

        ActivityManager.MemoryInfo memory = new ActivityManager.MemoryInfo();
        activityManager.getMemoryInfo(memory);
        memoryLeft = memory.availMem;
        memoryAll = memory.totalMem;
        String memoryRemain = Formatter.formatFileSize(this, memoryLeft);
        String memoryTotal = Formatter.formatFileSize(this, memoryAll);
        tvRam.setText("剩余/总内存：" + memoryRemain + "/" + memoryTotal);

        //设置滚动监听，控制悬浮块
        lvProcess.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {

            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                if (userProcess != null && systemProcess != null) {
                    if (firstVisibleItem < userProcess.size() + 1) {
                        tvHover.setText("用户进程（" + userProcess.size() + "）");
                    } else {
                        tvHover.setText("系统进程（" + systemProcess.size() + "）");
                    }
                } else {
                    tvHover.setText("载入中请稍后...");
                }
            }
        });

        initialData();
    }


    private void initialData() {
        //开启子线程下载数据
        new Thread() {

            @Override
            public void run() {
                List<ProcessInfo> processInfos = ProcessInfos.getProcessInfos(ProcessManagerActivity.this);
                userProcess = new ArrayList<>();
                systemProcess = new ArrayList<>();

                for (ProcessInfo processInfo : processInfos) {
                    if (processInfo.isUserProcess()) {
                        userProcess.add(processInfo);
                    } else {
                        systemProcess.add(processInfo);
                    }
                }
                ProcessManagerActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        processAdapter = new ProcessAdapter();
                        lvProcess.setAdapter(processAdapter);
                        runningProcess = mSp.getBoolean("showSystemProcess", true) ?
                                activityManager.getRunningAppProcesses().size() : userProcess.size();
                        tvProcess.setText("运行中进程" + runningProcess + "个");
                    }
                });
            }
        }.start();
    }


    private class ProcessAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            if (mSp.getBoolean("showSystemProcess", true)) {
                return userProcess.size() + systemProcess.size() + 2;
            } else {
                return userProcess.size() + 1;
            }
        }


        @Override
        public Object getItem(int position) {
            if (position < userProcess.size() + 1 && position > 0) {
                return userProcess.get(position - 1);
            } else if (position > userProcess.size() + 1) {
                return systemProcess.get(position - userProcess.size() - 2);
            }
            return null;
        }


        @Override
        public long getItemId(int position) {
            return position;
        }


        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            //判断是否为特殊行：标题行
            if (position == 0) {
                TextView textView = (TextView) View.inflate(ProcessManagerActivity.this, R.layout.hover_textview, null);
                textView.setText("用户进程（" + userProcess.size() + "）");
                return textView;
            } else if (position == userProcess.size() + 1) {
                TextView textView = (TextView) View.inflate(ProcessManagerActivity.this, R.layout.hover_textview, null);
                textView.setText("系统程序（" + systemProcess.size() + "）");
                return textView;
            } else {
                final ViewHolder holder;
                final ProcessInfo processInfo;
                if (position < userProcess.size() + 1) {
                    processInfo = userProcess.get(position - 1);
                } else {
                    processInfo = systemProcess.get(position - userProcess.size() - 2);
                }

                //convertView是系统自动生成，将上一个消失的view设为convertView，此处需要判断上一个消失的
                //如果是TextView，也就是标题列，那要重新赋值
                if (convertView == null || convertView instanceof TextView) {
                    convertView = View.inflate(ProcessManagerActivity.this, R.layout.process_list_item, null);
                    holder = new ViewHolder();
                    holder.ivIcon = (ImageView) convertView.findViewById(R.id.iv_pro_icon);
                    holder.tvName = (TextView) convertView.findViewById(R.id.tv_pro_name);
                    holder.tvMemory = (TextView) convertView.findViewById(R.id.tv_pro_memory);
                    holder.cbDelete = (CheckBox) convertView.findViewById(R.id.cb_pro_delete);
                    convertView.setTag(holder);
                } else {
                    holder = (ViewHolder) convertView.getTag();
                }

                if (processInfo.getPackageName().equals(ProcessManagerActivity.this.getPackageName())) {
                    holder.cbDelete.setVisibility(View.INVISIBLE);
                } else {
                    holder.cbDelete.setVisibility(View.VISIBLE);
                }

                holder.ivIcon.setBackground(processInfo.getIcon());
                holder.tvName.setText(processInfo.getAppName());
                holder.tvMemory.setText(Formatter.formatFileSize(ProcessManagerActivity.this,
                        processInfo.getMemorySeize()));
                holder.cbDelete.setChecked(processInfo.isChecked() ? true : false);
                holder.cbDelete.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (processInfo.isChecked()) {
                            holder.cbDelete.setChecked(false);
                            processInfo.setChecked(false);
                        } else {
                            holder.cbDelete.setChecked(true);
                            processInfo.setChecked(true);
                        }

                    }
                });

                return convertView;

            }
        }
    }

    static class ViewHolder {
        ImageView ivIcon;
        TextView tvName;
        TextView tvMemory;
        CheckBox cbDelete;
    }

    //全选
    public void selectAll(View view) {
        for (ProcessInfo processInfo : userProcess) {
            processInfo.setChecked(true);

        }
        if (mSp.getBoolean("showSystemProcess", true)) {
            for (ProcessInfo processInfo : systemProcess) {
                processInfo.setChecked(true);
            }
        }
        processAdapter.notifyDataSetChanged();
    }

    //反选
    public void selectReverse(View view) {
        for (ProcessInfo processInfo : userProcess) {
            processInfo.setChecked(processInfo.isChecked() ? false : true);
        }
        if (mSp.getBoolean("showSystemProcess", true)) {
            for (ProcessInfo processInfo : systemProcess) {
                processInfo.setChecked(processInfo.isChecked() ? false : true);
            }
        }
        processAdapter.notifyDataSetChanged();
    }

    //一键清理
    public void clear(View view) {
        int deleted = 0;
        long memoryCleared = 0;
        List<ProcessInfo> processDelete = new ArrayList<>();

        for (ProcessInfo processInfo : userProcess) {
            if (processInfo.isChecked()) {
                deleted++;
                memoryCleared += processInfo.getMemorySeize();
                activityManager.killBackgroundProcesses(processInfo.getPackageName());
                processDelete.add(processInfo);
            }
        }
        for (ProcessInfo processInfo : systemProcess) {
            if (processInfo.isChecked()) {
                deleted++;
                memoryCleared += processInfo.getMemorySeize();
                activityManager.killBackgroundProcesses(processInfo.getPackageName());
                processDelete.add(processInfo);
            }
        }
        userProcess.removeAll(processDelete);
        systemProcess.removeAll(processDelete);
        processDelete.clear();
        processAdapter.notifyDataSetChanged();
        //修改标题栏的数据信息
        runningProcess -= deleted;
        tvProcess.setText("运行中进程" + runningProcess + "个");
        memoryLeft += memoryCleared;
        tvRam.setText("剩余/总内存：" + Formatter.formatFileSize(ProcessManagerActivity.this,
                memoryLeft) + "/" + Formatter.formatFileSize(ProcessManagerActivity.this, memoryAll));

        //通过SP固化清理记录
        String clearDate = TimeUtils.getStringDate();
        String clearLog = "本次清理内存" + deleted + "个，" + "增加内存" +
                Formatter.formatFileSize(ProcessManagerActivity.this, memoryCleared);

        mSp.edit().putString("lastClearDate", clearDate).commit();
        mSp.edit().putString("lastClearLog", clearLog).commit();

        //弹出提示窗
        Toast.makeText(ProcessManagerActivity.this, clearLog, Toast.LENGTH_SHORT).show();
    }

    //设置
    public void setting(View view) {
        startActivity(new Intent(ProcessManagerActivity.this, ProcessManagerSettingActivity.class));
    }
}

