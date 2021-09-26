package com.changanford.home.search.data;

import java.io.Serializable;

public class SearchData implements Serializable {

    private static final long serialVersionUID = 3822057066394806570L;

    public String cDate;
    public long cTime;
    public int hotvalue;
    public int hotwordtype;
    public String iconcolor;
    public String icontext;
    public int id=-1;
    public int isSearch;
    public String tagName;
    public String ksorderid;
    public String uDate;
    public long uTime;
    public int tagId;



    public SearchData() {
    }

    public SearchData(String tagName) {
        this.tagName = tagName;
    }

    public SearchData(String cDate, long cTime, int hotvalue, int hotwordtype, String iconcolor, String icontext, int id, int isSearch, String tagName, String ksorderid, String uDate, long uTime, int tagId) {
        this.cDate = cDate;
        this.cTime = cTime;
        this.hotvalue = hotvalue;
        this.hotwordtype = hotwordtype;
        this.iconcolor = iconcolor;
        this.icontext = icontext;
        this.id = id;
        this.isSearch = isSearch;
        this.tagName = tagName;
        this.ksorderid = ksorderid;
        this.uDate = uDate;
        this.uTime = uTime;
        this.tagId = tagId;
    }
}
