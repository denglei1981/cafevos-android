package com.changanford.circle.bean

/**
 * @Author: lcw
 * @Date: 2021/9/29
 * @Des: type：类型 1 资讯 2 帖子 3 活动
 */
data class ReportDislikeBody(val type: Int, val linkId: String)