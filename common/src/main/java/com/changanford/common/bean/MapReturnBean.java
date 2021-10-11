package com.changanford.common.bean;

import android.os.Parcel;
import android.os.Parcelable;

import com.baidu.mapapi.search.core.PoiInfo;

public class MapReturnBean implements Parcelable {
    private PoiInfo poiInfo ;
    private String cityName;
    private String shefenName;
    private String quxianName;
    private String cid;
    private String Sid;
    private String qid;

    public MapReturnBean(Parcel in) {
        poiInfo = in.readParcelable(PoiInfo.class.getClassLoader());
        cityName = in.readString();
        shefenName = in.readString();
        quxianName = in.readString();
        cid = in.readString();
        Sid = in.readString();
        qid = in.readString();
    }

    public MapReturnBean() {

    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(poiInfo, flags);
        dest.writeString(cityName);
        dest.writeString(shefenName);
        dest.writeString(quxianName);
        dest.writeString(cid);
        dest.writeString(Sid);
        dest.writeString(qid);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<MapReturnBean> CREATOR = new Creator<MapReturnBean>() {
        @Override
        public MapReturnBean createFromParcel(Parcel in) {
            return new MapReturnBean(in);
        }

        @Override
        public MapReturnBean[] newArray(int size) {
            return new MapReturnBean[size];
        }
    };

    public PoiInfo getPoiInfo() {
        return poiInfo;
    }

    public void setPoiInfo(PoiInfo poiInfo) {
        this.poiInfo = poiInfo;
    }

    public String getCityName() {
        return cityName;
    }

    public void setCityName(String cityName) {
        this.cityName = cityName;
    }

    public String getShefenName() {
        return shefenName;
    }

    public void setShefenName(String shefenName) {
        this.shefenName = shefenName;
    }

    public String getQuxianName() {
        return quxianName;
    }

    public void setQuxianName(String quxianName) {
        this.quxianName = quxianName;
    }

    public String getCid() {
        return cid;
    }

    public void setCid(String cid) {
        this.cid = cid;
    }

    public String getSid() {
        return Sid;
    }

    public void setSid(String sid) {
        Sid = sid;
    }

    public String getQid() {
        return qid;
    }

    public void setQid(String qid) {
        this.qid = qid;
    }
}
