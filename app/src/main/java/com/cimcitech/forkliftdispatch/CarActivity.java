package com.cimcitech.forkliftdispatch;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.drawable.BitmapDrawable;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.cimcitech.forkliftdispatch.adapter.BatteryCarAdapter;
import com.cimcitech.forkliftdispatch.adapter.InternalCombustionCarAdapter;
import com.cimcitech.forkliftdispatch.model.DriverVo;
import com.cimcitech.forkliftdispatch.model.VehicleVo;
import com.cimcitech.forkliftdispatch.utils.Constants;
import com.cimcitech.forkliftdispatch.utils.GjsonUtil;
import com.cimcitech.forkliftdispatch.utils.ToastUtil;
import com.cimcitech.forkliftdispatch.utils.Urls;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import java.lang.ref.WeakReference;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import butterknife.Bind;
import butterknife.ButterKnife;
import okhttp3.Call;

public class CarActivity extends Fragment implements View.OnClickListener {

    private View v;
    private DriverVo.Rows driverVo;
    private VehicleVo vehicleVo;
    private ProgressDialog dialog;
    private Handler uiHandler = null;
    private final int INIT_VIEW = 1001;
    private TextView text_number_tv, name_tv, device_no_tv, hd_type_name, speed_tv, work_hours_tv, water_temperature_tv, oil_volume_tv;
    private ImageView image_view_1, image_view_2, image_view_3, image_view_4, image_view_5, image_view_6,
            image_view_7, image_view_8, image_view_9, image_view_10, image_view_11;
    private ImageView icv_image_view_1, icv_image_view_2, icv_image_view_3, icv_image_view_4, icv_image_view_5,
            icv_image_view_6, icv_image_view_7, icv_image_view_8, icv_image_view_9, icv_image_view_10;
    private InternalCombustionCarAdapter internalCombustionCarAdapter;
    UpdateUIBroadcastReceiver broadcastReceiver;
    private GridView carGrid;
    private GridView icv_carGrid;
    private PopupWindow pop;
    private BatteryCarAdapter batteryCarAdapter;
    private String alarmStr = "";
    private View battery_car_view, internal_combustion_view;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.activity_car, container, false);
        initHandler();
        initData();
        return v;
    }


    /**
     * 定义广播接收器（内部类）
     *
     * @author lenovo
     */
    private class UpdateUIBroadcastReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(MyService.ACTION_UPDATE_CAR)) {
                vehicleVo = (VehicleVo) intent.getExtras().getSerializable("vehicleVo");
                uiHandler.sendEmptyMessage(INIT_VIEW);//取得数据 更新UI
            }
        }
    }

    private void initData() {
        showSelTypePopWin();
        carGrid = (GridView) this.v.findViewById(R.id.carGrid);
        carGrid.setSelector(R.drawable.hide_listview_yellow_selector);
        icv_carGrid = (GridView) this.v.findViewById(R.id.icv_carGrid);
        icv_carGrid.setSelector(R.drawable.hide_listview_yellow_selector);
        // 动态注册广播
        IntentFilter filter = new IntentFilter();
        filter.addAction(MyService.ACTION_UPDATE_CAR);
        broadcastReceiver = new UpdateUIBroadcastReceiver();
        getActivity().registerReceiver(broadcastReceiver, filter);
        //----------
        device_no_tv = (TextView) this.v.findViewById(R.id.device_no_tv);
        name_tv = (TextView) this.v.findViewById(R.id.name_tv);
        text_number_tv = (TextView) this.v.findViewById(R.id.text_number_tv);
        hd_type_name = (TextView) this.v.findViewById(R.id.hd_type_name);
        image_view_1 = (ImageView) this.v.findViewById(R.id.image_view_1);
        image_view_2 = (ImageView) this.v.findViewById(R.id.image_view_2);
        image_view_3 = (ImageView) this.v.findViewById(R.id.image_view_3);
        image_view_4 = (ImageView) this.v.findViewById(R.id.image_view_4);
        image_view_5 = (ImageView) this.v.findViewById(R.id.image_view_5);
        image_view_6 = (ImageView) this.v.findViewById(R.id.image_view_6);
        image_view_7 = (ImageView) this.v.findViewById(R.id.image_view_7);
        image_view_8 = (ImageView) this.v.findViewById(R.id.image_view_8);
        image_view_9 = (ImageView) this.v.findViewById(R.id.image_view_9);
        image_view_10 = (ImageView) this.v.findViewById(R.id.image_view_10);
        image_view_11 = (ImageView) this.v.findViewById(R.id.image_view_11);

        icv_image_view_1 = (ImageView) this.v.findViewById(R.id.icv_image_view_1);
        icv_image_view_2 = (ImageView) this.v.findViewById(R.id.icv_image_view_2);
        icv_image_view_3 = (ImageView) this.v.findViewById(R.id.icv_image_view_3);
        icv_image_view_4 = (ImageView) this.v.findViewById(R.id.icv_image_view_4);
        icv_image_view_5 = (ImageView) this.v.findViewById(R.id.icv_image_view_5);
        icv_image_view_6 = (ImageView) this.v.findViewById(R.id.icv_image_view_6);
        icv_image_view_7 = (ImageView) this.v.findViewById(R.id.icv_image_view_7);
        icv_image_view_8 = (ImageView) this.v.findViewById(R.id.icv_image_view_8);
        icv_image_view_9 = (ImageView) this.v.findViewById(R.id.icv_image_view_9);
        icv_image_view_10 = (ImageView) this.v.findViewById(R.id.icv_image_view_10);

        image_view_1.setOnClickListener(this);
        image_view_2.setOnClickListener(this);
        image_view_3.setOnClickListener(this);
        image_view_4.setOnClickListener(this);
        image_view_5.setOnClickListener(this);
        image_view_6.setOnClickListener(this);
        image_view_7.setOnClickListener(this);
        image_view_8.setOnClickListener(this);
        image_view_9.setOnClickListener(this);
        image_view_10.setOnClickListener(this);
        image_view_11.setOnClickListener(this);

        icv_image_view_1.setOnClickListener(this);
        icv_image_view_2.setOnClickListener(this);
        icv_image_view_3.setOnClickListener(this);
        icv_image_view_4.setOnClickListener(this);
        icv_image_view_5.setOnClickListener(this);
        icv_image_view_6.setOnClickListener(this);
        icv_image_view_7.setOnClickListener(this);
        icv_image_view_8.setOnClickListener(this);
        icv_image_view_9.setOnClickListener(this);
        icv_image_view_10.setOnClickListener(this);

        battery_car_view = this.v.findViewById(R.id.battery_car_view);
        internal_combustion_view = this.v.findViewById(R.id.internal_combustion_view);

        work_hours_tv = (TextView) this.v.findViewById(R.id.work_hours_tv);
        water_temperature_tv = (TextView) this.v.findViewById(R.id.water_temperature_tv);
        oil_volume_tv = (TextView) this.v.findViewById(R.id.oil_volume_tv);

        speed_tv = (TextView) this.v.findViewById(R.id.speed_tv);
        driverVo = Constants.driver;
        if (driverVo != null) {
            name_tv.setText(driverVo.getDriver_name());
            text_number_tv.setText(driverVo.getDriver_no());
            device_no_tv.setText(driverVo.getDevice_no());
            showLoading();
            getCarInfoData();
        }
    }

    public void getCarInfoData() {
        OkHttpUtils.post()
                .url(Urls.NEW_VEHICLE)//vehicle
                .addParams("car_sn", driverVo.getDevice_no())
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
                            if (vehicleVo != null)
                                uiHandler.sendEmptyMessage(INIT_VIEW);
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
                        Constants.vehicleVo = vehicleVo;
                        hd_type_name.setText(vehicleVo.getHd_type_name());
                        if (vehicleVo.getV_type().equals("2")) { //电车
                            setAlarmImageView(vehicleVo);
                            Constants.maintenanceTime = 250;
                            batteryCarAdapter = new BatteryCarAdapter(getActivity(), vehicleVo);
                            carGrid.setAdapter(batteryCarAdapter);
                            battery_car_view.setVisibility(View.VISIBLE);
                            internal_combustion_view.setVisibility(View.GONE);
                        } else {  //其他车，如内燃车
                            setIcvAlarmImageView(vehicleVo);
                            Constants.maintenanceTime = 300;
                            internalCombustionCarAdapter = new InternalCombustionCarAdapter(getActivity(), vehicleVo);
                            icv_carGrid.setAdapter(internalCombustionCarAdapter);
                            battery_car_view.setVisibility(View.GONE);
                            internal_combustion_view.setVisibility(View.VISIBLE);
                        }
                        //速度
                        speed_tv.setText(!vehicleVo.getCan_speed().equals("") ? pudateFieldDecimal(vehicleVo.getCan_speed()) : "0.0");
                        name_tv.setText(Constants.driver.getDriver_name());
                        text_number_tv.setText(Constants.driver.getDriver_no());
                        device_no_tv.setText(Constants.driver.getDevice_no());

                        work_hours_tv.setText(!vehicleVo.getWork_hours().equals("") ? pudateFieldDecimal(vehicleVo.getWork_hours()) : "0.0"); // 工作小时数
                        water_temperature_tv.setText(!vehicleVo.getWater_temperature().equals("") ? pudateFieldDecimal(vehicleVo.getWater_temperature()) : "0.0");  //水温
                        oil_volume_tv.setText(!vehicleVo.getFuel_num().equals("") ? pudateFieldDecimal(vehicleVo.getFuel_num()) : "0.0");  // 油量
                        break;
                }
            }
        };
    }

    //String 转 Double 保留一位小数点
    public String pudateFieldDecimal(String value) {
        if (!value.equals("")) {
            java.text.DecimalFormat df = new java.text.DecimalFormat("#.0");
            if (Double.parseDouble(value) > 0)
                return df.format(Double.parseDouble(value));
            else
                return "0.0";
        }
        return "0.0";
    }

    //11个报警图片   电车指示灯
    private void setAlarmImageView(VehicleVo item) {
        if (item.getSafety_belt().equals("1")) { //安全带
            image_view_1.setImageResource(R.mipmap.image_view_1_selected);
        } else
            image_view_1.setImageResource(R.mipmap.image_view_1);

        if (item.getFuel_press_alarm().equals("1")) {//机油压力
            image_view_2.setImageResource(R.mipmap.image_view_2_selected);
        } else
            image_view_2.setImageResource(R.mipmap.image_view_2);

        if (item.getRegion_alarm().equals("1")) {//电子围栏
            image_view_3.setImageResource(R.mipmap.image_view_3_selected);
        } else
            image_view_3.setImageResource(R.mipmap.image_view_3);

        if (item.getBrake_pressure_alarm().equals("1")) {//制动压力
            image_view_4.setImageResource(R.mipmap.image_view_4_selected);
        } else
            image_view_4.setImageResource(R.mipmap.image_view_4);

        if (item.getWater_temperature() != null && !item.getWater_temperature().equals(""))
            if (Double.parseDouble(item.getWater_temperature()) > 110) {//水温
                image_view_5.setImageResource(R.mipmap.image_view_5_selected);
            } else
                image_view_5.setImageResource(R.mipmap.image_view_5);
        else
            image_view_5.setImageResource(R.mipmap.image_view_5);

        if (item.getCan_speed() != null && !item.getCan_speed().equals(""))
            if (Double.parseDouble(item.getCan_speed()) > 18) {//超速
                image_view_6.setImageResource(R.mipmap.image_view_6_selected);
            } else
                image_view_6.setImageResource(R.mipmap.image_view_6);
        else
            image_view_6.setImageResource(R.mipmap.image_view_6);

        image_view_7.setImageResource(R.mipmap.image_view_7);//超载
        image_view_8.setImageResource(R.mipmap.image_view_8);//离位超时

        if (item.getOil_water_sep_alarm().equals("1")) {//油水分离
            image_view_9.setImageResource(R.mipmap.image_view_9_selected);
        } else
            image_view_9.setImageResource(R.mipmap.image_view_9);

        if (item.getFuel_temperature() != null && !item.getFuel_temperature().equals(""))
            if (Double.parseDouble(item.getOil_water_sep_alarm()) > 100) {//油温
                image_view_10.setImageResource(R.mipmap.image_view_10_selected);
            } else
                image_view_10.setImageResource(R.mipmap.image_view_10);
        else
            image_view_10.setImageResource(R.mipmap.image_view_10);

        image_view_11.setImageResource(R.mipmap.image_view_11);//碰撞
    }

    // 内燃车指示灯
    private void setIcvAlarmImageView(VehicleVo item) {
        if (item.getCharge_status().equals("1"))//充电指示灯
            icv_image_view_1.setImageResource(R.mipmap.icv_image_view_1_selected);
        else
            icv_image_view_1.setImageResource(R.mipmap.icv_image_view_1);

        if (item.getOil_water_sep_alarm().equals("1")) {//油水分离
            icv_image_view_2.setImageResource(R.mipmap.icv_image_view_2_selected);
        } else
            icv_image_view_2.setImageResource(R.mipmap.icv_image_view_2);

        if (item.getAir_filter().equals(""))//空滤
            icv_image_view_3.setImageResource(R.mipmap.icv_image_view_3_selected);
        else
            icv_image_view_3.setImageResource(R.mipmap.icv_image_view_3);

        if (item.getEngine_error().equals("1")) //发动机故障
            icv_image_view_4.setImageResource(R.mipmap.icv_image_view_4_selected);
        else
            icv_image_view_4.setImageResource(R.mipmap.icv_image_view_4);

        if (item.getSerious_fault().equals("1")) //故障状态
            icv_image_view_5.setImageResource(R.mipmap.icv_image_view_5_selected);
        else
            icv_image_view_5.setImageResource(R.mipmap.icv_image_view_5);

        if (item.getFuel_press_alarm().equals("1")) {//机油压力
            icv_image_view_6.setImageResource(R.mipmap.icv_image_view_6_selected);
        } else
            icv_image_view_6.setImageResource(R.mipmap.icv_image_view_6);

        if (item.getFuel_temperature() != null && !item.getFuel_temperature().equals(""))
            if (Double.parseDouble(item.getOil_water_sep_alarm()) > 100) {//油温
                icv_image_view_7.setImageResource(R.mipmap.icv_image_view_7_selected);
            } else
                icv_image_view_7.setImageResource(R.mipmap.icv_image_view_7);
        else
            icv_image_view_7.setImageResource(R.mipmap.icv_image_view_7);

        if (item.getBrake_pressure_alarm().equals("1")) {//制动压力
            icv_image_view_8.setImageResource(R.mipmap.icv_image_view_8_selected);
        } else
            icv_image_view_8.setImageResource(R.mipmap.icv_image_view_8);

        if (item.getSerious_fault().equals("1")) //故障状态
            icv_image_view_9.setImageResource(R.mipmap.icv_image_view_9_selected);
        else
            icv_image_view_9.setImageResource(R.mipmap.icv_image_view_9);

        if (item.getPreheat_status().equals("1"))
            icv_image_view_10.setImageResource(R.mipmap.icv_image_view_10_selected);
        else
            icv_image_view_10.setImageResource(R.mipmap.icv_image_view_10);
    }


    public void showLoading() {
        if (dialog != null && dialog.isShowing()) return;
        dialog = new ProgressDialog(getActivity());
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

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.image_view_1:

                break;
            case R.id.image_view_2:
                alarmStr = "机油压力";
                showSelTypePopWin();
                pop.showAsDropDown(v);
                break;
            case R.id.image_view_3:
                alarmStr = "电子围栏";
                showSelTypePopWin();
                pop.showAsDropDown(v);
                break;
            case R.id.image_view_4:
                alarmStr = "制动压力";
                showSelTypePopWin();
                pop.showAsDropDown(v);
                break;
            case R.id.image_view_5:
                alarmStr = "水温";
                showSelTypePopWin();
                pop.showAsDropDown(v);
                break;
            case R.id.image_view_6:
                alarmStr = "超速";
                showSelTypePopWin();
                pop.showAsDropDown(v);
                break;
            case R.id.image_view_7:
                alarmStr = "超载";
                showSelTypePopWin();
                pop.showAsDropDown(v);
                break;
            case R.id.image_view_8:
                alarmStr = "离位超时";
                showSelTypePopWin();
                pop.showAsDropDown(v);
                break;
            case R.id.image_view_9:
                alarmStr = "油水分离";
                showSelTypePopWin();
                pop.showAsDropDown(v);
                break;
            case R.id.image_view_10:
                alarmStr = "油温";
                showSelTypePopWin();
                pop.showAsDropDown(v);
                break;
            case R.id.image_view_11:
                alarmStr = "碰撞";
                showSelTypePopWin();
                pop.showAsDropDown(v);
                break;
            case R.id.icv_image_view_1:
                alarmStr = "充电指示";
                showSelTypePopWin();
                pop.showAsDropDown(v);
                break;
            case R.id.icv_image_view_2:
                alarmStr = "油水分离";
                showSelTypePopWin();
                pop.showAsDropDown(v);
                break;
            case R.id.icv_image_view_3:
                alarmStr = "空滤";
                showSelTypePopWin();
                pop.showAsDropDown(v);
                break;
            case R.id.icv_image_view_4:
                alarmStr = "发动机故障";
                showSelTypePopWin();
                pop.showAsDropDown(v);
                break;
            case R.id.icv_image_view_5:
                alarmStr = "故障状态";
                showSelTypePopWin();
                pop.showAsDropDown(v);
                break;
            case R.id.icv_image_view_6:
                alarmStr = "机油压力";
                showSelTypePopWin();
                pop.showAsDropDown(v);
                break;
            case R.id.icv_image_view_7:
                alarmStr = "变速箱油温";
                showSelTypePopWin();
                pop.showAsDropDown(v);
                break;
            case R.id.icv_image_view_8:
                alarmStr = "制动压力";
                showSelTypePopWin();
                pop.showAsDropDown(v);
                break;
            case R.id.icv_image_view_9:
                alarmStr = "严重故障报警";
                showSelTypePopWin();
                pop.showAsDropDown(v);
                break;
            case R.id.icv_image_view_10:
                alarmStr = "预热";
                showSelTypePopWin();
                pop.showAsDropDown(v);
                break;
        }
    }

    private void showSelTypePopWin() {
        LayoutInflater inflater = LayoutInflater.from(getContext());
        // 引入窗口配置文件
        View view = inflater.inflate(R.layout.alarm_item_text, null);
        //view.getBackground().setAlpha(100);
        // 创建PopupWindow对象
        pop = new PopupWindow(view, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, false);
        //View pop_reward_view = view.findViewById(R.id.pop_reward_view);
        TextView listView = (TextView) view.findViewById(R.id.alarm_item_text);
        // 需要设置一下此参数，点击外边可消失
        pop.setBackgroundDrawable(new BitmapDrawable());
        // 设置点击窗口外边窗口消失
        pop.setOutsideTouchable(true);
        // 设置此参数获得焦点，否则无法点击
        pop.setFocusable(true);
        listView.setText(alarmStr);
    }
}
