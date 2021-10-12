package com.changanford.home.data;


public class EnumBean {
    private Object code;
    private String message;

    public EnumBean(Object code, String message) {
        this.code = code;
        this.message = message;
    }

    public EnumBean(Object code) {
        this.code = code;
    }

    public Object getCode() {
        return code;
    }

    public void setCode(Object code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
