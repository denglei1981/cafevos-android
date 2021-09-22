package com.changanford.circle.adapter

import android.content.Context
import android.view.View
import android.view.ViewGroup
import androidx.databinding.ViewDataBinding
import com.changanford.circle.R
import com.changanford.circle.config.CircleConfig
import com.changanford.circle.databinding.ItemPersonalBinding
import com.changanford.circle.ext.ImageOptions
import com.changanford.circle.ext.loadImage
import com.changanford.circle.ext.toIntPx
import com.changanford.common.basic.adapter.BaseAdapterOneLayout

/**
 *Author lcw
 *Time on 2021/9/22
 *Purpose
 */
class PersonalAdapter(context: Context) :
    BaseAdapterOneLayout<String>(context, R.layout.item_personal) {
    override fun fillData(vdBinding: ViewDataBinding?, item: String, position: Int) {
        val binding = vdBinding as ItemPersonalBinding

        val params = binding.clItem.layoutParams as ViewGroup.MarginLayoutParams
        if (position == 0) {
            params.topMargin =
                27.toIntPx()
        } else params.topMargin = 0

        binding.ivIcon.loadImage(CircleConfig.TestUrl, ImageOptions().apply { circleCrop = true })
        if (position == 0) {
            binding.tvOwner.visibility = View.VISIBLE
        } else {
            binding.tvOwner.visibility = View.GONE
        }
        if (position == 4) {
            binding.tvOut.visibility = View.VISIBLE
        } else {
            binding.tvOut.visibility = View.GONE
        }
        binding.bean = item
    }
}