package com.xaqinren.healthyelders.apiserver;

import com.xaqinren.healthyelders.global.CodeTable;

/**
 * 该类仅供参考，实际业务返回的固定字段, 根据需求来定义，
 */
public class MBaseResponse<T> {
    private String code;
    private String msg;
    private int status;
    private T data;

    public String getCode() {
        if (code == null) {
            return status + "";
        }
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }


    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public boolean isOk() {
        if (status > 0) {
            return false;
        }
        return code.equals(CodeTable.SUCCESS_CODE);
    }

    public String getMessage() {
        return msg;
    }

    public void setMessage(String message) {
        this.msg = message;
    }
}
