package com.changanford.circle.adapter

import android.content.Context
import android.view.ViewGroup
import androidx.databinding.ViewDataBinding
import com.changanford.circle.R
import com.changanford.circle.config.CircleConfig
import com.changanford.circle.databinding.ItemCircleDetailsPersonalBinding
import com.changanford.circle.ext.ImageOptions
import com.changanford.circle.ext.loadImage
import com.changanford.circle.ext.toIntPx
import com.changanford.circle.utils.MUtils
import com.changanford.common.basic.adapter.BaseAdapterOneLayout

/**
 *Author lcw
 *Time on 2021/9/22
 *Purpose
 */
class CircleDetailsPersonalAdapter(context: Context) :
    BaseAdapterOneLayout<String>(context, R.layout.item_circle_details_personal) {
    override fun fillData(vdBinding: ViewDataBinding?, item: String, position: Int) {
        val binding = vdBinding as ItemCircleDetailsPersonalBinding
        val params = binding.ivPersonal.layoutParams as ViewGroup.MarginLayoutParams
        if (position == 0) {
            params.leftMargin =
                0
        } else params.leftMargin = -(6.toIntPx())

        binding.ivPersonal.loadImage(
            CircleConfig.TestUrl,
            ImageOptions().apply { circleCrop = true })
    }
}