package com.changanford.common.bean

/**
 *Author lcw
 *Time on 2023/2/17
 *Purpose
 */

data class BizCodeBean(
    val ids: String?,
    val content:String?,
    val windowMsg: WindowMsg?
)

data class WindowMsg(
    val code: String,
    val msgTitle: String,
    val overMsg: String?=null
)
