package com.changanford.shop.bean

data class SaleAfterBean(val id:Int,val name:String,val isSelected:Boolean) {
}
data class  InvoiceInfo(
    var addressInfo:String="",
    var addressId: String,
    val mallMallOrderId:String,
    val mallMallOrderNo:String,
    val invoiceRmb:String,
    var  userName:String,
    var phone :String,
    var userInfo:String="",
    )

/**
 *  发票详情
 * */
data class InvoiceDetails(
    val addressId: Int,
    val applyTime: Long,
    val createBy: Any,
    val createTime: Long,
    val dataState: String,
    val invoiceHeader: String,
    val invoiceHeaderName: String,
    val invoiceRmb: Int,
    val invoiceStatus: String,
    val invoiceTime: Long?=null,
    val invoiceType: Int,
    val logisticsId: Any,
    val logisticsMemo: Any,
    val logisticsNum: Any,
    val mallMallInvoiceId: Int,
    val mallMallOrderId: Int,
    val mallMallOrderNo: String,
    val memo: String,
    val remark: Any,
    val searchValue: Any,
    val taxpayerIdentifier: Any,
    val updateBy: Any,
    val updateTime: Long
)