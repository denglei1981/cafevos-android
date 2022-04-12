package com.changanford.shop.adapter.order

import android.annotation.SuppressLint
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseDataBindingHolder
import com.changanford.common.bean.OrderItemBean
import com.changanford.shop.R
import com.changanford.shop.databinding.ItemOrdersGoodsBinding


class OrderRefundAdapter(): BaseQuickAdapter<OrderItemBean, BaseDataBindingHolder<ItemOrdersGoodsBinding>>(R.layout.item_orders_goods){
    @SuppressLint("SetTextI18n")
    override fun convert(holder: BaseDataBindingHolder<ItemOrdersGoodsBinding>, itemData: OrderItemBean) {
        holder.dataBinding?.apply{
            val position=holder.absoluteAdapterPosition

        }
    }

}