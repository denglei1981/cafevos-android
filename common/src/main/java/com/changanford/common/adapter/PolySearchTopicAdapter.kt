package com.changanford.common.adapter

import android.view.ViewGroup
import androidx.core.view.isVisible
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseDataBindingHolder
import com.changanford.common.R
import com.changanford.common.bean.Topic
import com.changanford.common.databinding.ItemSearchTopicBinding
import com.changanford.common.utilext.toIntPx

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
            setMargin(
                this,
                item,
                holder.layoutPosition
            )

        }
    }

    private fun setMargin(binding: ItemSearchTopicBinding, item: Topic,position: Int) {
        binding.root.post {
            val params = binding.tvIcon.layoutParams as ViewGroup.MarginLayoutParams
            if (isOdd(position)) {
                val leftMargin =
                    binding.clContent.width - binding.tvIcon.width - binding.tvTitle.maxWidth - binding.ivTag.width - 4.toIntPx()
                params.setMargins(leftMargin, 0, 0, 0)
//                params.leftMargin = 28.toIntPx()
            } else params.setMargins(0, 0, 0, 0)
            binding.tvIcon.layoutParams = params

            binding.apply {
                tvTitle.text = item.name

                tvContent.text = item.description

                if (item.isHot == 1) {
                    ivTag.isVisible = true
                    ivTag.setImageResource(R.mipmap.ic_se_topic_hot)
                } else if (item.isNew == "YES") {
                    ivTag.isVisible = true
                    ivTag.setImageResource(R.mipmap.ic_se_topic_new)
                } else {
                    ivTag.isVisible = false
                }
            }
        }
    }

    private fun isOdd(number: Int): Boolean {
        return number % 2 != 0 // 如果余数不等于零则表示该数字为奇数
    }

}