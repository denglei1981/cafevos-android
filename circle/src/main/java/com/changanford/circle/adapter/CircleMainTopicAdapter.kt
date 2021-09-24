package com.changanford.circle.adapter

import android.content.Context
import androidx.databinding.ViewDataBinding
import com.changanford.circle.R
import com.changanford.circle.config.CircleConfig
import com.changanford.circle.databinding.ItemCircleMainTopicBinding
import com.changanford.circle.ext.loadImage
import com.changanford.circle.ext.setCircular
import com.changanford.common.basic.adapter.BaseAdapterOneLayout

/**
 *Author lcw
 *Time on 2021/9/23
 *Purpose
 */
class CircleMainTopicAdapter(context: Context) :
    BaseAdapterOneLayout<String>(context, R.layout.item_circle_main_topic) {
    override fun fillData(vdBinding: ViewDataBinding?, item: String, position: Int) {
        val binding = vdBinding as ItemCircleMainTopicBinding
        binding.ivIcon.setCircular(5)
        binding.ivIcon.loadImage(CircleConfig.TestUrl2)
        binding.bean = item
    }
}