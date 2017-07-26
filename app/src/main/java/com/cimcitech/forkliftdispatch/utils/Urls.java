package com.cimcitech.forkliftdispatch.utils;

/**
 * Created by cimcitech on 2017/6/8.
 */

public class Urls {

    public static final String SERVICE = "http://61.144.248.90:8080/";
    //司机信息
    public static final String DRIVER = SERVICE + "hx_driver/FindAllByPageDataTable_hx_driver_byDevice";
    //车辆信息
    public static final String VEHICLE = SERVICE + "hx_data_new/FindAllByPageDataTable_hx_data_new";
    //任务订单
    public static final String TASK = SERVICE + "hx_task/FindAllByPageDataTable_hx_task_simple";
    //更新任务订单
    public static final String UPDATE_TASK = SERVICE + "hx_task/Update_hx_task_status";
    //更新apk
    public static final String UPDATE_APP = SERVICE + "apkdownload/FindAllByPageDataTable_apkdownload";
    //请求单个车辆信息
    public static final String NEW_VEHICLE = SERVICE + "hx_data_new/FindByNO_vw_hx_data_new";
}
