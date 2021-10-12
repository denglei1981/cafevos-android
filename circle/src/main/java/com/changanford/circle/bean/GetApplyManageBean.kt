package com.changanford.circle.bean

data class GetApplyManageBean(
    var applyReason: String = "",
    val cardImg: String = "",
    val cardNum: String = "",
    val circleId: String = "",
    val circleStarRoleId: String = "",
    val createTime: Long = 0,
    val id: Int = 0,
    val memo: String = "",
    val name: String = "",
    val operatorName: String = "",
    val reason: String = "",
    val status: Int = 0,
    val updateTime: Long = 0,
    val userId: Int = 0
)