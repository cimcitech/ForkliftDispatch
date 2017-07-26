package com.cimcitech.forkliftdispatch.model;

import java.util.ArrayList;

/**
 * Created by cimcitech on 2017/6/14.
 */

public class VersionVo {

    private int total;
    private ArrayList<Rows> rows;

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

    public class Rows {
        private String da_add;
        private String remark;
        private String id;
        private String version;
        private String url;

        public String getDa_add() {
            return da_add;
        }

        public void setDa_add(String da_add) {
            this.da_add = da_add;
        }

        public String getRemark() {
            return remark;
        }

        public void setRemark(String remark) {
            this.remark = remark;
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getVersion() {
            return version;
        }

        public void setVersion(String version) {
            this.version = version;
        }

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }
    }
}
