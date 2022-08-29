package com.changanford.circle.bean

import com.changanford.common.bean.ActBean

/**
 *Author lcw
 *Time on 2022/8/23
 *Purpose
 */
data class CircleActivityBean(
    val dataList: ArrayList<ActBean> = arrayListOf(),
    val extend: Any? = Any(),
    val pageNo: Int = 0,
    val pageSize: Int = 0,
    val total: Int = 0,
    val totalPage: Int = 0
)

data class CircleActivityItemBean(
    val activityAddr: String = "",
    val activityButton: String = "",
    val activityJoinCount: Int = 0,
    val activityTag: String = "",
    val activityTotalCount: Int = 0,
    val beginTime: Long = 0,
    val cityName: String = "",
    val coverImg: String = "",
    val deadLineTime: Long = 0,
    val endTime: Long = 0,
    val hot: String = "",
    val jumpDto: JumpDto = JumpDto(),
    val needSignUp: String = "",
    val official: String = "",
    val openTime: Long = 0,
    val outChain: String = "",
    val provinceName: String = "",
    val reason: Any? = Any(),
    val recommend: String = "",
    val status: String = "",
    val title: String = "",
    val townName: String = "",
    val userId: Int = 0,
    val voteButton: Any? = Any(),
    val wonderfulId: Int = 0,
    val wonderfulType: String = ""
)

data class JumpDto(
    val jumpCode: Int = 0,
    val jumpVal: String = ""
)