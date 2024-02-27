package com.changanford.home.adapter

import android.view.View
import android.view.ViewGroup
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseDataBindingHolder
import com.changanford.common.bean.Topic
import com.changanford.common.util.MConstant
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
            setMargin(
                root,
                ((55.toDouble() / 375) * MConstant.deviceWidth).toInt(),
                holder.layoutPosition
            )
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

    private fun setMargin(view: View?, margin: Int, position: Int) {
        view?.let {
            val params = view.layoutParams as ViewGroup.MarginLayoutParams
            if (isOdd(position)) {
                params.leftMargin = 30.toIntPx()
            } else params.leftMargin = 0
        }

    }

    private fun isOdd(number: Int): Boolean {
        return number % 2 != 0 // 如果余数不等于零则表示该数字为奇数
    }

    private fun setTitleMaxWidth(binding: ItemSearchTopicBinding) {
        binding.clContent.post {
            val useWidth =
                binding.clContent.width - binding.tvIcon.width - 32.toIntPx()
            binding.tvTitle.maxWidth = useWidth
        }
    }
}