package com.changanford.circle.adapter

import android.content.Context
import androidx.databinding.ViewDataBinding
import com.changanford.circle.R
import com.changanford.circle.databinding.ItemHotTopicBinding
import com.changanford.circle.utils.MUtils
import com.changanford.common.basic.adapter.BaseAdapterOneLayout

/**
 *Author lcw
 *Time on 2021/9/23
 *Purpose
 */
class HotTopicAdapter(context: Context) :
    BaseAdapterOneLayout<String>(context, R.layout.item_hot_topic) {
    override fun fillData(vdBinding: ViewDataBinding?, item: String, position: Int) {
        val binding = vdBinding as ItemHotTopicBinding
        MUtils.setTopMargin(binding.llContent, 18, position)
        binding.bean = item
    }
}