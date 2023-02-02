package com.changanford.circle.adapter

import android.content.Context
import android.view.View
import android.view.ViewGroup
import androidx.databinding.ViewDataBinding
import com.changanford.circle.R
import com.changanford.circle.config.CircleConfig
import com.changanford.circle.databinding.ItemCircleDetailsBottomBinding
import com.changanford.circle.ext.ImageOptions
import com.changanford.circle.ext.loadImage

import com.changanford.common.basic.adapter.BaseAdapterOneLayout
import com.changanford.common.util.ext.setCircular
import com.changanford.common.utilext.toIntPx

/**
 *Author lcw
 *Time on 2021/9/22
 *Purpose
 */
class CircleDetailsBarAdapter(context: Context) :
    BaseAdapterOneLayout<String>(context, R.layout.item_circle_details_bottom) {
    override fun fillData(vdBinding: ViewDataBinding?, item: String, position: Int) {
        val binding = vdBinding as ItemCircleDetailsBottomBinding
        binding.ivBg.setCircular(10)

        val params = binding.clContent.layoutParams as ViewGroup.MarginLayoutParams
        if (position == 0 || position == 1) {
            params.topMargin =
                10.toIntPx()
        } else params.topMargin = 0

        if (position == 4) {
            binding.ivPlay.visibility = View.VISIBLE
        } else {
            binding.ivPlay.visibility = View.GONE
        }

        if (position == 2) {
            binding.ivVery.visibility = View.VISIBLE
        } else {
            binding.ivVery.visibility = View.GONE
        }

        binding.ivHead.loadImage(CircleConfig.TestUrl, ImageOptions().apply { circleCrop = true })
        binding.bean = item
    }
}