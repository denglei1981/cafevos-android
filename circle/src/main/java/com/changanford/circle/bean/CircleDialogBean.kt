package com.changanford.circle.bean

/**
 * @Author: lcw
 * @Date: 2020/11/23
 * @Des:
 */
data class CircleDialogBeanItem(
    val circleStarRoleId: String,
    val createTime: String,
    val memo: String,
    val operatorName: String,
    val orderNum: String,
    val starAuthority: String,
    val starImgUrl: String,
    val starName: String,
    val starNum: String,
    val status: String,
    val surplusNum: String,
    val updateTime: String,
    var isCheck: Boolean
)