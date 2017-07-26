package com.cimcitech.forkliftdispatch.model;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by cimcitech on 2017/6/8.
 */

public class DriverVo implements Serializable {

    private int cmd;
    private int total;
    private ArrayList<Rows> rows;

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

    public ArrayList<Rows> getRows() {
        return rows;
    }

    public void setRows(ArrayList<Rows> rows) {
        this.rows = rows;
    }

    public class Rows implements Serializable {

        private String driver_name;
        private String company_id;
        private String device_id;
        private String vehicle_no;
        private String driver_no;
        private String device_no;
        private String tire_no;

        public String getDriver_name() {
            return driver_name;
        }

        public void setDriver_name(String driver_name) {
            this.driver_name = driver_name;
        }

        public String getCompany_id() {
            return company_id;
        }

        public void setCompany_id(String company_id) {
            this.company_id = company_id;
        }

        public String getDevice_id() {
            return device_id;
        }

        public void setDevice_id(String device_id) {
            this.device_id = device_id;
        }

        public String getVehicle_no() {
            return vehicle_no;
        }

        public void setVehicle_no(String vehicle_no) {
            this.vehicle_no = vehicle_no;
        }

        public String getDriver_no() {
            return driver_no;
        }

        public void setDriver_no(String driver_no) {
            this.driver_no = driver_no;
        }

        public String getDevice_no() {
            return device_no;
        }

        public void setDevice_no(String device_no) {
            this.device_no = device_no;
        }

        public String getTire_no() {
            return tire_no;
        }

        public void setTire_no(String tire_no) {
            this.tire_no = tire_no;
        }
    }


}
