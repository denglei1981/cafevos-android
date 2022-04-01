package com.changanford.shop.ui.coupon.adapter

import androidx.lifecycle.LifecycleOwner
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.module.LoadMoreModule
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.changanford.shop.R
import com.changanford.shop.bean.CouponData

class CouponCanUseAdapter() :
    BaseQuickAdapter<CouponData, BaseViewHolder>(R.layout.item_can_use_coupon),
    LoadMoreModule {
    override fun convert(holder: BaseViewHolder, item: CouponData) {

    }
}