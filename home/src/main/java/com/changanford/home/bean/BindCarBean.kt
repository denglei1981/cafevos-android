package com.changanford.home.bean

data class BindCarBean(
    val carModelCode: String,
    val carModelName: String,
    val materialCode: String,
    val materialName: String,
    val modelUrl: String,
    val plateNum: String,
    val seriesCode: String,
    val seriesName: String,
    val vin: String
){
    fun getVinStr():String{
        return "vin码: ".plus(vin)
    }
}