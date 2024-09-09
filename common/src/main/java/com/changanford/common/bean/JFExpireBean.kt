package com.changanford.common.bean

/**
 * @author: niubobo
 * @date: 2024/9/3
 * @description：
 */
data class JFExpireBean(
    val detailList: ArrayList<JFExpireItemBean>?,
    val pageNo: Int?,
    val pageSize: Int?,
    val showDaysNum: String,
    val total: Int?,
    val totalPage: Int,
    val totalScore: Int
)

data class JFExpireItemBean(
    val activity: Int?,
    val availableAmount: Int?,
    val budgetSource: String?,
    val eventCod: Any?,
    val expireTime: Long?,
    val gmtCreate: Long?,
    val id: String?,
    val memo: String?,
    val stype: String?,
    val totalAmount: Int?
)