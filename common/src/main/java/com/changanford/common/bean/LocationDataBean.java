package com.changanford.common.bean;

public class LocationDataBean {

    /**
     * provinceCode : 0
     * cityCode : 0
     * cityName :
     */

    private String provinceCode;
    private String cityCode;
    private String cityName;

    public String getProvinceCode() {
        return provinceCode;
    }

    public void setProvinceCode(String provinceCode) {
        this.provinceCode = provinceCode;
    }

    public String getCityCode() {
        return cityCode;
    }

    public void setCityCode(String cityCode) {
        this.cityCode = cityCode;
    }

    public String getCityName() {
        return cityName;
    }

    public void setCityName(String cityName) {
        this.cityName = cityName;
    }
}
