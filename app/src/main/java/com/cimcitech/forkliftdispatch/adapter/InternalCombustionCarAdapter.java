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

//内燃车
public class InternalCombustionCarAdapter extends BaseAdapter {

    private LayoutInflater inflater;

    private String[] name = {
            "水温",
            "油量",
            "累计工作时长",
            "下次维保时间",
    };

    private String[] unit = {
            "（℃）",
            "（%）",
            "（h）",
            "（h）",
    };

    public String[] vaule = new String[4];

    public InternalCombustionCarAdapter(Context context, VehicleVo item) {
        inflater = LayoutInflater.from(context);
        java.text.DecimalFormat df = new java.text.DecimalFormat("#.0");
        if (item != null) {
            vaule[0] = !item.getWater_temperature().equals("") ? pudateFieldDecimal(item.getWater_temperature()) : "0.0";//水温
            vaule[1] = !item.getFuel_num().equals("") ? pudateFieldDecimal(item.getFuel_num()) : "0.0";// 油量
            vaule[2] = !item.getWork_hours().equals("") ? pudateFieldDecimal(item.getWork_hours()) : "0.0";
            double d = (Math.ceil(Double.parseDouble(item.getWork_hours()) / Constants.maintenanceTime) * Constants.maintenanceTime - (Double.parseDouble(item.getWork_hours())));
            vaule[3] = !item.getWork_hours().equals("") && Double.parseDouble(item.getWork_hours()) > 0 ? df.format(d) : "0.0";
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

