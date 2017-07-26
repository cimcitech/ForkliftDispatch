package com.cimcitech.forkliftdispatch;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.cimcitech.forkliftdispatch.model.TaskVo;
import com.cimcitech.forkliftdispatch.utils.Constants;
import com.cimcitech.forkliftdispatch.utils.GjsonUtil;
import com.cimcitech.forkliftdispatch.utils.ToastUtil;
import com.cimcitech.forkliftdispatch.utils.Urls;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import okhttp3.Call;

public class MainActivity extends FragmentActivity implements View.OnClickListener {

    UpdateUIBroadcastReceiver broadcastReceiver;

    private Fragment mContent, carFragment, taskFragment, settingFragment;
    private FragmentTransaction transaction;
    private View menu_layout_1, menu_layout_4, menu_layout_5;
    private TaskVo taskVo;
    private Button num_icon;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        num_icon = (Button) this.findViewById(R.id.num_icon);
        initialization();
        getTaskData();
        // 动态注册广播
        IntentFilter filter = new IntentFilter();
        filter.addAction(MyService.ACTION_UPDATE_TASK);
        broadcastReceiver = new UpdateUIBroadcastReceiver();
        registerReceiver(broadcastReceiver, filter);
    }

    public void getTaskData() {
        if (Constants.driver != null)
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
                                if (taskVo != null && taskVo.getTotal() > 0)
                                    num_icon.setText(taskVo.getTotal() + "");
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.menu_layout_1:
                menu_layout_1.setSelected(true);
                menu_layout_4.setSelected(false);
                menu_layout_5.setSelected(false);
                switchContent(mContent, carFragment);
                break;
            case R.id.menu_layout_4:
                menu_layout_1.setSelected(false);
                menu_layout_4.setSelected(true);
                menu_layout_5.setSelected(false);
                switchContent(mContent, taskFragment);
                break;
            case R.id.menu_layout_5:
                menu_layout_1.setSelected(false);
                menu_layout_4.setSelected(false);
                menu_layout_5.setSelected(true);
                switchContent(mContent, settingFragment);
                break;
            default:
                break;
        }
    }

    private void initialization() {
        menu_layout_1 = findViewById(R.id.menu_layout_1);
        menu_layout_4 = findViewById(R.id.menu_layout_4);
        menu_layout_5 = findViewById(R.id.menu_layout_5);
        menu_layout_1.setOnClickListener(this);
        menu_layout_4.setOnClickListener(this);
        menu_layout_5.setOnClickListener(this);
        transaction = getSupportFragmentManager().beginTransaction();
        carFragment = new CarActivity();
        taskFragment = new TaskActivity();
        settingFragment = new SettingActivity();
        if (!taskFragment.isAdded()) {
            transaction.add(R.id.viewContainer, carFragment);
        }
        transaction.show(carFragment).commit();
        mContent = carFragment;
        menu_layout_1.setSelected(true);
    }

    public void switchContent(Fragment from, Fragment to) {
        if (mContent != to) {
            mContent = to;
            if (!to.isAdded()) {
                getSupportFragmentManager().beginTransaction().hide(from).add(R.id.viewContainer, to).show(to).commit();
            } else {
                getSupportFragmentManager().beginTransaction().hide(from).show(to).commit();
            }
        }
    }

    /**
     * 定义广播接收器（内部类）
     *
     * @author lenovo
     */
    private class UpdateUIBroadcastReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(MyService.ACTION_UPDATE_TASK)) {
                taskVo = (TaskVo) intent.getExtras().getSerializable("taskVo");
                if (taskVo.getTotal() == 0)
                    num_icon.setVisibility(View.GONE);
                else {
                    num_icon.setVisibility(View.VISIBLE);
                    num_icon.setText(String.valueOf(taskVo.getTotal()));
                }

            }
        }

    }

}