package com.changanford.home.adapter

import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseDataBindingHolder
import com.changanford.common.bean.Topic
import com.changanford.common.utilext.setDrawableNull
import com.changanford.common.utilext.setDrawableRight
import com.changanford.common.utilext.toIntPx
import com.changanford.home.R
import com.changanford.home.databinding.ItemSearchTopicBinding

/**
 * @author: niubobo
 * @date: 2024/2/1
 * @description：
 */
class PolySearchTopicAdapter :
    BaseQuickAdapter<Topic, BaseDataBindingHolder<ItemSearchTopicBinding>>(
        R.layout.item_search_topic
    ) {
    override fun convert(
        holder: BaseDataBindingHolder<ItemSearchTopicBinding>,
        item: Topic
    ) {
        holder.dataBinding?.apply {
//            setTitleMaxWidth(this)
            tvTitle.text = item.name

            tvContent.text = item.description

            if (item.isHot == 1) {
                tvTitle.setDrawableRight(R.mipmap.ic_se_topic_hot)
            } else if (item.isNew == "YES") {
                tvTitle.setDrawableRight(R.mipmap.ic_se_topic_new)
            } else {
                tvTitle.setDrawableNull()
            }
        }
    }

    private fun setTitleMaxWidth(binding: ItemSearchTopicBinding) {
        binding.clContent.post {
            val useWidth =
                binding.clContent.width - binding.tvIcon.width - 32.toIntPx()
            binding.tvTitle.maxWidth = useWidth
        }
    }
}