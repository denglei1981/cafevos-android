package com.changanford.circle.adapter

import android.annotation.SuppressLint
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.changanford.circle.R
import com.changanford.circle.bean.CircleInfo
import com.changanford.circle.databinding.ItemCircleAddressBinding
import com.changanford.circle.ext.loadImage
import com.changanford.common.util.ext.setCircular

import com.changanford.common.utilext.toIntPx

/**
 *Author lcw
 *Time on 2021/9/23
 *Purpose
 */
class CircleMainAddress :
    BaseQuickAdapter<CircleInfo,BaseViewHolder>( R.layout.item_circle_address) {

    @SuppressLint("SetTextI18n")
    override fun convert(holder: BaseViewHolder, item: CircleInfo) {
        val binding =DataBindingUtil.bind<ItemCircleAddressBinding>(holder.itemView)
        binding?.let {
            val params = binding.clContent.layoutParams as ViewGroup.MarginLayoutParams
            if (holder.layoutPosition == 0 || holder.layoutPosition == 1) {
                params.topMargin =
                    14.toIntPx()
            } else params.topMargin = 0
            binding.ivIcon.setCircular(5)
            binding.ivIcon.loadImage(item.pic)
            binding.tvNum.text = "${item.userCount}成员"

            binding.bean = item
        }
    }
}