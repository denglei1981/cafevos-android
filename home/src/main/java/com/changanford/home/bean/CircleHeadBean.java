package com.changanford.home.bean;

public class CircleHeadBean {

    /**
     * adId : 1
     * adImg : https://img2.autoimg.cn/admdfs/g24/M09/1B/10/ChwFjl6gE8iAOsocAAGQ4UWSmLg171.jpg
     * adName : 汽车之家春季车站
     * isVideo : 0
     * jumpDataType : 1
     * jumpDataValue : https://401cbc.autohome.com.cn/index/index.html?pvareaid=6840833
     * posId : 1
     * status : 1
     */

    private int adId;
    private String adImg;
    private String adName;
    private int isVideo;
    private int jumpDataType;
    private String jumpDataValue;
    private int posId;
    private int status;
    private boolean iscanclick =true;

    public boolean isIscanclick() {
        return iscanclick;
    }

    public void setIscanclick(boolean iscanclick) {
        this.iscanclick = iscanclick;
    }

    public int getAdId() {
        return adId;
    }

    public void setAdId(int adId) {
        this.adId = adId;
    }

    public String getAdImg() {
        return adImg;
    }

    public void setAdImg(String adImg) {
        this.adImg = adImg;
    }

    public String getAdName() {
        return adName;
    }

    public void setAdName(String adName) {
        this.adName = adName;
    }

    public int getIsVideo() {
        return isVideo;
    }

    public void setIsVideo(int isVideo) {
        this.isVideo = isVideo;
    }

    public int getJumpDataType() {
        return jumpDataType;
    }

    public void setJumpDataType(int jumpDataType) {
        this.jumpDataType = jumpDataType;
    }

    public String getJumpDataValue() {
        return jumpDataValue;
    }

    public void setJumpDataValue(String jumpDataValue) {
        this.jumpDataValue = jumpDataValue;
    }

    public int getPosId() {
        return posId;
    }

    public void setPosId(int posId) {
        this.posId = posId;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }
}
