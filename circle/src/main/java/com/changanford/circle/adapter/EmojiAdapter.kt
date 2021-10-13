package com.yw.li_model.adapter

import android.content.Context
import androidx.databinding.ViewDataBinding
import com.changanford.circle.R
import com.changanford.circle.databinding.ItemChatEmojiBinding
import com.changanford.common.basic.adapter.BaseAdapterOneLayout

class EmojiAdapter(context: Context) :
    BaseAdapterOneLayout<String>(context, R.layout.item_chat_emoji) {
    override fun fillData(vdBinding: ViewDataBinding?, item: String, position: Int) {
        val binding = vdBinding as ItemChatEmojiBinding
        binding.tvContent.text = item
        binding.bean = item
    }
}