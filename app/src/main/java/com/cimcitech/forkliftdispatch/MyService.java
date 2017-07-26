package com.cimcitech.forkliftdispatch;


import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

import com.cimcitech.forkliftdispatch.model.TaskVo;
import com.cimcitech.forkliftdispatch.model.VehicleVo;
import com.cimcitech.forkliftdispatch.utils.Constants;
import com.cimcitech.forkliftdispatch.utils.GjsonUtil;
import com.cimcitech.forkliftdispatch.utils.ToastUtil;
import com.cimcitech.forkliftdispatch.utils.Urls;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import java.util.Timer;
import java.util.TimerTask;

import okhttp3.Call;

/**
 * Created by cimcitech on 2017/6/13.
 */

public class MyService extends Service {

    public static final String TAG = "MyService";
    public static final String ACTION_UPDATE_TASK = "action.updateTask";
    public static final String ACTION_UPDATE_CAR = "action.updateCar";

    private MyBinder mBinder = new MyBinder();
    private TaskVo taskVo;
    private VehicleVo vehicleVo;
    private Timer timer;
    private TimerTask task;

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "onCreate() executed");
        timer = new Timer();
        task = new TimerTask() {
            @Override
            public void run() {

                if (Constants.driver != null) {
                    getTaskData();
                }
            }
        };
        timer.schedule(task, 5000, 5000);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "onStartCommand() executed");
        return super.onStartCommand(intent, flags, startId);

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy() executed");
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    public class MyBinder extends Binder {

        public void startDownload() {
            Log.d("TAG", "startDownload() executed");
            // 执行具体的下载任务
            //new DeviceUtils().loadFile(Constants.updateAppUrl);
        }
    }

    public void getTaskData() {
        OkHttpUtils.post()
                .url(Urls.TASK)
                .addParams("start", "0")
                .addParams("limit", "1000")
                .addParams("sort", "task_id")
                .addParams("dir", "desc")
                .addParams("driver_no", Constants.driver.getDriver_no())
                .addParams("run_status", "0")
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
                            taskVo = GjsonUtil.parseJsonWithGson(response, TaskVo.class);
                            if (taskVo != null) {
                                Intent intent = new Intent();
                                intent.setAction(ACTION_UPDATE_TASK);
                                intent.putExtra("taskVo", taskVo);
                                sendBroadcast(intent);
                                getCarInfoData();
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
    }

    public void getCarInfoData() {
        OkHttpUtils.post()
                .url(Urls.NEW_VEHICLE)//vehicle
                .addParams("car_sn", Constants.driver.getDevice_no())
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
                            vehicleVo = GjsonUtil.parseJsonWithGson(response, VehicleVo.class);
                            if (vehicleVo != null) {
                                Intent intent = new Intent();
                                intent.setAction(ACTION_UPDATE_CAR);
                                intent.putExtra("vehicleVo", vehicleVo);
                                sendBroadcast(intent);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
    }

}
