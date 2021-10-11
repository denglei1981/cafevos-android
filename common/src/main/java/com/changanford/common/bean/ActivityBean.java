package com.changanford.common.bean;

import java.util.List;

/**
 * 活动详情
 */
public class ActivityBean {

    /**
     * activityInfo : {"activityJoinCount":15082175,"activityName":"minim quis velit","activityTotalCount":70610069,"activityTxt":"ea qui ipsum","addr":"adipisicing fugiat tempor dolore","attributeDtos":[{"activityAttributeRelationId":21961339,"attributeName":"incididunt reprehenderit qui","attributeType":-64715775,"defaultValue":"eiusmod","mustInput":-85141278},{"activityAttributeRelationId":-71716323,"attributeName":"d","attributeType":84020911,"defaultValue":"sit consectetur nostrud","mustInput":-55285662}],"beginTime":"1954-02-13T07:16:14.509Z","browseCount":-51536120,"coverImgUrl":"voluptat","deadlineTime":"2002-08-02T03:14:05.922Z","endTime":"2001-11-11T01:31:41.172Z","latitude":"magna do incididunt in","longitude":"cillum","parted":true,"pictures":[{"imgTxt":"laboris consequat velit minim","imgUrl":"minim Ex"},{"imgTxt":"deserunt laboris","imgUrl":"voluptate exercitation aliquip"},{"imgTxt":"ip","imgUrl":"ut tempor amet"},{"imgTxt":"commodo et enim est incididunt","imgUrl":"esse cupidatat ut Lorem"}],"publisherUserHeadImg":"nulla","publisherUserId":-96222285,"publisherUserName":"enim dolore nisi","status":21007444}
     * isCollect : YES
     * isFollow : YES
     * shareBeanVO : {"shareDesc":"Ut","shareImg":"culpa enim ad","shareTitle":"in laborum dolor ut enim","shareUrl":"anim","wxminiprogramCode":"nulla sint voluptate"}
     */

    private ActivityInfoBean activityInfo;
    private String isCollect;
    private String isFollow;
    private ShareBeanVOBean shareBeanVO;

    public ActivityInfoBean getActivityInfo() {
        return activityInfo;
    }

    public void setActivityInfo(ActivityInfoBean activityInfo) {
        this.activityInfo = activityInfo;
    }

    public String getIsCollect() {
        return isCollect;
    }

    public void setIsCollect(String isCollect) {
        this.isCollect = isCollect;
    }

    public String getIsFollow() {
        return isFollow;
    }

    public void setIsFollow(String isFollow) {
        this.isFollow = isFollow;
    }

    public ShareBeanVOBean getShareBeanVO() {
        return shareBeanVO;
    }

    public void setShareBeanVO(ShareBeanVOBean shareBeanVO) {
        this.shareBeanVO = shareBeanVO;
    }

    public static class ActivityInfoBean {
        /**
         * activityJoinCount : 15082175
         * activityName : minim quis velit
         * activityTotalCount : 70610069
         * activityTxt : ea qui ipsum
         * addr : adipisicing fugiat tempor dolore
         * attributeDtos : [{"activityAttributeRelationId":21961339,"attributeName":"incididunt reprehenderit qui","attributeType":-64715775,"defaultValue":"eiusmod","mustInput":-85141278},{"activityAttributeRelationId":-71716323,"attributeName":"d","attributeType":84020911,"defaultValue":"sit consectetur nostrud","mustInput":-55285662}]
         * beginTime : 1954-02-13T07:16:14.509Z
         * browseCount : -51536120
         * coverImgUrl : voluptat
         * deadlineTime : 2002-08-02T03:14:05.922Z
         * endTime : 2001-11-11T01:31:41.172Z
         * latitude : magna do incididunt in
         * longitude : cillum
         * parted : true
         * pictures : [{"imgTxt":"laboris consequat velit minim","imgUrl":"minim Ex"},{"imgTxt":"deserunt laboris","imgUrl":"voluptate exercitation aliquip"},{"imgTxt":"ip","imgUrl":"ut tempor amet"},{"imgTxt":"commodo et enim est incididunt","imgUrl":"esse cupidatat ut Lorem"}]
         * publisherUserHeadImg : nulla
         * publisherUserId : -96222285
         * publisherUserName : enim dolore nisi
         * status : 21007444
         */
        private String cityId ;//市id（国标）
        private String cityName ;//城市名称
        private String provinceId;//省id（国标）
        private String provinceName;//省名称
        private String townId;//区id（国标）-获取不到的话可不穿
        private String townName;//区名称
        private int activityJoinCount;
        private String activityName;
        private int activityTotalCount;
        private String activityTxt;
        private String addr;
        private String beginTime;
        private int browseCount;
        private String coverImgUrl;
        private String deadlineTime;
        private String endTime;

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

        private String latitude;
        private String longitude;
        private boolean parted;
        private String publisherUserHeadImg;
        private int publisherUserId;
        private String publisherUserName;
        private int status;
        private List<AttributeDtosBean> attributeDtos;
        private List<PicturesBean> pictures;

        public int getActivityJoinCount() {
            return activityJoinCount;
        }

        public void setActivityJoinCount(int activityJoinCount) {
            this.activityJoinCount = activityJoinCount;
        }

        public String getActivityName() {
            return activityName;
        }

