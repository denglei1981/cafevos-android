package com.changanford.home.adapter

import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseDataBindingHolder
import com.changanford.common.bean.HotPicItemBean
import com.changanford.common.utilext.toIntPx
import com.changanford.home.R
import com.changanford.home.databinding.ItemSearchTopicBinding

/**
 * @author: niubobo
 * @date: 2024/2/1
 * @description：
 */
class PolySearchTopicAdapter :
    BaseQuickAdapter<HotPicItemBean, BaseDataBindingHolder<ItemSearchTopicBinding>>(
        R.layout.item_search_topic
    ) {
    override fun convert(
        holder: BaseDataBindingHolder<ItemSearchTopicBinding>,
        item: HotPicItemBean
    ) {
        holder.dataBinding?.apply {
            setTitleMaxWidth(this)
            tvTitle.text = item.name
            tvContent.text = item.description
        }
    }

    private fun setTitleMaxWidth(binding: ItemSearchTopicBinding) {
        binding.clContent.post {
            val useWidth =
                binding.clContent.width - binding.tvIcon.width - binding.ivRight.width - 15.toIntPx()
            binding.tvTitle.maxWidth = useWidth
        }
    }
}