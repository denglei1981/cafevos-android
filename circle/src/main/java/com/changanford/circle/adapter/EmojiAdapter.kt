package com.changanford.circle.adapter

import androidx.databinding.DataBindingUtil
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.changanford.circle.R
import com.changanford.circle.databinding.ItemChatEmojiBinding

class EmojiAdapter :
    BaseQuickAdapter<String, BaseViewHolder>( R.layout.item_chat_emoji) {

    override fun convert(holder: BaseViewHolder, item: String) {
        val binding :ItemChatEmojiBinding= DataBindingUtil.bind(holder.itemView)!!
        binding.tvContent.text = item
        binding.bean = item
    }
}