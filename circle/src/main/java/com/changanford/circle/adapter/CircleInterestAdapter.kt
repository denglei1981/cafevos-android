package com.changanford.circle.adapter

import android.content.Context
import android.view.ViewGroup
import androidx.databinding.ViewDataBinding
import com.changanford.circle.R
import com.changanford.circle.config.CircleConfig
import com.changanford.circle.databinding.ItemCircleInterestBinding
import com.changanford.circle.ext.loadImage

import com.changanford.common.basic.adapter.BaseAdapterOneLayout
import com.changanford.common.util.ext.setCircular
import com.changanford.common.utilext.toIntPx

class CircleInterestAdapter(context: Context) :
    BaseAdapterOneLayout<String>(context, R.layout.item_circle_interest) {
    override fun fillData(vdBinding: ViewDataBinding?, item: String, position: Int) {
        val binding = vdBinding as ItemCircleInterestBinding

        val params = binding.clContent.layoutParams as ViewGroup.MarginLayoutParams
        if (position == 0 || position == 1||position==2) {
            params.topMargin =
                5.toIntPx()
        } else params.topMargin = 0

        binding.spView.setCircular(5)
        binding.spView.loadImage(CircleConfig.TestUrl)
        binding.bean = item
    }
}