package com.changanford.common.bean

import android.text.TextUtils

data class BindCarBean(
    val carModelCode: String,
    val carModelName: String,
    val materialCode: String,
    var materialName: String?="",
    val modelUrl: String,
    val plateNum: String,
    val seriesCode: String,
    val seriesName: String,
    val vin: String,
    var confirm:Int=-1,
    var carSalesInfoId:String
){
    fun getVinStr():String{
        return "vin码: ".plus(vin)
    }

}