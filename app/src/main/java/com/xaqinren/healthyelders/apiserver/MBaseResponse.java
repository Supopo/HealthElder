package com.xaqinren.healthyelders.apiserver;

/**
 * 该类仅供参考，实际业务返回的固定字段, 根据需求来定义，
 */
public class MBaseResponse<T> {
    private String code;
    private String msg;
    private T data;

    public String getCode() {
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
        return code.equals("200");
    }

    public String getMessage() {
        return msg;
    }

    public void setMessage(String message) {
        this.msg = message;
    }
}
