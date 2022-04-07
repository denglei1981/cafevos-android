package com.changanford.shop.bean

data class SaleAfterBean(val id:Int,val name:String,val isSelected:Boolean) {
}
data class  InvoiceInfo(
    val addressInfo:String,
    val addressId: String,
    val mallMallOrderId:String,
    val mallMallOrderNo:String,
    val invoiceRmb:String,
    val userName:String,
    val phone :String
    )