package com.changanford.circle.adapter

import android.content.Context
import androidx.databinding.ViewDataBinding
import com.changanford.circle.R
import com.changanford.circle.config.CircleConfig
import com.changanford.circle.databinding.ItemPostDetailsCommentBinding
import com.changanford.circle.ext.ImageOptions
import com.changanford.circle.ext.loadImage
import com.changanford.common.basic.adapter.BaseAdapterOneLayout

class PostDetailsCommentAdapter(context: Context) :
    BaseAdapterOneLayout<String>(context, R.layout.item_post_details_comment) {
    override fun fillData(vdBinding: ViewDataBinding?, item: String, position: Int) {
        val binding = vdBinding as ItemPostDetailsCommentBinding
        binding.ivHead.loadImage(CircleConfig.TestUrl, ImageOptions().apply { circleCrop = true })
        binding.bean = item
    }
}