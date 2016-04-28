package com.exercise.yxty.safeguard.activity;

import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.TrafficStats;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.format.Formatter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.exercise.yxty.safeguard.R;
import com.exercise.yxty.safeguard.beans.TrafficStatsInfo;

import java.text.Format;
import java.util.ArrayList;
import java.util.List;

public class TrafficStatsActivity extends AppCompatActivity {

    //view
    ListView lvData;
    ProgressBar pb;

    //data
    List<TrafficStatsInfo> dataList;

    //manager
    PackageManager mPM;
    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0:
                    pb.setVisibility(View.INVISIBLE);
                    lvData.setAdapter(new TrafficStatsAdapter());
            }
        }
    };



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        initialUI();
        initialData();
    }

    private void initialData() {
        mPM = getPackageManager();
        dataList = new ArrayList<>();
        new Thread(){
            @Override
            public void run() {
                super.run();
                List<PackageInfo> installedPackages = mPM.getInstalledPackages(9);
                for (PackageInfo packageInfo : installedPackages) {
                    long download = TrafficStats.getUidRxBytes(packageInfo.applicationInfo.uid);
                    long upload = TrafficStats.getUidTxBytes(packageInfo.applicationInfo.uid);
                    //列出流量不为0的数据
                    if (download>0 || upload > 0) {
                        try {

                            TrafficStatsInfo trafficStatsInfo = new TrafficStatsInfo();
                            trafficStatsInfo.setIcon(packageInfo.applicationInfo.loadIcon(mPM));
                            trafficStatsInfo.setAppName((String) packageInfo.applicationInfo.loadLabel(mPM));
                            trafficStatsInfo.setUid(packageInfo.applicationInfo.uid);
                            trafficStatsInfo.setDownload(download);
                            trafficStatsInfo.setUpload(upload);
                            dataList.add(trafficStatsInfo);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
                Log.i("test",""+dataList.size());
                handler.sendEmptyMessageDelayed(0, 500);
            }
        }.start();


    }

    private void initialUI() {
        setContentView(R.layout.activity_trafficstats);
        lvData = (ListView) findViewById(R.id.lv_data);
        pb = (ProgressBar) findViewById(R.id.pb);
    }

    private class TrafficStatsAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return dataList.size();
        }


        @Override
        public Object getItem(int position) {
            return dataList.get(position);
        }


        @Override
        public long getItemId(int position) {
            return position;
        }


        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder;
            if (convertView == null) {
                convertView = View.inflate(TrafficStatsActivity.this,
                        R.layout.activity_trafficstats_item, null);
                holder = new ViewHolder();
                holder.ivIcon = (ImageView) convertView.findViewById(R.id.iv_icon);
                holder.tvName = (TextView) convertView.findViewById(R.id.tv_app_name);
                holder.tvDown = (TextView)convertView. findViewById(R.id.tv_download);
                holder.tvUp = (TextView) convertView.findViewById(R.id.tv_upload);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            TrafficStatsInfo info = dataList.get(position);
            holder.ivIcon.setBackground(info.getIcon());
            holder.tvName.setText(info.getAppName());
            holder.tvDown.setText(Formatter.formatFileSize(TrafficStatsActivity.this, info.getDownload()));
            holder.tvUp.setText(Formatter.formatFileSize(TrafficStatsActivity.this,info.getUpload()));

            return convertView;
        }
    }

    static class ViewHolder{
        ImageView ivIcon;
        TextView tvName;
        TextView tvDown;
        TextView tvUp;
    }

}
