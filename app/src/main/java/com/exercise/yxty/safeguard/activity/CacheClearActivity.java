package com.exercise.yxty.safeguard.activity;

import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.IPackageDataObserver;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageStats;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.os.RemoteException;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.format.Formatter;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import android.content.pm.IPackageStatsObserver;
import com.exercise.yxty.safeguard.R;
import com.exercise.yxty.safeguard.beans.CacheInfo;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

public class CacheClearActivity extends AppCompatActivity {

    //view
    ListView cacheList;
    TextView tvBulletin;
    ProgressBar pb;

    //manager
    PackageManager mPM;

    //data
    List<CacheInfo> cacheInfos;
    long totalCacheSize ;

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0:
                    pb.setVisibility(View.INVISIBLE);
                    cacheList.setAdapter(new CacheAdapter());


                    break;
                default:
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mPM = getPackageManager();

        initialUI();
    }

    @Override
    protected void onStart() {
        super.onStart();
        initialData();
    }

    private void initialUI() {
        setContentView(R.layout.activity_cache_clear);
        cacheList = (ListView) findViewById(R.id.lv_cache);
        tvBulletin = (TextView) findViewById(R.id.tv_bulletin);
        pb = (ProgressBar) findViewById(R.id.pb);
    }

    public void clearAll(View view) {
        try {

            Method[] methods = PackageManager.class.getMethods();
            for (Method method : methods) {

                //freeStorageSize值不能设太小，否则不清除，但担心溢出，所以设long.MAX-1
                if (method.getName().equals("freeStorageAndNotify")) {
                    method.invoke(mPM, new Object[]{Long.MAX_VALUE-1, new MyIPackageDataObserver()});
                    pb.setVisibility(View.VISIBLE);
                    initialData();
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void initialData() {
        totalCacheSize = 0;
        cacheInfos = new ArrayList<>();

        new Thread(){
            @Override
            public void run() {
                super.run();
                List<PackageInfo> installedPackages = mPM.getInstalledPackages(0);
                for (PackageInfo packageInfo : installedPackages) {
                    try {
                        /**
                         * 反射拿到方法需要AIDL参数，先建立AIDL再Rebuild就不会报错了
                         * 至于AIDL文件的具体内容，目前在源码中没找到，是在网上抄的
                         */
                        Method getPackageSizeInfo = PackageManager.class.getDeclaredMethod(
                                "getPackageSizeInfo",new Class[]{String.class, IPackageStatsObserver.class});
                        getPackageSizeInfo.invoke(mPM, new Object[]{packageInfo.packageName,
                                new MyIPackageStatsObserver(packageInfo)});
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }.start();
        handler.sendEmptyMessageDelayed(0, 2000);
    }


    private class MyIPackageStatsObserver extends IPackageStatsObserver.Stub {

        PackageInfo packageInfo;

        public MyIPackageStatsObserver(PackageInfo packageInfo) {
            this.packageInfo = packageInfo;
        }

        @Override
        public void onGetStatsCompleted(PackageStats pStats, boolean succeeded) throws RemoteException {
            long size = pStats.cacheSize;
            if (size > 4096 &&
                    (packageInfo.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM)==0) {
                CacheInfo cacheInfo = new CacheInfo();
                cacheInfo.setCacheSize(size);
                cacheInfo.setIcon(packageInfo.applicationInfo.loadIcon(mPM));
                cacheInfo.setAppName((String) packageInfo.applicationInfo.loadLabel(mPM));
                cacheInfo.setPackageName(packageInfo.packageName);
                cacheInfos.add(cacheInfo);
                totalCacheSize += size;
            }
        }
    }

    private class MyIPackageDataObserver extends IPackageDataObserver.Stub {

        @Override
        public void onRemoveCompleted(String packageName, boolean succeeded) throws RemoteException {
        }
    }

    private class CacheAdapter extends BaseAdapter {


        @Override
        public int getCount() {
            tvBulletin.setText("总计有缓存的程序：(" + cacheInfos.size() + ")个，总计" +
                    Formatter.formatFileSize(CacheClearActivity.this, totalCacheSize));
            return cacheInfos.size();
        }


        @Override
        public Object getItem(int position) {
            return cacheInfos.get(position);
        }


        @Override
        public long getItemId(int position) {
            return position;
        }


        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            ViewHolder holder;
            if (convertView == null) {
                convertView = View.inflate(CacheClearActivity.this,
                        R.layout.activity_cache_clear_item, null);
                holder = new ViewHolder();
                holder.ivIcon = (ImageView) convertView.findViewById(R.id.iv_icon);
                holder.ivClean = (ImageView) convertView.findViewById(R.id.iv_clean);
                holder.tvName = (TextView) convertView.findViewById(R.id.tv_app_name);
                holder.tvCacheSize = (TextView) convertView.findViewById(R.id.tv_app_cache);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            holder.ivIcon.setBackground(cacheInfos.get(position).getIcon());
            holder.tvName.setText(cacheInfos.get(position).getAppName());
            holder.tvCacheSize.setText(Formatter.formatFileSize(CacheClearActivity.this,
                    cacheInfos.get(position).getCacheSize()));
            holder.ivClean.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent detailsIntent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                    detailsIntent.addCategory(Intent.CATEGORY_DEFAULT);
                    detailsIntent.setData(Uri.parse("package:" + cacheInfos.get(position).getPackageName()));
                    startActivity(detailsIntent);
                }
            });

            return convertView;
        }
    }

    static class ViewHolder {
        ImageView ivIcon;
        TextView tvName;
        TextView tvCacheSize;
        ImageView ivClean;
    }
}
