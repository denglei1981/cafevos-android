package com.changanford.circle.adapter.circle

import android.annotation.SuppressLint
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseDataBindingHolder
import com.changanford.circle.R
import com.changanford.circle.databinding.ItemYoulikeBinding

class YouLikeAdapter: BaseQuickAdapter<String, BaseDataBindingHolder<ItemYoulikeBinding>>(R.layout.item_youlike){
    @SuppressLint("SetTextI18n")
    override fun convert(holder: BaseDataBindingHolder<ItemYoulikeBinding>, item: String) {
        holder.dataBinding?.apply {

        }
    }
}