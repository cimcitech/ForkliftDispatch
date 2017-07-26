package com.cimcitech.forkliftdispatch;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.drawable.BitmapDrawable;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.cimcitech.forkliftdispatch.model.DriverVo;
import com.cimcitech.forkliftdispatch.model.VehicleVo;
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

public class BindCarActivity extends AppCompatActivity {

    private SharedPreferences spf;

    @Bind(R.id.device_number_tv)
    TextView device_number_tv;
    @Bind(R.id.department_name_tv)
    TextView department_name_tv;
    @Bind(R.id.hard_id_tv)
    TextView hard_id_tv;
    @Bind(R.id.hd_type_name_tv)
    TextView hd_type_name_tv;
    @Bind(R.id.hd_type_no_tv)
    TextView hd_type_no_tv;

    private DriverVo driverVo;
    private PopupWindow pop;
    private Handler uiHandler = null;
    private final int INIT_VIEW = 1001;
    private final int INIT_CAR_DATA = 1002;
    private DriverAdapter adapter;
    private VehicleVo vehicleVo;
    private ProgressDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bind_car);
        ButterKnife.bind(this);
        initHandler();
        this.findViewById(R.id.back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        initViewData();
    }

    public void initViewData() {
        if (Constants.vehicleVo != null) {
            //device_number_tv.setText(Constants.driver.getDriver_name());
            Constants.vehicleVo.getDepartment_name();//公司名称
            Constants.vehicleVo.getHard_id();//控制器编号
            Constants.vehicleVo.getHd_type_name();//叉车类型
            Constants.vehicleVo.getHd_type_no();//叉车类型编号
            Constants.vehicleVo.getCar_sn();//叉车编号
            department_name_tv.setText(Constants.vehicleVo.getDepartment_name());
            hard_id_tv.setText(Constants.vehicleVo.getHard_id());
            hd_type_name_tv.setText(Constants.vehicleVo.getHd_type_name());
            hd_type_no_tv.setText(Constants.vehicleVo.getHd_type_no());
            device_number_tv.setText(Constants.vehicleVo.getCar_sn());
            dismissLoading();
        }
    }

    @OnClick(R.id.check_bind)
    public void checkBind(View v) {
        postBindcar(v);
    }

    public void postBindcar(final View v) {
        OkHttpUtils.post()
                .url(Urls.DRIVER)//vehicle
                .addParams("start", "0")
                .addParams("limit", "100")
                .addParams("sort", "device_id")
                .addParams("dir", "desc")
                .addParams("driver_no", Constants.userName)
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
                            driverVo = GjsonUtil.parseJsonWithGson(response, DriverVo.class);
                            if (driverVo != null && driverVo.getTotal() > 0) {
                                if (driverVo.getTotal() == 1) {
                                    uiHandler.sendEmptyMessage(INIT_VIEW);
                                } else {
                                    adapter = new DriverAdapter(BindCarActivity.this, driverVo.getRows());
                                    showSelTypePopWin();
                                    pop.showAtLocation(v, Gravity.CENTER, 0, 0);
                                }
                            } else
                                ToastUtil.showToast("获取司机信息失败，请检查司机工号是否输入正确");
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
                        ToastUtil.showToast("该司机只绑定一辆车辆，无其他车辆选择");
                        break;
                    case INIT_CAR_DATA:
                        initViewData();
                        break;
                }
            }
        };
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
                showLoading();
                DriverVo.Rows item = adapter.getRowsItem().get(position);
                Constants.driver = item; //重新缓存司机信息
                //SharedPreferences.Editor editor = driver.edit();
                //editor.putString("user_name", name.getText().toString().trim());
                //editor.commit();
                getCarInfoData(item.getDevice_no());
                pop.dismiss();
            }
        });
    }

    public void getCarInfoData(String device_no) {
        OkHttpUtils.post()
                .url(Urls.NEW_VEHICLE)//vehicle
                .addParams("car_sn", device_no)
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
                                Constants.vehicleVo = vehicleVo;
                                uiHandler.sendEmptyMessage(INIT_CAR_DATA);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
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
}
