package com.changanford.shop.bean

data class OrderFormState(
    val content:String?=null,
    val contentLength:Int=0,
    val isDataValid: Boolean = false,
    val rating:Int=0
)
/**
 * 发布评价
* */
data class PostEvaluationBean(
    var anonymous: String?=null,//是否匿名
    var evalScore: Int? = null,//评分(0-5)
    var evalText: String? = null,//评价内容
    var imgUrls: List<String>? = null,
    var mallMallOrderSkuId:String?= null,//订单skuId
)
data class RefundBean(var orderNo:String,var payFb:String?,var payRmb:String?,var refundType:String){

}