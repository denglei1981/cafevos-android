package com.changanford.shop.bean

import com.changanford.common.bean.BindMobileJumpData
import com.changanford.common.bean.RefundOrderItemBean

data class SaleAfterBean(val id: Int, val name: String, val isSelected: Boolean) {
}

data class InvoiceInfo(
    var addressInfo: String = "",
    var addressId: String= "",
    val mallMallOrderId: String= "",
    val mallMallOrderNo: String= "",
    val invoiceRmb: String?= "",
    var userName: String= "",
    var phone: String= "",
    var userInfo: String = "",
)

/**
 *  发票详情
 * */
data class InvoiceDetails(
    var addressId: String="",
    var applyTime: String?="",
    var createBy: String?=null,
    var createTime: String?=null,
    var dataState: String?=null,
    var invoiceHeader: String,
    var invoiceHeaderName: String,
    var invoiceRmb: String?="",
    var invoiceStatus: String,
    var invoiceTime: Long? = null,
    var invoiceType: Int,
    var logisticsId: String?="",
    var logisticsMemo: String?="",
    var logisticsNum: String?="",
    var mallMallInvoiceId: String?="",
    var mallMallOrderId: String?="",
    var mallMallOrderNo: String?="",
    var memo: String?="",
    var remark: String?="",
    var searchValue: String?="",
    var taxpayerIdentifier: String,
    var updateBy: String?="",
    var updateTime: String?="",
    var jumpDataType: String?="",
    var jumpDataValue:String?=""
)


data class RefundProgressBean(
    val fbRefund: String?=null,
    val fbRefundApply: String,
    val mallMallRefundId: String,
    val refundDetailStatus: String,
    val refundMethod: String,
    val refundNo: String,
    val refundReason: String,
    val refundStatus: String,
    val rmbRefund: String?=null,
    val rmbRefundApply: String,
    var sku: RefundOrderItemBean?=null,
    val refundLogMap: RefundOutStautsBean,
    var refundList: MutableList<RefundStautsBean>? = mutableListOf(),
    var refundDescText:String,
    val refundDescImgs:MutableList<String>?= mutableListOf()
)

data class RefundOutStautsBean(
    val ON_GOING: MutableList<RefundStautsBean>? = mutableListOf(),
    val SUCESS: MutableList<RefundStautsBean>? = mutableListOf(),
    val CLOSED: MutableList<RefundStautsBean>? = mutableListOf(),
    val FINISH: MutableList<RefundStautsBean>? = mutableListOf()
)

/**
 *  退款的值
 * */
data class RefundStautsBean(
    val operationDesc: String,
    val operationTime: Long,
    val refundDetailStatus: String,
    val refundStauts: String
)