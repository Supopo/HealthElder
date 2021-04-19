package com.xaqinren.healthyelders.apiserver;

public class ApiException extends RuntimeException {
    public int status;
    public String msg;

    public ApiException(int code, String message) {
        this.status = code;
        this.msg = message;
    }
}
