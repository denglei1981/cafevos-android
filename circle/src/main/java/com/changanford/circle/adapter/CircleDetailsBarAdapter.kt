package com.changanford.circle.adapter

import android.content.Context
import android.view.View
import androidx.databinding.ViewDataBinding
import com.changanford.circle.R
import com.changanford.circle.config.CircleConfig
import com.changanford.circle.databinding.ItemCircleDetailsBottomBinding
import com.changanford.circle.ext.ImageOptions
import com.changanford.circle.ext.loadImage
import com.changanford.circle.ext.setCircular
import com.changanford.common.basic.adapter.BaseAdapterOneLayout

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