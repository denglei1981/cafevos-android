package com.changanford.common.bean

/**
 *Author lcw
 *Time on 2022/6/30
 *Purpose
 */
data class WaitReceiveBean(
    val createTime: Long = 0,
    val id: Int = 0,
    val integral: Int = 0,
    val modelCode: String = "",
    val receiveStatus: Int = 0,
    val receiveTime: Any? = Any(),
    val seriesCode: String = "",
    val seriesName: String = "",
    val userId: Int = 0,
    val vin: String = ""
)