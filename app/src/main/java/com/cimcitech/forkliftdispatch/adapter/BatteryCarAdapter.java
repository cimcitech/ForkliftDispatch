package com.cimcitech.forkliftdispatch.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.cimcitech.forkliftdispatch.R;
import com.cimcitech.forkliftdispatch.model.VehicleVo;
import com.cimcitech.forkliftdispatch.utils.Constants;

/**
 * Created by cimcitech on 2017/6/15.
 */

//电瓶车
public class BatteryCarAdapter extends BaseAdapter {

    private LayoutInflater inflater;

    private String[] name = {
            "牵引电机温度",
            "油泵电机温度",
            "牵引电控温度",
            "油泵电控温度",
            "累计工作时长",
            "下次维保时间",
            "剩余电量",
            "泵控时间",
            "牵引时间",
    };

    private String[] unit = {
            "（℃）",
            "（℃）",
            "（℃）",
            "（℃）",
            "（h）",
            "（h）",
            "（%）",
            "（h）",
            "（h）",
    };

    public String[] vaule = new String[9];

    public BatteryCarAdapter(Context context, VehicleVo item) {
        inflater = LayoutInflater.from(context);
        java.text.DecimalFormat df = new java.text.DecimalFormat("#.0");
        if (item != null) {
            vaule[0] = !item.getPull_motor_temperature().equals("") ? pudateFieldDecimal(item.getPull_motor_temperature()) : "0.0";
            vaule[1] = !item.getOil_pump_motor_tempera().equals("") ? pudateFieldDecimal(item.getOil_pump_motor_tempera()) : "0.0";
            vaule[2] = !item.getPull_control_temperature().equals("") ? pudateFieldDecimal(item.getPull_control_temperature()) : "0.0";
            vaule[3] = !item.getPump_control_temperature().equals("") ? pudateFieldDecimal(item.getPump_control_temperature()) : "0.0";
            vaule[4] = !item.getWork_hours().equals("") ? pudateFieldDecimal(item.getWork_hours()) : "0.0";
            double d = (Math.ceil(Double.parseDouble(item.getWork_hours()) / Constants.maintenanceTime) * Constants.maintenanceTime - (Double.parseDouble(item.getWork_hours())));
            vaule[5] = !item.getWork_hours().equals("") ? df.format(d) : "0.0";
            vaule[6] = !item.getDump_energy().equals("") ? pudateFieldDecimal(item.getDump_energy()) : "0.0";
            vaule[7] = !item.getPump_time().equals("") ? pudateFieldDecimal(item.getPump_time()) : "0.0";
            vaule[8] = !item.getPull_time().equals("") ? pudateFieldDecimal(item.getPull_time()) : "0.0";
        }
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

    @Override
    public int getCount() {
        return name.length;
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
        ImgTextWrapper wrapper;
        if (convertView == null) {
            wrapper = new ImgTextWrapper();
            convertView = inflater.inflate(R.layout.car_adapter_item, null);
            wrapper.value_tv = (TextView) convertView.findViewById(R.id.value_tv);
            wrapper.name_tv = (TextView) convertView.findViewById(R.id.name_tv);
            wrapper.unit_tv = (TextView) convertView.findViewById(R.id.unit_tv);
            convertView.setTag(wrapper);
            convertView.setPadding(5, 10, 5, 10);
        } else {
            wrapper = (ImgTextWrapper) convertView.getTag();
        }
        wrapper.name_tv.setText(name[position]);
        wrapper.unit_tv.setText(unit[position]);
        wrapper.value_tv.setText(vaule[position]);
        return convertView;
    }

    class ImgTextWrapper {
        TextView value_tv, name_tv, unit_tv;

    }
}

