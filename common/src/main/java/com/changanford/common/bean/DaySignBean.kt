package com.changanford.common.bean

/**********************************************************************************
 * @Copyright (C), 2018-2020.
 * @FileName: com.hpb.mvvm.ccc.bean.DaySignBean
 * @Author:　 　
 * @Version : V1.0
 * @Date: 2020/5/27 10:20
 * @Description: 　
 * *********************************************************************************
 */

data class DaySignBean(
    val multiple: String,
    val actionId: String,
    val actionName: String,
    val actionTimes: Int,
    val cumulation: Int,
    val growth: Int,
    val integral: Int,
    val nextGrowth: Int,
    val nextIntegral: Int,
    val ontinuous: Int,
    val week: String,
    val weeks: List<Int>?,
    //新增
    val totalIntegral: Int,//null,
    val totalGrowth: Int,//null,
    val roundList: Int,//null,
    val signRule: Int,//null,
    val reissueIntegral: Int,//null,
    val additionStatus: Int,//2,是否有额外奖励弹窗，1是2否
    val additionRewardType: Int,//null,额外奖励类型，1福币+成长值，2勋章，3，优惠券
    val additionFb: Int,//null,获取福币
    val additionGrowth: Int,//null,获取成长值
    val luckyBlessingBagId: Int,//null,抽奖ID
    val additionMedalName: String,//null,勋章
    val additionMedalImg:String,//勋章图片
    val additionCouponName: String,//null,优惠券
    val additionCouponImg:String,//优惠券图片
    val sevenDays: ArrayList<Sign7DayBean>,//
)

data class Sign7DayBean(
    var signStatus: Int,//状态，1已签到，2可签到，3未签到
    var days: Int,//连续签到天数
    var fb: Int,//签到获得福币
    var luckyBlessingBagId: Int,//抽奖id
)


