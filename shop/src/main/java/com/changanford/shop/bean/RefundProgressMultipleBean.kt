package com.changanford.shop.bean

import com.changanford.common.bean.RefundOrderItemBean

/**
 *Author lcw
 *Time on 2023/5/12
 *Purpose
 */
data class RefundProgressMultipleBean(
    val busSource: String,
    val fbRefund: String?,
    val fbRefundApply: String?,
    val mallMallRefundId: String,
    val mallRefundType: String,
    val applyTime: Long,
    val orderNo: String,
    val refundDescImgs: List<String>,
    val refundDescText: String? = null,
    val refundDetailStatus: String,
    var refundList: MutableList<RefundStautsBean>? = mutableListOf(),
    val refundLogMap: RefundOutStautsBean,
    val refundMethod: String,
    val refundNo: String,
    val refundReason: String,
    var refundStatus: String,
    val refundTimes: Int,
    val rmbRefund: String?,
    val rmbRefundApply: String?,
    var isExpand: Boolean,
    val sku: RefundOrderItemBean? = null
)