package com.cimcitech.forkliftdispatch.model;

/**
 * Created by cimcitech on 2017/6/13.
 */

public class ResultVo {

    private String Message;
    private boolean Result;

    public String getMessage() {
        return Message;
    }

    public void setMessage(String message) {
        Message = message;
    }

    public boolean isResult() {
        return Result;
    }

    public void setResult(boolean result) {
        Result = result;
    }
}
