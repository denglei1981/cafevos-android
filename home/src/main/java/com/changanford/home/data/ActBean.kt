package com.changanford.home.data

import android.text.TextUtils
import com.changanford.common.util.TimeUtils

class ActBean {
    val townName: String? = null
    val beginTime: Long = 0
    val deadLineTime: Long = 0
    val endTime: Long = 0
    val official: Int? = null
    val title: String? = null
    val userId = 0
    val wonderfulId = 0
    var wonderfulType: Int = 0
    val activityJoinCount: String? = null //参加活动人数
    val coverImg: String? = null
    val jumpType = 0
    val jumpVal: String? = null
    val serverTime: Long = 0
    val cityName: String = ""
    val provinceName: String = ""
    val recommend: String? = null
    val hot: String? = null
    fun getAddress(): String {
        if (!TextUtils.isEmpty(cityName)) {
            return cityName
        }
        if(TextUtils.isEmpty(provinceName)){
            return "未知"
        }
        return provinceName
    }

    fun getActTimeS():String{
       return "活动时间: ".plus(TimeUtils.formateActTime(beginTime)).plus(" 至 ").plus(TimeUtils.formateActTime(endTime))
    }

    fun getSignTimes():String{
        return "报名截止时间: ".plus(TimeUtils.formateActTime(deadLineTime))
    }

    fun getEndTimeTips():String{
        return "截止时间: ".plus(TimeUtils.formateActTime(deadLineTime))
    }
}

class FastBeanData(var ads:List<AdBean>,var showType:String=""){

}