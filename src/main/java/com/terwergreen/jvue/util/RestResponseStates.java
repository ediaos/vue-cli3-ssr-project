package com.terwergreen.jvue.util;

/**
 * @author terwergreen Rest响应客户端的状态值
 */
public enum RestResponseStates {

    SUCCESS(1, "请求成功，无任何异常"),

    SERVER_ERROR(2, "系统忙，请稍候再试。如有疑问请联系管理员"),

    INVALID_ACCOUNT(2000, "该账号尚未注册"),

    INVALID_PASSWORD(2001, "密码错误，请重新输入");

    private Integer value;
    private String msg;

    RestResponseStates(Integer value, String msg) {
        this.value = value;
        this.msg = msg;
    }

    public Integer getValue() {
        return value;
    }

    public void setValue(Integer value) {
        this.value = value;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    @Override
    public String toString() {
        return this.getValue().toString();
    }
}