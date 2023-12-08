package com.changanford.common.bean

/**
 *Author lcw
 *Time on 2023/12/8
 *Purpose
 */
data class JoinCircleCheckBean(
    val canJoin: Boolean,
    val alertMes: String? = null
)
