package com.changanford.circle.adapter

import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseDataBindingHolder
import com.changanford.circle.R
import com.changanford.circle.databinding.ItemQusetionTopTagBinding

/**
 * @author: niubobo
 * @date: 2024/5/8
 * @description：
 */
class QuestionTopTagAdapter :
    BaseQuickAdapter<String, BaseDataBindingHolder<ItemQusetionTopTagBinding>>(
        R.layout.item_qusetion_top_tag
    ) {
    override fun convert(holder: BaseDataBindingHolder<ItemQusetionTopTagBinding>, item: String) {
        holder.dataBinding?.let {
            it.tvTag.text = item
        }
    }
}