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
    val multiple:String,
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
    val weeks: List<Int>?
)