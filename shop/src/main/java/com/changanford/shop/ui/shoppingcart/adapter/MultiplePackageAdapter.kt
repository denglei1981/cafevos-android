package com.changanford.shop.ui.shoppingcart.adapter

import androidx.lifecycle.LifecycleOwner
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.module.LoadMoreModule
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.changanford.shop.R
import com.changanford.shop.bean.CouponData

/**
 *  多包裹适配器
 * */
class MultiplePackageAdapter() :
    BaseQuickAdapter<CouponData, BaseViewHolder>(R.layout.item_multiple_package),
    LoadMoreModule {
    override fun convert(holder: BaseViewHolder, item: CouponData) {

    }
}

