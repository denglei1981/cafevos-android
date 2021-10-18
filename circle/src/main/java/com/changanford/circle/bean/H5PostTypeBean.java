package com.changanford.circle.bean;


public class H5PostTypeBean {

    /**
     * circleId : 1
     * circleName : 圈子名字
     * topicId : 1
     * topicName : 话题名字
     * “ext” : 额外字段-标识
     * activityId : 132154646
     */

    private String circleId;
    private String circleName;
    private String topicId;
    private String topicName;
    private String ext; // FIXME check this code

    public String getCircleId() {
        return circleId;
    }

    public void setCircleId(String circleId) {
        this.circleId = circleId;
    }

    public String getCircleName() {
        return circleName;
    }

    public void setCircleName(String circleName) {
        this.circleName = circleName;
    }

    public String getTopicId() {
        return topicId;
    }

    public void setTopicId(String topicId) {
        this.topicId = topicId;
    }

    public String getTopicName() {
        return topicName;
    }

    public void setTopicName(String topicName) {
        this.topicName = topicName;
    }

    public String getExt() {
        return ext;
    }

    public void setExt(String ext) {
        this.ext = ext;
    }

}
