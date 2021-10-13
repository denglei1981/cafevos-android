package com.changanford.common.bean

data class MiddlePageBean(var carModelMoreJump:JumpDataBean,var carModels: ArrayList<CarModels>, var carInfos: ArrayList<CarInfos>)
data class CarModels(
    val spuId: String,
    var spuCode: String,//车型编码
    var spuName: String,//车型编码
    var carModelPic: String//车型名称
)

data class CarInfos(
    var url: String
)