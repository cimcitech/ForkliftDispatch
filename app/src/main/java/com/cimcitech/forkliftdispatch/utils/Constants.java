package com.cimcitech.forkliftdispatch.utils;

import android.content.Context;

import com.cimcitech.forkliftdispatch.model.DriverVo;
import com.cimcitech.forkliftdispatch.model.VehicleVo;

/**
 * Created by cimcitech on 2017/3/20.
 */

public class Constants {
    public static String KEY_FUND_AUTO = "key_fund_auto"; //手势密码
    public static String KEY_DRIVER_AUTO = "key_driver_auto"; //登录用户名
    public static String userName = "";
    public static final int POINT_STATE_NORMAL = 0; // 手势密码点的状态 正常状态
    public static final int POINT_STATE_SELECTED = 1; // 手势密码点的状态 按下状态
    public static final int POINT_STATE_WRONG = 2; // 手势密码点的状态 错误状态
    public static Context CONTEXT;
    public static DriverVo.Rows driver; //司机信息
    public static String updateAppUrl = "";
    public static VehicleVo vehicleVo; //车辆信息
    public static int maintenanceTime = 250; //维保计算公式，电动250小时维保一次，内燃300小时维保一次。根据车型计算下次维保时间
}
