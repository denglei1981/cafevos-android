package com.changanford.home.bean

/**
 *Author lcw
 *Time on 2023/12/15
 *Purpose
 */
data class SpecialCarListBean(
    val carModelId: Int,
    val carModelName: String,
    var isCheck: Boolean = false
)