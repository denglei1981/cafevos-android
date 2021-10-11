package com.changanford.common.bean;

import java.util.List;

public class QueryDetail {

    /**
     * isCollect : NO
     * isFollow : NO
     * isParted : NO
     * queryDetail : {"browseCount":-82317494,"coverImgUrl":"exercitation irure","deadlineTime":"1946-09-29T00:21:54.008Z","publisherUserHeadImg":"Lorem ad ","publisherUserId":-36894959,"publisherUserName":"officia","queryName":"cons","questions":[{"isQuestionNecessary":-97767573,"optionList":[{"count":-87085132,"isSelected":-50167469,"optionImgUrl":"nulla","optionName":"irure laborum mollit in aliqua","percent":-6.719143829480161E7,"surveyOptionId":-36194794}],"questionAnswer":"ex et in","questionImgUrl":"nostrud esse non ","questionInfo":"veniam adipisicing amet enim mollit","questionType":37256151,"surveyQuestionId":-28180685},{"isQuestionNecessary":68255564,"optionList":[{"count":16529234,"isSelected":10594413,"optionImgUrl":"ipsum voluptate","optionName":"deserunt minim","percent":-8.824880528546846E7,"surveyOptionId":-31140474},{"count":53668073,"isSelected":93348569,"optionImgUrl":"ullamco laboris culpa laborum","optionName":"veniam quis Ut dolore","percent":9.422695698379186E7,"surveyOptionId":97060369}],"questionAnswer":"ad","questionImgUrl":"deserunt dolor consequat","questionInfo":"proident ipsum","questionType":19698295,"surveyQuestionId":-33085200},{"isQuestionNecessary":49802328,"optionList":[{"count":-18837963,"isSelected":43150727,"optionImgUrl":"si","optionName":"Ut pariatur dolore quis","percent":-1.5660740197319716E7,"surveyOptionId":-76768039},{"count":36257392,"isSelected":-93163376,"optionImgUrl":"Lorem","optionName":"et officia sed pariatur","percent":-2.732538101803246E7,"surveyOptionId":-86965693}],"questionAnswer":"Excepteur laboris","questionImgUrl":"ut","questionInfo":"aliqua","questionType":11284946,"surveyQuestionId":-84465387}],"surveyId":52508729}
     * shareBeanVO : {"shareDesc":"dolor incididunt in id sunt","shareImg":"nulla nostrud dolore laboris","shareTitle":"dolore occaecat","shareUrl":"in tempor mollit sint","wxminiprogramCode":"consectetur magna aliquip ex"}
     */

    private String isCollect;
    private String isFollow;
    private String isParted;
    private QueryDetailBean queryDetail;
    private ShareBeanVOBean shareBeanVO;

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

    public String getIsParted() {
        return isParted;
    }

    public void setIsParted(String isParted) {
        this.isParted = isParted;
    }

    public QueryDetailBean getQueryDetail() {
        return queryDetail;
    }

    public void setQueryDetail(QueryDetailBean queryDetail) {
        this.queryDetail = queryDetail;
    }

    public ShareBeanVOBean getShareBeanVO() {
        return shareBeanVO;
    }

    public void setShareBeanVO(ShareBeanVOBean shareBeanVO) {
        this.shareBeanVO = shareBeanVO;
    }

    public static class QueryDetailBean {
        /**
         * browseCount : -82317494
         * coverImgUrl : exercitation irure
         * deadlineTime : 1946-09-29T00:21:54.008Z
         * publisherUserHeadImg : Lorem ad
         * publisherUserId : -36894959
         * publisherUserName : officia
         * queryName : cons
         * questions : [{"isQuestionNecessary":-97767573,"optionList":[{"count":-87085132,"isSelected":-50167469,"optionImgUrl":"nulla","optionName":"irure laborum mollit in aliqua","percent":-6.719143829480161E7,"surveyOptionId":-36194794}],"questionAnswer":"ex et in","questionImgUrl":"nostrud esse non ","questionInfo":"veniam adipisicing amet enim mollit","questionType":37256151,"surveyQuestionId":-28180685},{"isQuestionNecessary":68255564,"optionList":[{"count":16529234,"isSelected":10594413,"optionImgUrl":"ipsum voluptate","optionName":"deserunt minim","percent":-8.824880528546846E7,"surveyOptionId":-31140474},{"count":53668073,"isSelected":93348569,"optionImgUrl":"ullamco laboris culpa laborum","optionName":"veniam quis Ut dolore","percent":9.422695698379186E7,"surveyOptionId":97060369}],"questionAnswer":"ad","questionImgUrl":"deserunt dolor consequat","questionInfo":"proident ipsum","questionType":19698295,"surveyQuestionId":-33085200},{"isQuestionNecessary":49802328,"optionList":[{"count":-18837963,"isSelected":43150727,"optionImgUrl":"si","optionName":"Ut pariatur dolore quis","percent":-1.5660740197319716E7,"surveyOptionId":-76768039},{"count":36257392,"isSelected":-93163376,"optionImgUrl":"Lorem","optionName":"et officia sed pariatur","percent":-2.732538101803246E7,"surveyOptionId":-86965693}],"questionAnswer":"Excepteur laboris","questionImgUrl":"ut","questionInfo":"aliqua","questionType":11284946,"surveyQuestionId":-84465387}]
         * surveyId : 52508729
         */

