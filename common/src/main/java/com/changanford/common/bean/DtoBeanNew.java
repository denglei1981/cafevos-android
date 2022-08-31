package com.changanford.common.bean;

import com.luck.picture.lib.entity.LocalMedia;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class DtoBeanNew implements Serializable {

    private String activityAddr;//活动详细地址
    private Integer activityTotalCount;//活动限制人数，传-1表示不限制
    private ArrayList<AttributeBean.AttributeCategoryVos.AttributeListBean> attributes;//填写资料数组，数组顺序是用户填写的顺序
    private String beginTime;//开始时间
    private String beginTimeShow;//开始时间
    private String circleId;//圈子id
    private String cityId;//市id（国标）
    private String cityName;//城市名称
    private String content;//活动描述
    private List<ContentImg> contentImgList;//内容图片
    private String coverImgUrl;//封面图片
    private DealerSites dealerSites;//
    private String detailHtml;//内容(富文本)
    private String endTime;//结束时间
    private String endTimeShow;//结束时间
    private String latitude;//纬度
    private String longitude;//经度
    private String provinceId;//省id（国标）
    private String provinceName;//省名称
    private String signBeginTime;//报名开始时间
    private String signEndTime;//报名截止时间
    private String signBeginTimeShow;//报名开始时间显示
    private String signEndTimeShow;//报名截止时间显示
    private String title;//标题
    private String townId;//区id（国标）-获取不到的话可不穿
    private String townName;//区名称
    private String wonderfulType;//0-线上活动，1-线下活动，2-问卷,
    private String deadLineTime;//截止时间

    public String getBeginTimeShow() {
        return beginTimeShow;
    }

    public void setBeginTimeShow(String beginTimeShow) {
        this.beginTimeShow = beginTimeShow;
    }

    public String getEndTimeShow() {
        return endTimeShow;
    }

    public void setEndTimeShow(String endTimeShow) {
        this.endTimeShow = endTimeShow;
    }

    public String getSignBeginTimeShow() {
        return signBeginTimeShow;
    }

    public void setSignBeginTimeShow(String signBeginTimeShow) {
        this.signBeginTimeShow = signBeginTimeShow;
    }

    public String getSignEndTimeShow() {
        return signEndTimeShow;
    }

    public void setSignEndTimeShow(String signEndTimeShow) {
        this.signEndTimeShow = signEndTimeShow;
    }

    public ArrayList<AttributeBean.AttributeCategoryVos.AttributeListBean> getAttributes() {
        return attributes;
    }

    public void setAttributes(ArrayList<AttributeBean.AttributeCategoryVos.AttributeListBean> attributes) {
        this.attributes = attributes;
    }

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

    public String getWonderfulType() {
        return wonderfulType;
    }

    public void setWonderfulType(String wonderfulType) {
        this.wonderfulType = wonderfulType;
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

    public String getCircleId() {
        return circleId;
    }

    public void setCircleId(String circleId) {
        this.circleId = circleId;
    }

    public DealerSites getDealerSites() {
        return dealerSites;
    }

    public void setDealerSites(DealerSites dealerSites) {
        this.dealerSites = dealerSites;
    }

    public String getDetailHtml() {
        return detailHtml;
    }

    public void setDetailHtml(String detailHtml) {
        this.detailHtml = detailHtml;
    }

    public String getSignBeginTime() {
        return signBeginTime;
    }

    public void setSignBeginTime(String signBeginTime) {
        this.signBeginTime = signBeginTime;
    }

    public String getSignEndTime() {
        return signEndTime;
    }

    public void setSignEndTime(String signEndTime) {
        this.signEndTime = signEndTime;
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

    public class Attribute {
        private String attributeContent;//
        private Integer attributeId;//属性id
        private String attributeName;//属性名称
        private Integer attributeType;//字段类型（0-电话，1-其他）
        private Integer defaultCheck;//是否默认勾选
        private Integer mustInput;//是否必填

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

    public static class ContentImg implements Serializable{
        private String contentDesc; //图片描述
        private LocalMedia localMedias; //保存草稿的图片
        private String contentImgUrl; //图片url

        public LocalMedia getLocalMedias() {
            return localMedias;
        }

        public void setLocalMedias(LocalMedia localMedias) {
            this.localMedias = localMedias;
        }

        public ContentImg( String contentDesc,LocalMedia localMedias){
            this.contentDesc = contentDesc;
            this.localMedias = localMedias;
        }
        public ContentImg(String contentImgUrl, String contentDesc) {
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

    private class DealerSites implements Serializable{
        private String cityName;//			false string
        private int joinNum;//			falseinteger(int32)
        private String siteBeginTime;//			falsestring(date-time)
        private String siteDeadTime;//			falsestring(date-time)
        private String siteEndTime;//			falsestring(date-time)
        private String siteName;//			falsestring
        private int ucoin;//

        public String getCityName() {
            return cityName;
        }

        public void setCityName(String cityName) {
            this.cityName = cityName;
        }

        public int getJoinNum() {
            return joinNum;
        }

        public void setJoinNum(int joinNum) {
            this.joinNum = joinNum;
        }

        public String getSiteBeginTime() {
            return siteBeginTime;
        }

        public void setSiteBeginTime(String siteBeginTime) {
            this.siteBeginTime = siteBeginTime;
        }

        public String getSiteDeadTime() {
            return siteDeadTime;
        }

        public void setSiteDeadTime(String siteDeadTime) {
            this.siteDeadTime = siteDeadTime;
        }

        public String getSiteEndTime() {
            return siteEndTime;
        }

        public void setSiteEndTime(String siteEndTime) {
            this.siteEndTime = siteEndTime;
        }

        public String getSiteName() {
            return siteName;
        }

        public void setSiteName(String siteName) {
            this.siteName = siteName;
        }

        public int getUcoin() {
            return ucoin;
        }

        public void setUcoin(int ucoin) {
            this.ucoin = ucoin;
        }
    }
}