        public void setActivityName(String activityName) {
            this.activityName = activityName;
        }

        public int getActivityTotalCount() {
            return activityTotalCount;
        }

        public void setActivityTotalCount(int activityTotalCount) {
            this.activityTotalCount = activityTotalCount;
        }

        public String getActivityTxt() {
            return activityTxt;
        }

        public void setActivityTxt(String activityTxt) {
            this.activityTxt = activityTxt;
        }

        public String getAddr() {
            return addr;
        }

        public void setAddr(String addr) {
            this.addr = addr;
        }

        public String getBeginTime() {
            return beginTime;
        }

        public void setBeginTime(String beginTime) {
            this.beginTime = beginTime;
        }

        public int getBrowseCount() {
            return browseCount;
        }

        public void setBrowseCount(int browseCount) {
            this.browseCount = browseCount;
        }

        public String getCoverImgUrl() {
            return coverImgUrl;
        }

        public void setCoverImgUrl(String coverImgUrl) {
            this.coverImgUrl = coverImgUrl;
        }

        public String getDeadlineTime() {
            return deadlineTime;
        }

        public void setDeadlineTime(String deadlineTime) {
            this.deadlineTime = deadlineTime;
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

        public boolean isParted() {
            return parted;
        }

        public void setParted(boolean parted) {
            this.parted = parted;
        }

        public String getPublisherUserHeadImg() {
            return publisherUserHeadImg;
        }

        public void setPublisherUserHeadImg(String publisherUserHeadImg) {
            this.publisherUserHeadImg = publisherUserHeadImg;
        }

        public int getPublisherUserId() {
            return publisherUserId;
        }

        public void setPublisherUserId(int publisherUserId) {
            this.publisherUserId = publisherUserId;
        }

        public String getPublisherUserName() {
            return publisherUserName;
        }

        public void setPublisherUserName(String publisherUserName) {
            this.publisherUserName = publisherUserName;
        }

        public int getStatus() {
            return status;
        }

        public void setStatus(int status) {
            this.status = status;
        }

        public List<AttributeDtosBean> getAttributeDtos() {
            return attributeDtos;
        }

        public void setAttributeDtos(List<AttributeDtosBean> attributeDtos) {
            this.attributeDtos = attributeDtos;
        }

        public List<PicturesBean> getPictures() {
            return pictures;
        }

        public void setPictures(List<PicturesBean> pictures) {
            this.pictures = pictures;
        }

        public static class AttributeDtosBean {
            /**
             * activityAttributeRelationId : 21961339
             * attributeName : incididunt reprehenderit qui
             * attributeType : -64715775
             * defaultValue : eiusmod
             * mustInput : -85141278
             */

            private int activityAttributeRelationId;
            private String attributeName;
            private int attributeType;
            private String defaultValue;
            private int mustInput;

            public int getActivityAttributeRelationId() {
                return activityAttributeRelationId;
            }

            public void setActivityAttributeRelationId(int activityAttributeRelationId) {
                this.activityAttributeRelationId = activityAttributeRelationId;
            }

            public String getAttributeName() {
                return attributeName;
            }

            public void setAttributeName(String attributeName) {
                this.attributeName = attributeName;
            }

            public int getAttributeType() {
                return attributeType;
            }

            public void setAttributeType(int attributeType) {
                this.attributeType = attributeType;
            }

            public String getDefaultValue() {
                return defaultValue;
            }

            public void setDefaultValue(String defaultValue) {
                this.defaultValue = defaultValue;
            }

            public int getMustInput() {
                return mustInput;
            }

            public void setMustInput(int mustInput) {
                this.mustInput = mustInput;
            }
        }

        public static class PicturesBean {
            /**
             * imgTxt : laboris consequat velit minim
             * imgUrl : minim Ex
             */

            private String imgTxt;
            private String imgUrl;

            public String getImgTxt() {
                return imgTxt;
            }

            public void setImgTxt(String imgTxt) {
                this.imgTxt = imgTxt;
            }

            public String getImgUrl() {
                return imgUrl;
            }

            public void setImgUrl(String imgUrl) {
                this.imgUrl = imgUrl;
            }
        }
    }

    public static class ShareBeanVOBean {
        /**
         * shareDesc : Ut
         * shareImg : culpa enim ad
         * shareTitle : in laborum dolor ut enim
         * shareUrl : anim
         * wxminiprogramCode : nulla sint voluptate
         */

        private String shareDesc;
        private String shareImg;
        private String shareTitle;
        private String shareUrl;
        private String wxminiprogramCode;

        public String getShareDesc() {
            return shareDesc;
        }

        public void setShareDesc(String shareDesc) {
            this.shareDesc = shareDesc;
        }

        public String getShareImg() {
            return shareImg;
        }

        public void setShareImg(String shareImg) {
            this.shareImg = shareImg;
        }

        public String getShareTitle() {
            return shareTitle;
        }

        public void setShareTitle(String shareTitle) {
            this.shareTitle = shareTitle;
        }

        public String getShareUrl() {
            return shareUrl;
        }

        public void setShareUrl(String shareUrl) {
            this.shareUrl = shareUrl;
        }

        public String getWxminiprogramCode() {
            return wxminiprogramCode;
        }

        public void setWxminiprogramCode(String wxminiprogramCode) {
            this.wxminiprogramCode = wxminiprogramCode;
        }
    }
}
