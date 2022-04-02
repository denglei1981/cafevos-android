package com.changanford.shop.ui.order.adapter

import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.module.LoadMoreModule
import com.chad.library.adapter.base.viewholder.BaseDataBindingHolder
import com.changanford.common.bean.GoodsDetailBean
import com.changanford.common.bean.OrderItemBean
import com.changanford.common.utilext.GlideUtils
import com.changanford.shop.R
import com.changanford.shop.databinding.InItemOrderGoodsBinding
import com.changanford.shop.databinding.InItemOrderGoodsV2Binding
import com.changanford.shop.databinding.ItemInOrderDetailGoodsinfoV2Binding
import com.changanford.shop.databinding.ItemShoppingCartBinding

/**
 *
 * */
class OrderDetailsItemV2Adapter() :
    BaseQuickAdapter<OrderItemBean, BaseDataBindingHolder<InItemOrderGoodsV2Binding>>(R.layout.in_item_order_goods_v2) {

    override fun convert(
        holder: BaseDataBindingHolder<InItemOrderGoodsV2Binding>,
        item: OrderItemBean
    ) {
        holder.dataBinding?.apply {
            model = item
            GlideUtils.loadBD(item.skuImg,imgGoodsCover)
        }
    }

    fun getShoppingList() {

    }

    interface ShopBackListener {

        fun check()
    }
}