        private int browseCount;
        private String coverImgUrl;
        private String deadlineTime;
        private String publisherUserHeadImg;
        private int publisherUserId;
        private String publisherUserName;
        private String queryName;
        private int surveyId;
        private List<QuestionsBean> questions;

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

        public String getQueryName() {
            return queryName;
        }

        public void setQueryName(String queryName) {
            this.queryName = queryName;
        }

        public int getSurveyId() {
            return surveyId;
        }

        public void setSurveyId(int surveyId) {
            this.surveyId = surveyId;
        }

        public List<QuestionsBean> getQuestions() {
            return questions;
        }

        public void setQuestions(List<QuestionsBean> questions) {
            this.questions = questions;
        }

        public static class QuestionsBean {
            /**
             * isQuestionNecessary : -97767573
             * optionList : [{"count":-87085132,"isSelected":-50167469,"optionImgUrl":"nulla","optionName":"irure laborum mollit in aliqua","percent":-6.719143829480161E7,"surveyOptionId":-36194794}]
             * questionAnswer : ex et in
             * questionImgUrl : nostrud esse non
             * questionInfo : veniam adipisicing amet enim mollit
             * questionType : 37256151
             * surveyQuestionId : -28180685
             */

            private int isQuestionNecessary;
            private String questionAnswer;
            private String questionImgUrl;
            private String questionInfo;
            private int questionType;
            private int surveyQuestionId;
            private List<OptionListBean> optionList;

            public int getIsQuestionNecessary() {
                return isQuestionNecessary;
            }

            public void setIsQuestionNecessary(int isQuestionNecessary) {
                this.isQuestionNecessary = isQuestionNecessary;
            }

            public String getQuestionAnswer() {
                return questionAnswer;
            }

            public void setQuestionAnswer(String questionAnswer) {
                this.questionAnswer = questionAnswer;
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

            public int getSurveyQuestionId() {
                return surveyQuestionId;
            }

            public void setSurveyQuestionId(int surveyQuestionId) {
                this.surveyQuestionId = surveyQuestionId;
            }

            public List<OptionListBean> getOptionList() {
                return optionList;
            }

            public void setOptionList(List<OptionListBean> optionList) {
                this.optionList = optionList;
            }

            public static class OptionListBean {
                /**
                 * count : -87085132
                 * isSelected : -50167469
                 * optionImgUrl : nulla
                 * optionName : irure laborum mollit in aliqua
                 * percent : -6.719143829480161E7
                 * surveyOptionId : -36194794
                 */

                private int count;
                private int isSelected;
                private String optionImgUrl;
                private String optionName;
                private double percent;
                private int surveyOptionId;

                public int getCount() {
                    return count;
                }

                public void setCount(int count) {
                    this.count = count;
                }

                public int getIsSelected() {
                    return isSelected;
                }

                public void setIsSelected(int isSelected) {
                    this.isSelected = isSelected;
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

                public double getPercent() {
                    return percent;
                }

                public void setPercent(double percent) {
                    this.percent = percent;
                }

                public int getSurveyOptionId() {
                    return surveyOptionId;
                }

                public void setSurveyOptionId(int surveyOptionId) {
                    this.surveyOptionId = surveyOptionId;
                }
            }
        }
    }

    public static class ShareBeanVOBean {
        /**
         * shareDesc : dolor incididunt in id sunt
         * shareImg : nulla nostrud dolore laboris
         * shareTitle : dolore occaecat
         * shareUrl : in tempor mollit sint
         * wxminiprogramCode : consectetur magna aliquip ex
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
