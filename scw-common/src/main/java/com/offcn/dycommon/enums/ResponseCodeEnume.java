package com.offcn.dycommon.enums;

public enum  ResponseCodeEnume {

    SUCCESS(200,"操作成功"),
    FAIL(1,"服务异常"),
    NOT_FOUND(404,"资源未找到"),
    NOT_AUTHED(403,"无权限，拒绝访问"),
    PARAM_INVAILD(400,"提交非法参数");

    private Integer code;
    private String message;

    private ResponseCodeEnume() {
    }

    private ResponseCodeEnume(Integer code, String message) {
        this.code = code;
        this.message = message;
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
}
