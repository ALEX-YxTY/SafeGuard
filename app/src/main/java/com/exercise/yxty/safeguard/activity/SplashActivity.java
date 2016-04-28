package com.exercise.yxty.safeguard.activity;

import android.app.ActionBar;
import android.app.AlertDialog;
import android.content.*;
import android.content.pm.*;
import android.net.Uri;
import android.os.*;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.*;

import com.exercise.yxty.safeguard.R;
import com.exercise.yxty.safeguard.beans.VirusInfo;
import com.exercise.yxty.safeguard.db.AntivirusDAO;
import com.exercise.yxty.safeguard.service.AddressQueryService;
import com.exercise.yxty.safeguard.service.AppLockService;
import com.exercise.yxty.safeguard.service.CleanCacheService;
import com.google.gson.Gson;
import com.lidroid.xutils.*;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;

import org.json.*;
import java.io.*;
import java.net.*;

import com.exercise.yxty.safeguard.utils.*;
import com.lidroid.xutils.http.client.HttpRequest;


public class SplashActivity extends AppCompatActivity {

    protected static final int CODE_UPDATE_DIALOG = 0;
    protected static final int CODE_URL_ERROR = 1;
    protected static final int CODE_NET_ERROR = 2;
    protected static final int CODE_JSON_ERROR = 3;
    protected static final int CODE_ENTER_HOME = 4;

    private String mVersionName ;
    private int mVersionCode ;
    private String mDescription ;
    private String mDownloadURL ;
    private TextView tv_versionName ;
    private TextView tv_progress ;

    private SharedPreferences sp;

