package com.changanford.common.bean

/**
 * 月签到详情
 */
data class MonthSignBean(
    var multiple: String,//获得加速倍数
    var reissueIntegral: String,//补签需要消耗的积分
    var ontinuous: String,//连续签到天数
    var cumulation: String,//累计签到天数
    var totalIntegral: String,//签到获得总积分
    var totalGrowth: String,//签到获得总成长值
    var signRule: String, //签到规则
    var nextIntegral: String = "0",//明天签到获得积分
    var nextGrowth: String = "0",//明天签到获得成长值
    var roundList: ArrayList<RoundBean>
)

data class RoundBean(
    var date: String,//时间 格式yyyy-MM-dd
    var isSignIn: Int,//是否签到 1是0否
    var integral: Int,//签到可获得积分
    var isNowDay: Boolean = false //是否 是今天
)