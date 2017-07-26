package com.cimcitech.forkliftdispatch.model;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by cimcitech on 2017/6/10.
 */

public class TaskVo implements Serializable {

    private int cmd;
    private int total;
    private ArrayList<Rows> rows;

    public ArrayList<Rows> getRows() {
        return rows;
    }

    public void setRows(ArrayList<Rows> rows) {
        this.rows = rows;
    }

    public int getCmd() {
        return cmd;
    }

    public void setCmd(int cmd) {
        this.cmd = cmd;
    }

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public class Rows implements Serializable {
        private String task_id;
        private String driver_name;
        private String da_task;
        private String task_detail;
        private String start_point;
        private String end_point;
        private String device_no;
        private String run_status;

        public String getTask_id() {
            return task_id;
        }

        public void setTask_id(String task_id) {
            this.task_id = task_id;
        }

        public String getDriver_name() {
            return driver_name;
        }

        public void setDriver_name(String driver_name) {
            this.driver_name = driver_name;
        }

        public String getDa_task() {
            return da_task;
        }

        public void setDa_task(String da_task) {
            this.da_task = da_task;
        }

        public String getTask_detail() {
            return task_detail;
        }

        public void setTask_detail(String task_detail) {
            this.task_detail = task_detail;
        }

        public String getStart_point() {
            return start_point;
        }

        public void setStart_point(String start_point) {
            this.start_point = start_point;
        }

        public String getEnd_point() {
            return end_point;
        }

        public void setEnd_point(String end_point) {
            this.end_point = end_point;
        }

        public String getDevice_no() {
            return device_no;
        }

        public void setDevice_no(String device_no) {
            this.device_no = device_no;
        }

        public String getRun_status() {
            return run_status;
        }

        public void setRun_status(String run_status) {
            this.run_status = run_status;
        }
    }
}
