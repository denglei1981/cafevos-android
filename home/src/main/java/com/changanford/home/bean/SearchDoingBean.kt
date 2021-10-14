package com.changanford.home.bean

/**
 * @Author: hpb
 * @Date: 2020/5/22
 * @Des:
 */
data class SearchDoingBean(
    val activityJoinCount: Int,
    val activityTotalCount: Int,
    val beginTime: Any,
    val cityName: String,
    val coverImg: String,
    val deadLineTime: Long,
    val endTime: Long,
    val serverTime:Long,
    val jumpType: Int,
    val jumpVal: String,
    val official: Int,
    val provinceName: String,
    val reason: Any,
    val status: Int,
    val title: String,
    val townName: Any,
    val userId: Int,
    val wonderfulId: Int,
    val wonderfulType: String
)