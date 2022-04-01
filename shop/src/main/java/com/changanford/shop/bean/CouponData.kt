package com.changanford.shop.bean

/**
 *  优惠券 数据
 * */
data class CouponData(
    val conditionMoney: Int,
    val conditionName: String,
    val couponConditionId: Int,
    val couponId: Int,
    val couponMarkId: Int,
    val couponMoney: Int,
    val couponName: String,
    val couponRatio: Int,
    val couponRecordId: Int,
    val desc: String,
    val discountType: String,
    val img: String,
    val markImg: String,
    val markName: String,
    val state: String,
    val type: String,
    val useLimit: String,
    val useLimitValue: String,
    val userId: Int,
    val validityBeginTime: String,
    val validityEndTime: String
)