package com.changanford.shop.adapter.order

import android.annotation.SuppressLint
import android.view.View
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseDataBindingHolder
import com.changanford.common.bean.OrderRefundItemBean
import com.changanford.common.wutil.ScreenUtils
import com.changanford.shop.R
import com.changanford.shop.control.OrderControl
import com.changanford.shop.databinding.ItemOrdersGoodsBinding


class OrderRefundAdapter : BaseQuickAdapter<OrderRefundItemBean, BaseDataBindingHolder<ItemOrdersGoodsBinding>>(R.layout.item_orders_goods){
    private val dp4 by lazy { ScreenUtils.dp2px(context,4f) }
    private val control by lazy { OrderControl(context,null) }
    @SuppressLint("SetTextI18n")
    override fun convert(holder: BaseDataBindingHolder<ItemOrdersGoodsBinding>, item: OrderRefundItemBean) {
        holder.dataBinding?.apply{
            tvOrderNo.text="退款编号：${item.orderNo}"
            ScreenUtils.setMargin(tvOrderNo,l=dp4)
            tvTag.visibility= View.VISIBLE
            tvOrderStates.text=item.getRefundStatusTxt()
            control.bindingGoodsInfo(inGoodsInfo,item)
        }
    }

}