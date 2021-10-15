package com.changanford.home.data;


public class EnumBean {
    private String code;
    private String message;

    public EnumBean(String code, String message) {
        this.code = code;
        this.message = message;
    }

    public EnumBean(String code) {
        this.code = code;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
