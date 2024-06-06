package com.changanford.circle.bean

/**
 * @author: niubobo
 * @date: 2024/6/6
 * @description：
 */
data class MyJoinCircleBean(
    val circleId: String?,
    val circleLord: Boolean?,
    val isGrounding: Int?,
    val joinTime: String?,
    val name: String?,
    val pic: String?,
    var isCheck: Boolean = false,
    var canCheck:Boolean
) {
}