    Long startTime;
    Long endTime;

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case CODE_URL_ERROR:
                    Toast.makeText(SplashActivity.this, "url错误", Toast.LENGTH_SHORT).show();
                    enterHome();
                    break;
                case CODE_NET_ERROR:
                    Toast.makeText(SplashActivity.this, "连接错误", Toast.LENGTH_SHORT).show();
                    enterHome();
                    break;
                case CODE_JSON_ERROR:
                    Toast.makeText(SplashActivity.this, "无法解析文件", Toast.LENGTH_SHORT).show();
                    enterHome();
                    break;
                case CODE_UPDATE_DIALOG:
                    showDialog();
                    break;
                case CODE_ENTER_HOME:
                    enterHome();
                    break;

            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash);

        initialView();
        downloadDB("address.db");
        downloadDB("antivirus.db");

        sp = getSharedPreferences("config", MODE_PRIVATE);

        if (sp.getBoolean("showCallAddress", false)) {
            startService(new Intent(this, AddressQueryService.class));
        }

        if (sp.getBoolean("cleanCache", false)) {
            startService(new Intent(this, CleanCacheService.class));
        }

        if (sp.getBoolean("LockApps", false)) {
            startService(new Intent(this, AppLockService.class));
        }

        if (sp.getBoolean("AntiVirusUpdate", false)) {
            updateVirusDatabase();
        }

        if (sp.getBoolean("update", true)) {
            checkVersion();
        } else {
            Message msg = mHandler.obtainMessage(CODE_ENTER_HOME);
            mHandler.sendMessageDelayed(msg, 2000);
        }
    }

    private void updateVirusDatabase() {
        new Thread(){
            @Override
            public void run() {
                super.run();
                HttpUtils httpUtils = new HttpUtils();
                String url = "http://192.168.1.101:8888/VirusUpdate.json";
                //使用HttpUtils的普通GET方法
                httpUtils.send(HttpRequest.HttpMethod.GET, url, new RequestCallBack<String>() {
                    @Override
                    public void onSuccess(ResponseInfo<String> responseInfo) {
                        try {
                            Gson gson = new Gson();
                            VirusInfo virusInfo = gson.fromJson(responseInfo.result, VirusInfo.class);
                            AntivirusDAO dao = new AntivirusDAO();
                            dao.add(virusInfo.getFileMd5(), virusInfo.getName(), virusInfo.getDesc());
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onFailure(HttpException e, String s) {
                        e.printStackTrace();
                    }
                });

            }
        }.start();
    }

    private void initialView() {
        RelativeLayout rlSplash = (RelativeLayout) findViewById(R.id.rl_splash);
        AlphaAnimation animation = new AlphaAnimation(0.3f, 1.0f);
        animation.setDuration(2000);
        rlSplash.startAnimation(animation);
        tv_versionName = (TextView) findViewById(R.id.tv_versionName);
        tv_versionName.setText("版本号：" + getVersionName());
    }

    private void downloadDB(final String dbName) {
        final File file = new File(getFilesDir(), dbName);
        Thread thread = new Thread(){
            @Override
            public void run() {
                super.run();
                if (!file.exists()) {
                    try {
                        InputStream is = getAssets().open(dbName);
                        FileOutputStream fos =  new FileOutputStream(file);
                        IOUtils.write(fos, is);
                    }  catch (IOException e) {
                        e.printStackTrace();
                    }
                }else {
                    System.out.println(dbName +"数据库已存在");
                }
            }
        };
        thread.start();
    }

    /**
     * 获取当前版本号
     * @return String 版本号信息
     */
    private String getVersionName() {
        PackageManager pm = getPackageManager();
        try {
            PackageInfo pi = pm.getPackageInfo(getPackageName(), 0);
            String versionName = pi.versionName;
            return versionName;
        } catch (PackageManager.NameNotFoundException e) {
            return "版本错误";
        }
    }

    /**
     * 获取当前版本号
     * @return int 版本号
     */
    private int getVersionCode() {
        PackageManager pm = getPackageManager();
        try {
            PackageInfo pi = pm.getPackageInfo(getPackageName(), 0);
            int versionCode = pi.versionCode;
            return versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            return -1;
        }

    }

    /**
     * 检查版本信息，有新版本弹窗提示下载，否则进入主页，此页面至少保持2秒
     */
    private void checkVersion() {
        startTime = System.currentTimeMillis();
        new Thread(){
            @Override
            public void run() {
                Message msg = Message.obtain();
                HttpURLConnection connection = null;
                try {
                    URL url = new URL("http://192.168.1.101:8888/versionDetail.json");
                    connection = (HttpURLConnection) url.openConnection();
                    connection.setRequestMethod("GET");
                    connection.setConnectTimeout(5000);
                    connection.setReadTimeout(5000);
                    connection.connect();
                    if (connection.getResponseCode() == 200) {
                        InputStream is = connection.getInputStream();
                        String result = StringUtils.readFromStream(is);
                        JSONObject jsonObject = new JSONObject(result);
                        mVersionName = jsonObject.getString("versionName");
                        mVersionCode = jsonObject.getInt("versionCode");
                        mDescription = jsonObject.getString("description");
                        mDownloadURL = jsonObject.getString("downloadURL");

                        if (mVersionCode > getVersionCode()) {
                            //弹窗AlertDialog，选择是否下载
                            msg.what = CODE_UPDATE_DIALOG;

                        } else {
                            //无新版本，直接进入主页
                            msg.what = CODE_ENTER_HOME;
                        }


                    } else {
                        //连接错误，直接进入主页
                        msg.what = CODE_NET_ERROR;
                    }
                } catch (JSONException e) {
                    //无法解析文件，直接进入主页
                    msg.what = CODE_JSON_ERROR;
                } catch (MalformedURLException e) {
                    //url错误，直接进入主页
                    msg.what = CODE_URL_ERROR;
                } catch (IOException e) {
                    //连接错误，直接进入主页
                    msg.what = CODE_NET_ERROR;
                } finally {
                    endTime = System.currentTimeMillis();
                    Long timeUsed = endTime-startTime;
                    if (timeUsed < 2000) {
                        try {
                            sleep(2000-endTime + startTime);
                        } catch (InterruptedException e) {}
                    }

                    mHandler.sendMessage(msg);

                    if (connection != null) {
                        connection.disconnect();
                    }
                }
            }
        }.start();
    }

    /**
     * 进入主页面
     */
    private void enterHome() {
        Intent intent = new Intent(this, HomeActivity.class);
        startActivity(intent);
        SplashActivity.this.finish();
    }


    /**
     * 显示升级提示弹窗
     */
    private void showDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("升级提醒:版本号 "+ mVersionName);
        builder.setMessage(mDescription);
        builder.setNegativeButton("以后再说", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                enterHome();
            }
        });
        builder.setPositiveButton("下载更新", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                downLoadUpdate();
            }
        });
        builder.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                enterHome();
            }
        });
        builder.show();
    }

    private void downLoadUpdate() {

        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED) ) {
            tv_progress = (TextView) findViewById(R.id.tv_progress);
            tv_progress.setVisibility(View.VISIBLE);
            HttpUtils httpUtils = new HttpUtils();
            String target = Environment.getExternalStorageDirectory() + "/safeguard_tt.apk";
            httpUtils.download(mDownloadURL, target, new RequestCallBack<File>() {

                @Override
                public void onStart() {
                    tv_progress.setText("开始下载");
                    super.onStart();
                }

                @Override
                public void onLoading(long total, long current, boolean isUploading) {
                    super.onLoading(total, current, isUploading);
                    if (!isUploading) {
                        tv_progress.setText("下载完成：" + current * 100 / total + "% ");
                    }
                }

                @Override
                public void onSuccess(ResponseInfo<File> responseInfo) {
                    Intent intent = new Intent();
                    intent.setAction("android.intent.action.VIEW");
                    intent.addCategory("android.intent.category.DEFAULT");
                    intent.setDataAndType(Uri.fromFile(responseInfo.result),
                            "application/vnd.android.package-archive");
                    startActivityForResult(intent, 0);
                }

                @Override
                public void onFailure(HttpException e, String s) {
                    Toast.makeText(SplashActivity.this, "下载失败", Toast.LENGTH_SHORT).show();
                    enterHome();
                }
            });

        } else {
            Toast.makeText(SplashActivity.this,"储存卡已满,请清除后下载",Toast.LENGTH_SHORT).show();
            enterHome();
        }


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        enterHome();
    }
}