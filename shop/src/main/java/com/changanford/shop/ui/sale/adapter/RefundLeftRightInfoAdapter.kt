package com.changanford.shop.ui.sale.adapter

import androidx.lifecycle.LifecycleOwner
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.module.LoadMoreModule
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.changanford.shop.R
import com.changanford.shop.bean.CouponData

/**
 *    自己构建一下
 * */
class RefundLeftRightInfoAdapter() :
    BaseQuickAdapter<CouponData, BaseViewHolder>(R.layout.item_gray_layout_left_right_text),
    LoadMoreModule {
    override fun convert(holder: BaseViewHolder, item: CouponData) {

    }
}

