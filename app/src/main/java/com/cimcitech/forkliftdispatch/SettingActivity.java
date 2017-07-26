package com.cimcitech.forkliftdispatch;

import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.cimcitech.forkliftdispatch.utils.ToastUtil;

import butterknife.ButterKnife;
import butterknife.OnClick;

public class SettingActivity extends Fragment implements View.OnClickListener {

    private View v;
    private boolean isFinish = true;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.activity_setting, container, false);
        this.v.findViewById(R.id.layout_view_1).setOnClickListener(this);
        this.v.findViewById(R.id.layout_view_2).setOnClickListener(this);
        this.v.findViewById(R.id.layout_view_3).setOnClickListener(this);
        this.v.findViewById(R.id.layout_view_4).setOnClickListener(this);
        this.v.findViewById(R.id.layout_view_5).setOnClickListener(this);
        return v;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.layout_view_1:
                startActivity(new Intent(getActivity(), BindCarActivity.class));
                break;
            case R.id.layout_view_2:
                ToastUtil.showToast("功能暂未开放");
                break;
            case R.id.layout_view_3:
                ToastUtil.showToast("功能暂未开放");
//                Intent intent = new Intent(getActivity(), GestureEditActivity.class);
//                intent.putExtra("isFinish", isFinish);
//                startActivity(intent);
                break;
            case R.id.layout_view_4:
                startActivity(new Intent(getActivity(), FrequencyActivity.class));
                break;
            case R.id.layout_view_5:
                startActivity(new Intent(getActivity(), AppVersionActivity.class));
                break;
        }
    }
}
