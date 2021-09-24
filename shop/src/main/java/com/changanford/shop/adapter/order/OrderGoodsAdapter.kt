package com.changanford.shop.adapter.order

import android.annotation.SuppressLint
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.module.LoadMoreModule
import com.chad.library.adapter.base.viewholder.BaseDataBindingHolder
import com.changanford.shop.R
import com.changanford.shop.bean.OrderBean
import com.changanford.shop.databinding.ItemOrdersGoodsBinding


class OrderGoodsAdapter: BaseQuickAdapter<OrderBean, BaseDataBindingHolder<ItemOrdersGoodsBinding>>(R.layout.item_orders_goods), LoadMoreModule {
    @SuppressLint("SetTextI18n")
    override fun convert(holder: BaseDataBindingHolder<ItemOrdersGoodsBinding>, item: OrderBean) {
        val dataBinding=holder.dataBinding
        if(dataBinding!=null){
            dataBinding.model=item
            dataBinding.executePendingBindings()
        }
    }
}