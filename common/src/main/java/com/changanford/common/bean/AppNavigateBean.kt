package com.changanford.common.bean

import android.graphics.Bitmap

/**
 *Author lcw
 *Time on 2024/1/16
 *Purpose
 */
data class AppNavigateBean(
    val createTime: Long,
    val dateState: Int,
    val iconFirst: String,
    val iconFive: String,
    val iconFourth: String,
    val iconSecond: String,
    val iconThird: String,
    val id: Int,
    val jsonFirst: String,
    val jsonFive: String,
    val jsonFourth: String,
    val jsonSecond: String,
    val jsonThird: String,
    val navigateName: String,
    val navigateStatus: Int,
    val `operator`: String,
    val updateTime: Long,
    var btOne:Bitmap,
    var btTwo:Bitmap,
    var btThree:Bitmap,
    var btFour:Bitmap,
    var btFive:Bitmap,
)