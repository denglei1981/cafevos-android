package com.changanford.common.bean;

import java.util.List;

public class DtoBean {

    private String activityAddr ;//活动详细地址
    private Integer activityTotalCount ;//活动限制人数，传-1表示不限制
    private String beginTime ;//开始时间

    private String content ;//活动描述
    private List<ContentImg> contentImgList ;//内容图片
    private List<AttributeBean.AttributesInfoBean.AttributeListBean> attributes; //填写资料数组，数组顺序是用户填写的顺序
    private String coverImgUrl;//封面图片
    private String deadLineTime;//截止时间
    private String endTime;//结束时间
    private String latitude;//纬度
    private String longitude;//经度
    private String title;//标题
    private String cityId ;//市id（国标）
    private String cityName ;//城市名称
    private String provinceId;//省id（国标）
    private String provinceName;//省名称
    private String townId;//区id（国标）-获取不到的话可不穿
    private String townName;//区名称

    public String getActivityAddr() {
        return activityAddr;
    }

    public void setActivityAddr(String activityAddr) {
        this.activityAddr = activityAddr;
    }

    public Integer getActivityTotalCount() {
        return activityTotalCount;
    }

    public void setActivityTotalCount(Integer activityTotalCount) {
        this.activityTotalCount = activityTotalCount;
    }

    public String getBeginTime() {
        return beginTime;
    }

    public void setBeginTime(String beginTime) {
        this.beginTime = beginTime;
    }

    public String getCityId() {
        return cityId;
    }

    public void setCityId(String cityId) {
        this.cityId = cityId;
    }

    public String getCityName() {
        return cityName;
    }

    public void setCityName(String cityName) {
        this.cityName = cityName;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public List<ContentImg> getContentImgList() {
        return contentImgList;
    }

    public void setContentImgList(List<ContentImg> contentImgList) {
        this.contentImgList = contentImgList;
    }

    public List<AttributeBean.AttributesInfoBean.AttributeListBean> getAttributes() {
        return attributes;
    }

    public void setAttributes(List<AttributeBean.AttributesInfoBean.AttributeListBean> attributes) {
        this.attributes = attributes;
    }

    public String getCoverImgUrl() {
        return coverImgUrl;
    }

    public void setCoverImgUrl(String coverImgUrl) {
        this.coverImgUrl = coverImgUrl;
    }

    public String getDeadLineTime() {
        return deadLineTime;
    }

    public void setDeadLineTime(String deadLineTime) {
        this.deadLineTime = deadLineTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public String getProvinceId() {
        return provinceId;
    }

    public void setProvinceId(String provinceId) {
        this.provinceId = provinceId;
    }

    public String getProvinceName() {
        return provinceName;
    }

    public void setProvinceName(String provinceName) {
        this.provinceName = provinceName;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getTownId() {
        return townId;
    }

    public void setTownId(String townId) {
        this.townId = townId;
    }

    public String getTownName() {
        return townName;
    }

    public void setTownName(String townName) {
        this.townName = townName;
    }

    public class Attribute{
        private Integer attributeId ;//属性id
        private String attributeName ;//属性名称
        private Integer attributeType ;//字段类型（0-电话，1-其他）
        private Integer defaultCheck ;//是否默认勾选
        private Integer mustInput ;//是否必填

        public Integer getAttributeId() {
            return attributeId;
        }

        public void setAttributeId(Integer attributeId) {
            this.attributeId = attributeId;
        }

        public String getAttributeName() {
            return attributeName;
        }

        public void setAttributeName(String attributeName) {
            this.attributeName = attributeName;
        }

        public Integer getAttributeType() {
            return attributeType;
        }

        public void setAttributeType(Integer attributeType) {
            this.attributeType = attributeType;
        }

        public Integer getDefaultCheck() {
            return defaultCheck;
        }

        public void setDefaultCheck(Integer defaultCheck) {
            this.defaultCheck = defaultCheck;
        }

        public Integer getMustInput() {
            return mustInput;
        }

        public void setMustInput(Integer mustInput) {
            this.mustInput = mustInput;
        }
    }
    public static class ContentImg{
        private String contentDesc; //图片描述
        private String contentImgUrl; //图片url

        public ContentImg(String contentDesc, String contentImgUrl) {
            this.contentDesc = contentDesc;
            this.contentImgUrl = contentImgUrl;
        }
        public ContentImg() {

        }

        public String getContentDesc() {
            return contentDesc;
        }

        public void setContentDesc(String contentDesc) {
            this.contentDesc = contentDesc;
        }

        public String getContentImgUrl() {
            return contentImgUrl;
        }

        public void setContentImgUrl(String contentImgUrl) {
            this.contentImgUrl = contentImgUrl;
        }
    }
}
