package com.changanford.shop.adapter.order

import android.annotation.SuppressLint
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseDataBindingHolder
import com.changanford.common.bean.OrderTypeItem
import com.changanford.shop.R
import com.changanford.shop.databinding.ItemOrderTypeBinding


class OrderTypeAdapter: BaseQuickAdapter<OrderTypeItem, BaseDataBindingHolder<ItemOrderTypeBinding>>(R.layout.item_order_type){
    @SuppressLint("SetTextI18n")
    override fun convert(holder: BaseDataBindingHolder<ItemOrderTypeBinding>, item: OrderTypeItem) {
        val dataBinding=holder.dataBinding
        if(dataBinding!=null){
            dataBinding.model=item
            dataBinding.executePendingBindings()
        }
    }
}