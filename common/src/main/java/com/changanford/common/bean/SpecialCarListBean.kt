package com.changanford.common.bean

/**
 *Author lcw
 *Time on 2023/12/15
 *Purpose
 */
data class SpecialCarListBean(
    val carModelId: String,
    val carModelName: String,
    val carModelPic: String,
    var isCheck: Boolean = false
)