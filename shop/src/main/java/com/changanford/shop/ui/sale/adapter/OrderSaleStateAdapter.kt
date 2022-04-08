package com.changanford.shop.ui.sale.adapter

import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseDataBindingHolder
import com.changanford.shop.R
import com.changanford.shop.bean.SaleAfterBean
import com.changanford.shop.databinding.ItemOrderSaleAfterBinding

/**
 *     售后的一些数据
 * */
class OrderSaleStateAdapter() :
    BaseQuickAdapter<SaleAfterBean, BaseDataBindingHolder<ItemOrderSaleAfterBinding>>(R.layout.item_order_sale_after) {
    init {
        addChildClickViewIds(R.id.btn_states)
    }
    override fun convert(
        holder: BaseDataBindingHolder<ItemOrderSaleAfterBinding>,
        item: SaleAfterBean
    ) {
        holder.dataBinding?.apply {
            btnStates.text = item.name
            btnStates.isSelected=        item.isSelected
        }
    }

}

