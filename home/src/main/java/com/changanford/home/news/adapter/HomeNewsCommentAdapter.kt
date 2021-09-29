package com.changanford.home.news.adapter

import android.content.Context
import androidx.databinding.ViewDataBinding
import com.changanford.common.basic.adapter.BaseAdapterOneLayout
import com.changanford.home.R
import com.changanford.home.databinding.ItemHomeNewsCommentBinding

class HomeNewsCommentAdapter(context: Context) :
    BaseAdapterOneLayout<String>(context, R.layout.item_home_news_comment) {
    override fun fillData(vdBinding: ViewDataBinding?, item: String, position: Int) {
        val binding = vdBinding as ItemHomeNewsCommentBinding
//        binding.ivHead.loadImage(CircleConfig.TestUrl, ImageOptions().apply { circleCrop = true })
        binding.bean = item
    }
}