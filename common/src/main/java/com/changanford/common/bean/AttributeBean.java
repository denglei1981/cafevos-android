package com.changanford.common.bean;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class AttributeBean {

    /**
     * attributesInfo : {"attributeList":[{"attributeId":-10419076,"attributeName":"pariatur laboris ad nulla","attributeType":-23312887,"defaultCheck":-72583994,"mustInput":-95104119},{"attributeId":26060166,"attributeName":"est ea laboris aute elit","attributeType":39810216,"defaultCheck":91030600,"mustInput":73013852},{"attributeId":-28079519,"attributeName":"ad Lorem sint ","attributeType":92352488,"defaultCheck":85566618,"mustInput":28315660},{"attributeId":-48710143,"attributeName":"laborum sint","attributeType":-64081863,"defaultCheck":-39719160,"mustInput":19537991}]}
     */

    private AttributesInfoBean attributesInfo;

    public AttributesInfoBean getAttributesInfo() {
        return attributesInfo;
    }

    public void setAttributesInfo(AttributesInfoBean attributesInfo) {
        this.attributesInfo = attributesInfo;
    }
    public static class AttributesInfoBean {
        private ArrayList<AttributeCategoryVos> attributeCategoryVos;

        public ArrayList<AttributeCategoryVos> getAttributeCategoryVos() {
            return attributeCategoryVos;
        }

        public void setAttributeCategoryVos(ArrayList<AttributeCategoryVos> attributeCategoryVos) {
            this.attributeCategoryVos = attributeCategoryVos;
        }
    }

    public static class AttributeCategoryVos {

        private int categoryId;//
        private String categoryName;//基本属性
        private List<AttributeListBean> attributeList;

        public int getCategoryId() {
            return categoryId;
        }

        public void setCategoryId(int categoryId) {
            this.categoryId = categoryId;
        }

        public String getCategoryName() {
            return categoryName;
        }

        public void setCategoryName(String categoryName) {
            this.categoryName = categoryName;
        }

        public List<AttributeListBean> getAttributeList() {
            return attributeList;
        }

        public void setAttributeList(List<AttributeListBean> attributeList) {
            this.attributeList = attributeList;
        }

        public static class AttributeListBean implements Serializable {
            /**
             * attributeId : -10419076
             * attributeName : pariatur laboris ad nulla
             * attributeType : -23312887
             * defaultCheck : -72583994
             * mustInput : -95104119
             */
            private int checktype ; //标记是否选择中
            private String attributeContent;//

            public String getAttributeContent() {
                return attributeContent;
            }

            public void setAttributeContent(String attributeContent) {
                this.attributeContent = attributeContent;
            }

            public int getChecktype() {
                return checktype;
            }

            public void setChecktype(int checktype) {
                this.checktype = checktype;
            }

            private int attributeId;  //属性id
            private String attributeName; //属性名称
            private String attributeType; //字段类型（0-电话，1-其他）
            private int defaultCheck; //是否默认勾选
            private int mustInput;  //是否必填

            public int getAttributeId() {
                return attributeId;
            }

            public void setAttributeId(int attributeId) {
                this.attributeId = attributeId;
            }

            public String getAttributeName() {
                return attributeName;
            }

            public void setAttributeName(String attributeName) {
                this.attributeName = attributeName;
            }

            public String getAttributeType() {
                return attributeType;
            }

            public void setAttributeType(String attributeType) {
                this.attributeType = attributeType;
            }

            public int getDefaultCheck() {
                return defaultCheck;
            }

            public void setDefaultCheck(int defaultCheck) {
                this.defaultCheck = defaultCheck;
            }

            public int getMustInput() {
                return mustInput;
            }

            public void setMustInput(int mustInput) {
                this.mustInput = mustInput;
            }
        }
    }
}
