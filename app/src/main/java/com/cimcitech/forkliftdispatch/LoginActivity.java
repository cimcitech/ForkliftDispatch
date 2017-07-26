package com.cimcitech.forkliftdispatch;


import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.cimcitech.forkliftdispatch.model.DriverVo;
import com.cimcitech.forkliftdispatch.scheduling.Login2Activity;
import com.cimcitech.forkliftdispatch.utils.Constants;
import com.cimcitech.forkliftdispatch.utils.GjsonUtil;
import com.cimcitech.forkliftdispatch.utils.ToastUtil;
import com.cimcitech.forkliftdispatch.utils.Urls;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.Call;

public class LoginActivity extends AppCompatActivity {

    @Bind(R.id.name_et)
    EditText name;
    @Bind(R.id.scheduling_login)
    Button schedulingLogin;

    private ProgressDialog dialog;
    private Handler uiHandler = null;
    private final int INIT_VIEW = 1001;
    private DriverVo vehicleVo;
    private SharedPreferences driver;
    private PopupWindow pop;
    private DriverAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);
        initHandler();
        driver = this.getSharedPreferences(Constants.KEY_DRIVER_AUTO, MODE_PRIVATE);
        getUserInfo();
    }

    @OnClick(R.id.scheduling_login)
    public void schedulingLogin() {
        startActivity(new Intent(LoginActivity.this, Login2Activity.class));
        finish();
    }

    public boolean checkInput() {
        if (name.getText().toString().trim().equals("")) {
            ToastUtil.showToast("请输入您的工号");
            return false;
        }
        return true;
    }

    private void getUserInfo() {
        if (driver.getString("user_name", "") != "") {
            String user_name = driver.getString("user_name", "");
            name.setText(user_name);
        }
    }

    @OnClick(R.id.login_btn)
    public void httpLogin(final View v) {
        if (!checkInput())
            return;
        showLoading();
        OkHttpUtils.post()
                .url(Urls.DRIVER)//vehicle
                .addParams("start", "0")
                .addParams("limit", "100")
                .addParams("sort", "device_id")
                .addParams("dir", "desc")
                .addParams("driver_no", name.getText().toString().trim())
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        Log.e("LoginActivity", "error=" + e);
                        ToastUtil.showToast("请求失败，请检查网络是否可用");
                        dismissLoading();
                    }

                    @Override
                    public void onResponse(String response, int id) {
                        Log.e("LoginActivity", "token.response=" + response);
                        try {
                            vehicleVo = GjsonUtil.parseJsonWithGson(response, DriverVo.class);
                            if (vehicleVo != null && vehicleVo.getTotal() > 0) {
                                Constants.userName = name.getText().toString().trim();
                                if (vehicleVo.getTotal() == 1) {
                                    uiHandler.sendEmptyMessage(INIT_VIEW);
                                } else {
                                    adapter = new DriverAdapter(LoginActivity.this, vehicleVo.getRows());
                                    showSelTypePopWin();
                                    pop.showAtLocation(v, Gravity.CENTER, 0, 0);
                                }
                            } else
                                ToastUtil.showToast("获取司机信息失败，请检查司机工号是否输入正确");
                            dismissLoading();
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
                        Constants.driver = vehicleVo.getRows().get(0);
                        SharedPreferences.Editor editor = driver.edit();
                        editor.putString("user_name", name.getText().toString().trim());
                        editor.commit();
                        startActivity(new Intent(LoginActivity.this, MainActivity.class));
                        finish();
                        break;
                }
            }
        };
    }

    public void showLoading() {
        if (dialog != null && dialog.isShowing()) return;
        dialog = new ProgressDialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCanceledOnTouchOutside(false);
        dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        dialog.setMessage("请求网络中...");
        dialog.show();
    }

    public void dismissLoading() {
        if (dialog != null && dialog.isShowing()) {
            dialog.dismiss();
        }
    }

    private void showSelTypePopWin() {
        LayoutInflater inflater = LayoutInflater.from(this);
        // 引入窗口配置文件
        View view = inflater.inflate(R.layout.login_list_view, null);
        view.getBackground().setAlpha(100);
        // 创建PopupWindow对象
        pop = new PopupWindow(view, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT, false);
        //View pop_reward_view = view.findViewById(R.id.pop_reward_view);
        ListView listView = (ListView) view.findViewById(R.id.listContent);
        listView.setAdapter(adapter);
        // 需要设置一下此参数，点击外边可消失
        pop.setBackgroundDrawable(new BitmapDrawable());
        // 设置点击窗口外边窗口消失
        pop.setOutsideTouchable(true);
        // 设置此参数获得焦点，否则无法点击
        pop.setFocusable(true);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                DriverVo.Rows item = adapter.getRowsItem().get(position);
                Constants.driver = item;
                SharedPreferences.Editor editor = driver.edit();
                editor.putString("user_name", name.getText().toString().trim());
                editor.commit();
                startActivity(new Intent(LoginActivity.this, MainActivity.class));
                pop.dismiss();
            }
        });
    }

    public class DriverAdapter extends BaseAdapter {

        private ArrayList<DriverVo.Rows> rows;
        private LayoutInflater inflater;

        public DriverAdapter(Context context, ArrayList<DriverVo.Rows> rows) {
            this.rows = rows;
            this.inflater = LayoutInflater.from(context);
        }

        public ArrayList<DriverVo.Rows> getRowsItem() {
            return rows;
        }

        @Override
        public int getCount() {
            return rows.size();
        }

        @Override
        public Object getItem(int position) {
            return position;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            HolderView holderView;
            DriverVo.Rows item = rows.get(position);
            if (convertView == null) {
                holderView = new HolderView();
                convertView = inflater.inflate(R.layout.alert_list_view, null);
                holderView.alert_item_tv = (TextView) convertView.findViewById(R.id.alert_item_tv);
                convertView.setTag(holderView);
            } else {
                holderView = (HolderView) convertView.getTag();
            }
            holderView.alert_item_tv.setText("叉车编号：" + item.getDevice_no());
            return convertView;
        }

        public class HolderView {
            TextView alert_item_tv;
        }
    }

}
