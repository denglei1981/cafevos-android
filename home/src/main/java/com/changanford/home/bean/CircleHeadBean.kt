package com.changanford.home.bean

class CircleHeadBean {
    /**
     * adId : 1
     * adImg : https://img2.autoimg.cn/admdfs/g24/M09/1B/10/ChwFjl6gE8iAOsocAAGQ4UWSmLg171.jpg
     * adName : 汽车之家春季车站
     * isVideo : 0
     * jumpDataType : 1
     * jumpDataValue : https://401cbc.autohome.com.cn/index/index.html?pvareaid=6840833
     * posId : 1
     * status : 1
     */
    var adId = 0
    var adImg: String? = null
    var adName: String? = null
    var isVideo = 0
    var jumpDataType = 0
    var jumpDataValue: String? = null
    var posId = 0
    var status = 0
    var maPlanId:String? = ""
    var maJourneyActCtrlId:String? = ""
    var maJourneyId:String? = ""
    var isIscanclick = true
        private set

    fun setIscanclick(iscanclick: Boolean) {
        isIscanclick = iscanclick
    }
}