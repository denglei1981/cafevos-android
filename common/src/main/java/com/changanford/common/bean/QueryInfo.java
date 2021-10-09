package com.changanford.common.bean;

import java.io.Serializable;
import java.util.List;

public class QueryInfo implements Serializable {
    private String coverImgUrl;  //封面图
    private String deadlineTime; //截止时间
    private String title;  //标题
    private List<QuessionBean> questionList;  //问题（前端传入顺序作为问卷顺序）

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

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public List<QuessionBean> getQuestionList() {
        return questionList;
    }

    public void setQuestionList(List<QuessionBean> questionList) {
        this.questionList = questionList;
    }

    public static class QuessionBean implements Serializable{
        private int isQuestionNecessary ;//是否必答，主观题需传该字段，0表示必答，1-不答
        private String questionImgUrl;  //问题图片
        private String BDquestionImgUrl;
        private String questionInfo;  //问题描述

        public String getBDquestionImgUrl() {
            return BDquestionImgUrl;
        }

        public void setBDquestionImgUrl(String BDquestionImgUrl) {
            this.BDquestionImgUrl = BDquestionImgUrl;
        }

        private int questionType;  //问题类型，(0-单选，1-多选，2-文本)
        private List<OptionBean> optionList;  //选项，顺序和传入数组顺序控制
        public static class OptionBean implements Serializable{
            private String optionImgUrl;  //选项图片
            private String optionName;  //选项名
            private String BdoptionImgUrl;

            public String getBdoptionImgUrl() {
                return BdoptionImgUrl;
            }

            public void setBdoptionImgUrl(String bdoptionImgUrl) {
                BdoptionImgUrl = bdoptionImgUrl;
            }

            public OptionBean(String optionName) {
                this.optionName = optionName;
            }

            public String getOptionImgUrl() {
                return optionImgUrl;
            }

            public void setOptionImgUrl(String optionImgUrl) {
                this.optionImgUrl = optionImgUrl;
            }

            public String getOptionName() {
                return optionName;
            }

            public void setOptionName(String optionName) {
                this.optionName = optionName;
            }
        }

        public int getIsQuestionNecessary() {
            return isQuestionNecessary;
        }

        public void setIsQuestionNecessary(int isQuestionNecessary) {
            this.isQuestionNecessary = isQuestionNecessary;
        }

        public String getQuestionImgUrl() {
            return questionImgUrl;
        }

        public void setQuestionImgUrl(String questionImgUrl) {
            this.questionImgUrl = questionImgUrl;
        }

        public String getQuestionInfo() {
            return questionInfo;
        }

        public void setQuestionInfo(String questionInfo) {
            this.questionInfo = questionInfo;
        }

        public int getQuestionType() {
            return questionType;
        }

        public void setQuestionType(int questionType) {
            this.questionType = questionType;
        }

        public List<OptionBean> getOptionList() {
            return optionList;
        }

        public void setOptionList(List<OptionBean> optionList) {
            this.optionList = optionList;
        }
    }
}
