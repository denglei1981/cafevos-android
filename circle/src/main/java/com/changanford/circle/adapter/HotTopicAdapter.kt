package com.changanford.circle.adapter

import android.annotation.SuppressLint
import android.content.Context
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.module.LoadMoreModule
import com.chad.library.adapter.base.viewholder.BaseViewHolder
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
class HotTopicAdapter:
    BaseQuickAdapter<HotPicItemBean,BaseViewHolder>( R.layout.item_hot_topic),LoadMoreModule {

    @SuppressLint("SetTextI18n")
    override fun convert(holder: BaseViewHolder, item: HotPicItemBean) {
        val binding =DataBindingUtil.bind<ItemHotTopicBinding>(holder.itemView)
        binding?.let {
            MUtils.setTopMargin(binding.llContent, 18, holder.layoutPosition)

            binding.tvContent.text="${item.postsCount}帖子     ${item.likesCount}热度"

            binding.bean = item
        }
    }
}