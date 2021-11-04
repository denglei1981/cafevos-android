package com.changanford.circle.adapter

import android.annotation.SuppressLint
import android.content.Context
import androidx.databinding.ViewDataBinding
import com.changanford.circle.R
import com.changanford.circle.bean.Topic
import com.changanford.circle.config.CircleConfig
import com.changanford.circle.databinding.ItemCircleMainTopicBinding
import com.changanford.circle.ext.ImageOptions
import com.changanford.circle.ext.loadImage
import com.changanford.circle.ext.setCircular
import com.changanford.common.basic.adapter.BaseAdapterOneLayout
import com.xiaomi.push.er

/**
 *Author lcw
 *Time on 2021/9/23
 *Purpose
 */
class CircleMainTopicAdapter(context: Context) :
    BaseAdapterOneLayout<Topic>(context, R.layout.item_circle_main_topic) {

    @SuppressLint("SetTextI18n")
    override fun fillData(vdBinding: ViewDataBinding?, item: Topic, position: Int) {
        val binding = vdBinding as ItemCircleMainTopicBinding
        binding.ivIcon.setCircular(5)
        binding.ivIcon.loadImage(item.pic, ImageOptions().apply {
            placeholder = R.mipmap.ic_def_square_img
            error=R.mipmap.ic_def_square_img
        })
        binding.tvNum.text = "${item.postsCount}帖子 ${item.heat}热度"
        binding.bean = item
    }
}