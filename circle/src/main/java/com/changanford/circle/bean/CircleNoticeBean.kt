package com.changanford.circle.bean

import com.changanford.common.bean.AuthorBaseVo

/**
 *Author lcw
 *Time on 2022/8/22
 *Purpose
 */
data class CircleNoticeBean(
    val dataList: ArrayList<CircleNoticeItem> = arrayListOf(),
    val extend: Extend = Extend(),
    val pageNo: Int = 0,
    val pageSize: Int = 0,
    val total: Int = 0,
    val totalPage: Int = 0
)

data class CircleNoticeItem(
    val authorBaseVo: AuthorBaseVo,
    val checkNoReason: Any? = Any(),
    val checkStatus: String = "",
    val circleId: Int = 0,
    val createTime: Long = 0,
    val dataState: String = "",
    val demo: Any? = Any(),
    val detailHtml: String = "",
    val machineCheckResult: String = "",
    val noticeId: Int = 0,
    val noticeName: String = "",
    val noticeTime: Long = 0,
    val noticeTimeStr: String = "",
    val onShelve: String = "",
    val `operator`: Any? = Any(),
    val top: String = "",
    val updateTime: Long = 0,
    val userId: Int = 0
)

