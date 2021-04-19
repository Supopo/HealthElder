package me.goldze.mvvmhabit.http;

/**
 * 该类仅供参考，实际业务返回的固定字段, 根据需求来定义，
 */
public class BaseResponse<T> {
    private int status;
    private String message;
    private T data;

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }


    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public boolean isOk() {
        return status == 100;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
