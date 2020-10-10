package com.offcn.dycommon.response;

import com.offcn.dycommon.enums.ResponseCodeEnume;

public class AppResponse<T> {

    private Integer code;
    private String message;
    private T data;

    public AppResponse() {
    }

    public AppResponse(Integer code, String message, T data) {
        this.code = code;
        this.message = message;
        this.data = data;
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }
    /**
     * 快速响应成功
     * @param data
     * @return
     */
    public static<T> AppResponse<T> ok(T data){
        AppResponse<T> resp = new AppResponse<T>();
        resp.setCode(ResponseCodeEnume.SUCCESS.getCode());
        resp.setMessage(ResponseCodeEnume.SUCCESS.getMessage());
        resp.setData(data);
        return resp;
    }

    /**
     * 快速响应失败
     */
    public static<T> AppResponse<T> fail(T data){
        AppResponse<T> resp = new AppResponse<T>();
        resp.setCode(ResponseCodeEnume.FAIL.getCode());
        resp.setMessage(ResponseCodeEnume.FAIL.getMessage());
        resp.setData(data);
        return resp;
    }
}
