package com.changanford.common.bean

data class BindCarBean(
    val carModelCode: String,
    val carModelName: String,
    val materialCode: String,
    var materialName: String? = "",
    val modelUrl: String,
    val plateNum: String,
    val seriesCode: String,
    val seriesName: String,
    val vin: String,
    var confirm: Int = -2,
    var carSalesInfoId: String
) {
    fun getVinStr(): String {
        return "vin码: ".plus(vin)
    }

}