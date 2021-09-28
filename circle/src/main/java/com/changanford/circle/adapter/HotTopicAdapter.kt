package com.changanford.circle.adapter

import android.annotation.SuppressLint
import android.content.Context
import androidx.databinding.ViewDataBinding
import com.changanford.circle.R
import com.changanford.circle.bean.HotPicItemBean
import com.changanford.circle.databinding.ItemHotTopicBinding
import com.changanford.circle.utils.MUtils
import com.changanford.common.basic.adapter.BaseAdapterOneLayout

/**
 *Author lcw
 *Time on 2021/9/23
 *Purpose
 */
class HotTopicAdapter(context: Context) :
    BaseAdapterOneLayout<HotPicItemBean>(context, R.layout.item_hot_topic) {
    @SuppressLint("SetTextI18n")
    override fun fillData(vdBinding: ViewDataBinding?, item: HotPicItemBean, position: Int) {
        val binding = vdBinding as ItemHotTopicBinding
        MUtils.setTopMargin(binding.llContent, 18, position)

        binding.tvContent.text="${item.postsCount}帖子     ${item.likesCount}热度"

        binding.bean = item
    }
}