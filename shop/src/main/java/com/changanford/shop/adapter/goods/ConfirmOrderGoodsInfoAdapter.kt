package com.changanford.shop.adapter.goods

import android.annotation.SuppressLint
import android.view.View
import androidx.core.content.ContextCompat
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseDataBindingHolder
import com.changanford.common.bean.GoodsDetailBean
import com.changanford.common.util.ext.setCircular
import com.changanford.common.utilext.load
import com.changanford.common.wutil.FlowLayoutManager
import com.changanford.shop.R
import com.changanford.shop.databinding.ItemGoodsOrderBinding


class ConfirmOrderGoodsInfoAdapter :
    BaseQuickAdapter<GoodsDetailBean, BaseDataBindingHolder<ItemGoodsOrderBinding>>(R.layout.item_goods_order) {
    @SuppressLint("SetTextI18n")
    override fun convert(
        holder: BaseDataBindingHolder<ItemGoodsOrderBinding>,
        item: GoodsDetailBean
    ) {
        holder.dataBinding?.apply {
            val position = holder.absoluteAdapterPosition
            imgGoodsCover.load(item.skuImg)
            tvNumber.text = "x${item.buyNum}"
            if (item.showSevenTips) {
                tvHint.visibility = View.VISIBLE
            } else {
                tvHint.visibility = View.GONE
            }
            rvGoodsProperty.layoutManager = FlowLayoutManager(context, false, true)
            val adapter = OrderGoodsAttributeAdapter()
            adapter.noStock = item.noStock
            rvGoodsProperty.adapter = adapter
            adapter.setList(item.skuCodeTxts?.filter { "" != it })
            model = item

            val txColor = if (item.noStock) ContextCompat.getColor(
                context,
                R.color.color_99
            ) else ContextCompat.getColor(context, R.color.color_33)
            tvGoodsTitle.setTextColor(txColor)

            if (item.noStock) {
                tvNoStock.visibility = View.VISIBLE
                tvRmbPrice.visibility = View.INVISIBLE
                tvFbPrice.visibility = View.INVISIBLE
            } else {
                tvNoStock.visibility = View.GONE
                tvRmbPrice.visibility = View.VISIBLE
                tvFbPrice.visibility = View.VISIBLE
            }

            executePendingBindings()
            viewDivider.visibility = if (position == data.size - 1) View.INVISIBLE else View.VISIBLE
        }
    }
}