package com.terwergreen.jvue.util;

/**
 * @author terwergreen
 * 接口返回数据模型DTO
 */
public class RestResponse {

    /**
     * 状态码（数字节点），包括通用状态码及业务状态
     */
    private Integer status;

    /**
     * 消息
     */
    private String msg;

    /**
     * 数据节点（对象节点），包含接口中的业务数据
     */
    private Object data;

    public RestResponse() {
    }

    public RestResponse(Integer status) {
        this.status = status;
    }

    public RestResponse(Integer status, String msg) {
        this.status = status;
        this.msg = msg;
    }

    public RestResponse(Integer status, String msg, Object data) {
        this.status = status;
        this.msg = msg;
        this.data = data;
    }

    public RestResponse(RestResponse dto) {
        if (null == dto || null == dto.getStatus()) {
            this.status = RestResponseStates.SUCCESS.getValue();
            this.msg = RestResponseStates.SUCCESS.getMsg();
        } else {
            this.status = dto.getStatus();
            this.msg = dto.getMsg();
            this.data = dto.getData();
        }
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }
}
