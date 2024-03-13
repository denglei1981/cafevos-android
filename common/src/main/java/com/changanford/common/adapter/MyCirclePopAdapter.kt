package com.changanford.common.adapter

import androidx.core.content.ContextCompat
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseDataBindingHolder
import com.changanford.common.R
import com.changanford.common.databinding.ItemPopMyCircleBinding
import com.changanford.common.util.ext.setCircular
import com.changanford.common.utilext.load

/**
 * @author: niubobo
 * @date: 2024/3/13
 * @description：
 */
class MyCirclePopAdapter :
    BaseQuickAdapter<String, BaseDataBindingHolder<ItemPopMyCircleBinding>>(R.layout.item_pop_my_circle) {

    var selectPosition = -1

    override fun convert(holder: BaseDataBindingHolder<ItemPopMyCircleBinding>, item: String) {
        holder.dataBinding?.apply {
            ivCover.setCircular(4)
            ivCover.load(item)
            tvContent.text = "探险者 EXPEXP探险者 EXPEXP"
            if (selectPosition == holder.layoutPosition) {
                tvContent.setTextColor(ContextCompat.getColor(context, R.color.circle_1700F4))
            } else {
                tvContent.setTextColor(ContextCompat.getColor(context, R.color.color_d916))
            }
        }
    }
}