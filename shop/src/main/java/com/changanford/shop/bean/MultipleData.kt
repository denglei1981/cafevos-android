package com.changanford.shop.bean

data class PackageInfo(
    val code: String,
    val context: String,
    val id: String,
    val logisticsId: String,
    val logisticsLogo: String,
    val logisticsName: String,
    val logisticsPhone: String,
    val status: String,
    val userId: String
)
data class PackageSkuBean(
    val id: String,
    val num: Long,
    val orderCode: String,
    val orderId: String,
    val pakageId: String,
    val skuCode: String,
    val skuId: String,
    val skuImg: String,
    val snapshotOfAttrOption: String,
    val spuId: String,
    val spuName: String
)

data class  LogisticsItems(var pakage: PackageInfo,var pakageOrderSkus:MutableList<PackageSkuBean>)
data class  PackMainData(var logisticsItems:MutableList<LogisticsItems>,var noSendSkuDTOs:MutableList<NoSendSkuData>?= mutableListOf())

data class NoSendSkuData(
    val busSourse: String,
    val busSourseDetailId: String,
    val busSourseId: String,
    val buyNum: Int,
    val createTime: Long,
    val dataState: String,
    val discountScale: String,
    val fbOfUnitPrice: Int,
    val mallMallDiscountScaleId: String,
    val mallMallOrderId: Int,
    val mallMallOrderSkuId: Int,
    val mallMallSkuId: Int,
    val mallMallSpuId: Int,
    val memo: String,
    val modelName: String,
    val orderNo: String,
    val refundStauts: String,
    val registSuccessTime: String,
    val sendNum: Any,
    val sharedFb: Int,
    val sharedRmb: Int,
    val skuCode: String,
    val skuImg: String,
    val snapshotOfAttrOption: String,
    val specifications: String,
    val spuName: String,
    val sysl: Int,
    val updateTime: Long,
    val userId: Int,
    val vin: String
)