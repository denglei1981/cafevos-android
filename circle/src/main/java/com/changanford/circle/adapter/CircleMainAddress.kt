package com.changanford.circle.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.ViewGroup
import androidx.databinding.ViewDataBinding
import com.changanford.circle.R
import com.changanford.circle.bean.CircleInfo
import com.changanford.circle.databinding.ItemCircleAddressBinding
import com.changanford.circle.ext.loadImage
import com.changanford.circle.ext.setCircular
import com.changanford.circle.ext.toIntPx
import com.changanford.common.basic.adapter.BaseAdapterOneLayout

/**
 *Author lcw
 *Time on 2021/9/23
 *Purpose
 */
class CircleMainAddress(context: Context) :
    BaseAdapterOneLayout<CircleInfo>(context, R.layout.item_circle_address) {
    @SuppressLint("SetTextI18n")
    override fun fillData(vdBinding: ViewDataBinding?, item: CircleInfo, position: Int) {
        val binding = vdBinding as ItemCircleAddressBinding

        val params = binding.clContent.layoutParams as ViewGroup.MarginLayoutParams
        if (position == 0 || position == 1) {
            params.topMargin =
                14.toIntPx()
        } else params.topMargin = 0
        binding.ivIcon.setCircular(5)
        binding.ivIcon.loadImage(item.pic)
        binding.tvNum.text = "${item.userCount}成员"

        binding.bean = item
    }
}