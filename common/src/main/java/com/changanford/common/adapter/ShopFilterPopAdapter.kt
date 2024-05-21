package com.changanford.common.adapter

import androidx.core.content.ContextCompat
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseDataBindingHolder
import com.changanford.common.R
import com.changanford.common.bean.ShopFilterPopBean
import com.changanford.common.databinding.ItemPopShopPriceFilterBinding

/**
 * @author: niubobo
 * @date: 2024/5/15
 * @description：
 */
class ShopFilterPopAdapter :
    BaseQuickAdapter<ShopFilterPopBean, BaseDataBindingHolder<ItemPopShopPriceFilterBinding>>(
        R.layout.item_pop_shop_price_filter
    ) {

    var selectPosition = 0

    override fun convert(
        holder: BaseDataBindingHolder<ItemPopShopPriceFilterBinding>,
        item: ShopFilterPopBean
    ) {
        holder.dataBinding?.apply {
            tvContent.text = item.price
            val bg = if (item.isCheck) R.drawable.bg_shape_1700f4_23 else R.drawable.bg_bord_4d4a_23
            tvContent.setBackgroundResource(bg)
            val txColor = if (item.isCheck) ContextCompat.getColor(
                context,
                R.color.white
            ) else ContextCompat.getColor(context, R.color.color_d916)
            tvContent.setTextColor(txColor)
            tvContent.setOnClickListener {
                selectTab(holder.layoutPosition, !item.isCheck)
                selectPosition = holder.layoutPosition
            }
        }
    }

    private fun selectTab(position: Int, isCheck: Boolean) {
        data.forEachIndexed { index, shopFilterPopBean ->
            if (index == position) {
                shopFilterPopBean.isCheck = isCheck
            } else {
                shopFilterPopBean.isCheck = false
            }
        }
        notifyItemRangeChanged(0, itemCount)
    }
}