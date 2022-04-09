package com.changanford.shop.ui.sale.adapter

import androidx.lifecycle.LifecycleOwner
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.module.LoadMoreModule
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.changanford.shop.R
import com.changanford.shop.bean.CouponData

/**
 *    退款进度线。
 * */
class RefundProgressAdapter() :
    BaseQuickAdapter<CouponData, BaseViewHolder>(R.layout.item_refund_line_progress),
    LoadMoreModule {
    override fun convert(holder: BaseViewHolder, item: CouponData) {

    }
}

