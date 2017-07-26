package com.cimcitech.forkliftdispatch;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.cimcitech.forkliftdispatch.model.ResultVo;
import com.cimcitech.forkliftdispatch.model.TaskVo;
import com.cimcitech.forkliftdispatch.model.VehicleVo;
import com.cimcitech.forkliftdispatch.utils.Constants;
import com.cimcitech.forkliftdispatch.utils.GjsonUtil;
import com.cimcitech.forkliftdispatch.utils.ToastUtil;
import com.cimcitech.forkliftdispatch.utils.Urls;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import java.util.ArrayList;

import okhttp3.Call;

public class TaskActivity extends Fragment implements View.OnClickListener {

    private View v;
    private ListView listContent;
    private TaskAdapter adapter;
    private TaskVo taskVo;
    private Handler uiHandler = null;
    private final int INIT_VIEW = 1001;
    private final int UPDATE_TASK = 1002;
    private ImageView imageView1, imageView2, imageView3;
    private int taskStatus = 0;
    private ResultVo resultVo;
    UpdateUIBroadcastReceiver broadcastReceiver;
    private int taskCount = 0; //记录任务的条数
    private String message = "";

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.activity_task, container, false);
        initHandler();
        getXmlView();
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
            if (intent.getAction().equals(MyService.ACTION_UPDATE_TASK)) {
                taskVo = (TaskVo) intent.getExtras().getSerializable("taskVo");
                if (taskCount != taskVo.getTotal()) {
                    if (taskStatus == 0) {
                        adapter = null;
                        adapter = new TaskAdapter(getActivity(), taskVo.getRows());
                        uiHandler.sendEmptyMessage(INIT_VIEW);
                        taskCount = taskVo.getTotal();
                    }
                }
            }
        }

    }

    private void getXmlView() {

        // 动态注册广播
        IntentFilter filter = new IntentFilter();
        filter.addAction(MyService.ACTION_UPDATE_TASK);
        broadcastReceiver = new UpdateUIBroadcastReceiver();
        getActivity().registerReceiver(broadcastReceiver, filter);

        imageView1 = (ImageView) this.v.findViewById(R.id.imageView1);
        imageView2 = (ImageView) this.v.findViewById(R.id.imageView2);
        imageView3 = (ImageView) this.v.findViewById(R.id.imageView3);
        this.v.findViewById(R.id.textView1).setOnClickListener(this);
        this.v.findViewById(R.id.textView2).setOnClickListener(this);
        this.v.findViewById(R.id.textView3).setOnClickListener(this);
        if (Constants.driver != null)
            getTaskData();
        listContent = (ListView) this.v.findViewById(R.id.listContent);
    }

    public void getTaskData() {
        OkHttpUtils.post()
                .url(Urls.TASK)//vehicle
                .addParams("start", "0")
                .addParams("limit", "1000")
                .addParams("sort", "task_id")
                .addParams("dir", "desc")
                .addParams("driver_no", Constants.driver.getDriver_no())
                .addParams("run_status", taskStatus + "")
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
                            if (taskVo != null && taskVo.getTotal() >= 0) {

                                taskCount = taskVo.getTotal();
                                if (adapter == null)
                                    adapter = new TaskAdapter(getActivity(), taskVo.getRows());
                                uiHandler.sendEmptyMessage(INIT_VIEW);
                            }

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
                        listContent.setAdapter(adapter);
                        break;
                    case UPDATE_TASK: //监听修改任务请求 更新页面
                        adapter = null;
                        if (Constants.driver != null)
                            getTaskData();
                        break;
                }
            }
        };
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.textView1:
                adapter = null;
                setImageViewBackground(imageView1);
                taskStatus = 0;
                if (Constants.driver != null)
                    getTaskData();
                break;
            case R.id.textView2:
                adapter = null;
                setImageViewBackground(imageView2);
                taskStatus = 1;
                if (Constants.driver != null)
                    getTaskData();
                break;
            case R.id.textView3:
                adapter = null;
                taskStatus = 2;
                setImageViewBackground(imageView3);
                if (Constants.driver != null)
                    getTaskData();
                break;
        }
    }

    public void setImageViewBackground(ImageView iv) {
        imageView1.setVisibility(View.INVISIBLE);
        imageView2.setVisibility(View.INVISIBLE);
        imageView3.setVisibility(View.INVISIBLE);
        iv.setVisibility(View.VISIBLE);
    }

    public class TaskAdapter extends BaseAdapter {

        private ArrayList<TaskVo.Rows> rows;
        private LayoutInflater inflater;
        private Drawable rightDrawable;
        private int statusNum = 100; //修改任务单状态用到

        public TaskAdapter(Context context, ArrayList<TaskVo.Rows> rows) {
            this.rows = rows;
            this.inflater = LayoutInflater.from(context);
        }

        public ArrayList<TaskVo.Rows> getRowsItem() {
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
            TaskVo.Rows item = rows.get(position);
            if (convertView == null) {
                holderView = new HolderView();
                convertView = inflater.inflate(R.layout.task_item_view, null);
                holderView.task_id_tv = (TextView) convertView.findViewById(R.id.task_id_tv);
                holderView.start_point_tv = (TextView) convertView.findViewById(R.id.start_point_tv);
                holderView.end_point_tv = (TextView) convertView.findViewById(R.id.end_point_tv);
                holderView.task_detail_tv = (TextView) convertView.findViewById(R.id.task_detail_tv);
                holderView.da_task_tv = (TextView) convertView.findViewById(R.id.da_task_tv);
                holderView.check_tv = (TextView) convertView.findViewById(R.id.check_tv);
                convertView.setTag(holderView);
            } else {
                holderView = (HolderView) convertView.getTag();
            }
            holderView.task_id_tv.setText("TX" + item.getTask_id());
            holderView.start_point_tv.setText(item.getStart_point());
            holderView.end_point_tv.setText(item.getEnd_point());
            holderView.task_detail_tv.setText(item.getTask_detail());
            holderView.da_task_tv.setText(item.getDa_task());
            holderView.check_tv.setOnClickListener(new checkTaskOnclickListener(position));

            if (item.getRun_status().equals("0")) {
                rightDrawable = inflater.getContext().getResources().getDrawable(R.mipmap.task_renwu_1);
                rightDrawable.setBounds(0, 0, rightDrawable.getMinimumWidth(), rightDrawable.getMinimumHeight());
                holderView.check_tv.setCompoundDrawables(rightDrawable, null, null, null);
            } else if (item.getRun_status().equals("1")) {
                rightDrawable = inflater.getContext().getResources().getDrawable(R.mipmap.task_renwu_2);
                rightDrawable.setBounds(0, 0, rightDrawable.getMinimumWidth(), rightDrawable.getMinimumHeight());
                holderView.check_tv.setCompoundDrawables(rightDrawable, null, null, null);
            } else
                holderView.check_tv.setCompoundDrawables(null, null, null, null);


            if (item.getRun_status().equals("0")) {
                holderView.check_tv.setText("接收任务");
                statusNum = 1; //接收状态由0设置为1
            }
            if (item.getRun_status().equals("1")) {
                holderView.check_tv.setText("完成任务");
                statusNum = 2;//接收状态由1设置为2
            }
            if (item.getRun_status().equals("2")) {
                holderView.check_tv.setText("已完成");
                statusNum = 100; //已经完成无需修改了
            }
            return convertView;
        }

        public class HolderView {
            TextView task_id_tv, start_point_tv, end_point_tv,
                    task_detail_tv, da_task_tv, check_tv;
        }

        class checkTaskOnclickListener implements View.OnClickListener {
            private int position;

            public checkTaskOnclickListener(int position) {
                this.position = position;
            }

            @Override
            public void onClick(View v) {
                TaskVo.Rows item = rows.get(position);
                if (statusNum == 1) {
                    message = "接收编号TX" + item.getTask_id() + "任务？";
                } else if (statusNum == 2)
                    message = "完成编号TX" + item.getTask_id() + "任务？";
                AlertDialog.Builder builder = new AlertDialog.Builder(inflater.getContext());
                builder.setMessage(message);
                builder.setTitle("提示");
                builder.setPositiveButton("确认", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        carryOutTask(position);
                    }
                });
                builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                builder.create().show();
            }
        }

        public void carryOutTask(int position) {
            if (statusNum < 3) {
                TaskVo.Rows item = rows.get(position);
                OkHttpUtils.post()
                        .url(Urls.UPDATE_TASK)//vehicle
                        .addParams("task_id", item.getTask_id())
                        .addParams("status", statusNum + "")
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
                                    resultVo = GjsonUtil.parseJsonWithGson(response, ResultVo.class);
                                    if (resultVo != null && resultVo.isResult()) {
                                        ToastUtil.showToast(resultVo.getMessage());
                                        uiHandler.sendEmptyMessage(UPDATE_TASK);
                                    } else
                                        ToastUtil.showToast(resultVo.getMessage());
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        });
            }
        }
    }
}
