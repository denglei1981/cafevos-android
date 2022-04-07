package com.changanford.shop.ui.sale.adapter
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.module.LoadMoreModule
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.changanford.shop.R
import com.changanford.shop.bean.SaleAfterBean
/**
 *     售后的一些数据
 * */
class OrderSaleStateAdapter() :
    BaseQuickAdapter<SaleAfterBean, BaseViewHolder>(R.layout.in_order_detail_sale_after),
    LoadMoreModule {
    override fun convert(holder: BaseViewHolder, item: SaleAfterBean) {

    }
}

