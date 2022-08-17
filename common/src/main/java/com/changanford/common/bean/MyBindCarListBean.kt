package com.changanford.common.bean

/**
 *Author lcw
 *Time on 2022/8/16
 *Purpose
 */
data class MyBindCarListBean(
    val authId: Int = 0,
    val carSalesInfoId: Int = 0,
    val isDefault: Int = 0,
    val isHaveMaintainManual: Int = 0,
    val isHaveOwnerManual: Int = 0,
    val maintainManualId: Any? = Any(),
    val maintainManualVal: Any? = Any(),
    val modelCode: String = "",
    val modelName: String = "",
    val modelUrl: String = "",
    val ownerManualPath: Any? = Any(),
    val ownerName: String = "",
    val plateNum: String = "",
    val saleDate: Long = 0,
    val seriesCode: String = "",
    val seriesName: String = "",
    val vin: String = ""
)