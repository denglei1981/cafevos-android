package com.changanford.home.bean

/**
 * @Author : wenke
 * @Time : 2022/2/9 0009
 * @Description : FBBean
 */
data class FBBean(
    val content: String? = null,
    val integral: String? = null,
    val isPop: Int? = null,

    val title: String? = null
)

// 优惠券
data class CouponItem(
    val couponId: Int,
    val couponName: String,
    val couponSendId: Int,
    val createTime: String,
    val dataState: String,
    val deadLine: String,
    val img: String,
    val memo: String,
    val `operator`: String,
    val phone: String,
    val popup: String,
    val sendTime: String,
    val state: String,
    val type: String,
    val updateTime: String,
    val userId: Int,
    val validityBeginTime: String,
    val validityDays: Int,
    val validityEndTime: String,
    val validityType: String
)