package com.cimcitech.forkliftdispatch;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.cimcitech.forkliftdispatch.model.VersionVo;
import com.cimcitech.forkliftdispatch.updateApk.*;
import com.cimcitech.forkliftdispatch.utils.Constants;
import com.cimcitech.forkliftdispatch.utils.GjsonUtil;
import com.cimcitech.forkliftdispatch.utils.ToastUtil;
import com.cimcitech.forkliftdispatch.utils.Urls;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.Call;

import static com.cimcitech.forkliftdispatch.updateApk.DeviceUtils.getVersionCode;
import static com.cimcitech.forkliftdispatch.updateApk.DeviceUtils.getVersionName;

public class AppVersionActivity extends AppCompatActivity {

    @Bind(R.id.version_tv)
    TextView version_name;
    @Bind(R.id.check_update)
    Button check_update;

    private String downUrl = "";
    private String destFileName = "";
    private VersionVo versionVo;
    private Handler uiHandler = null;
    private final int INIT_VIEW = 1001;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app_version);
        ButterKnife.bind(this);
        initHandler();
        version_name.setText("当前版本信息为：V " + getAppVersionName(this));
        this.findViewById(R.id.back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    @OnClick(R.id.check_update)
    public void checkUpdate(View view) {
        updateApp();
    }

    /**
     * 返回当前程序版本名
     */
    public static String getAppVersionName(Context context) {
        String versionName = "";
        try {
            // ---get the package info---
            PackageManager pm = context.getPackageManager();
            PackageInfo pi = pm.getPackageInfo(context.getPackageName(), 0);
            versionName = pi.versionName;
            if (versionName == null || versionName.length() <= 0) {
                return "";
            }
        } catch (Exception e) {
            Log.e("VersionInfo", "Exception", e);
        }
        return versionName;
    }

    public void updateApp() {
        OkHttpUtils.post()
                .url(Urls.UPDATE_APP)
                .addParams("start", "0")
                .addParams("limit", "10")
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        Log.e("LoginActivity", "error=" + e);
                        ToastUtil.showToast("请求失败，请检查网络是否可用");
                    }

                    @Override
                    public void onResponse(String response, int id) {
                        Log.e("LoginActivity", "token.response=" + response);
                        try {
                            versionVo = GjsonUtil.parseJsonWithGson(response, VersionVo.class);
                            if (versionVo.getTotal() > 0) {
                                Constants.updateAppUrl = versionVo.getRows().get(0).getUrl();
                                downUrl = versionVo.getRows().get(0).getUrl();
                                destFileName = getResources().getString(R.string.app_name) + getVersionName(Constants.CONTEXT) + "_" + getVersionCode(Constants.CONTEXT) + ".apk";
                                uiHandler.sendEmptyMessage(INIT_VIEW);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
    }

    private void initHandler() {
        uiHandler = new Handler() {
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case INIT_VIEW:
                        String version1 = versionVo.getRows().get(0).getVersion();
                        String version2 = getVersionName(Constants.CONTEXT);
                        if (!versionVo.getRows().get(0).getVersion().equals(getVersionName(Constants.CONTEXT))) {
                            downApk();
                        } else
                            ToastUtil.showToast("当前版本已经是最新版本");
                        break;
                }
            }
        };
    }

    /**
     * 下载 APK
     */
    private void downApk() {
        final ProgressDialog pDialog = new ProgressDialog(this);
        pDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        pDialog.setMessage("正在下载");
        pDialog.setCancelable(false);
        pDialog.show();

        TaskPool.myPool().post(new AsyncRunnable<File>() {
            @Override
            public void postForeground(File result) {

                pDialog.dismiss();
                if (result != null) {
                    installApk(AppVersionActivity.this, Uri.fromFile(result));
                } else {
                    Toast.makeText(AppVersionActivity.this, "下载文件出错", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public File run() {
                try {
                    return getFileFromServer(downUrl, pDialog, destFileName);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return null;
            }
        });
    }

    /**
     * 从服务器下载 apk
     *
     * @param urlPath 下载地址
     * @param pDialog 进度提示框
     * @return
     * @throws Exception 文件流异常
     */
    private static File getFileFromServer(String urlPath, ProgressDialog pDialog, String fileName) throws Exception {
        //如果相等的话表示当前的sdcard挂载在手机上并且是可用的
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            URL url = new URL(urlPath);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setConnectTimeout(5000);
            //获取到文件的大小
            int max = conn.getContentLength();
            pDialog.setMax(max);
            float fileSize = Float.valueOf(max) / (1024 * 1024);
            InputStream is = conn.getInputStream();
            File file = new File(Environment.getExternalStorageDirectory(), fileName);
            FileOutputStream fos = new FileOutputStream(file);
            BufferedInputStream bis = new BufferedInputStream(is);
            byte[] buffer = new byte[1024];
            int len;
            int total = 0;
            float showSize = 0;
            while ((len = bis.read(buffer)) != -1) {
                fos.write(buffer, 0, len);
                total += len;
                showSize = Float.valueOf(total) / (1024 * 1024);
                //获取当前下载量
                pDialog.setProgress(total);
                pDialog.setProgressNumberFormat(String.format("%.2fM/%.2fM", showSize, fileSize));
            }
            fos.close();
            bis.close();
            is.close();

            return file;
        } else {
            return null;
        }
    }

    /**
     * 安装 APK
     *
     * @param activity
     * @param fileUri
     */
    protected void installApk(Activity activity, Uri fileUri) {
        Intent intent = new Intent();
        //执行动作
        intent.setAction(Intent.ACTION_VIEW);
        // 如果不加无法正常显示安装过程和安装成功提示
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        //执行的数据类型
        intent.setDataAndType(fileUri, "application/vnd.android.package-archive");
        activity.startActivity(intent);
    }
}
