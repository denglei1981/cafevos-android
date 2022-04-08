package com.changanford.shop.bean

/**
 * Data validation state of the login form.
 */
data class OrderFormState(
    val content:String?=null,
    val contentLength:Int=0,
    val isDataValid: Boolean = false,
    val rating:Int=0
)

data class RefundBean(var orderNo:String,var payFb:String?,var payRmb:String?,var refundType:String){

}