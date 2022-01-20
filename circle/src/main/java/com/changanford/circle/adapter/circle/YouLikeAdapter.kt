package com.changanford.circle.adapter.circle

import android.annotation.SuppressLint
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseDataBindingHolder
import com.changanford.circle.R
import com.changanford.circle.databinding.ItemYoulikeBinding
import com.changanford.common.bean.NewCircleBean
import com.changanford.common.utilext.load

class YouLikeAdapter: BaseQuickAdapter<NewCircleBean, BaseDataBindingHolder<ItemYoulikeBinding>>(R.layout.item_youlike){
    @SuppressLint("SetTextI18n")
    override fun convert(holder: BaseDataBindingHolder<ItemYoulikeBinding>, itemData: NewCircleBean) {
        holder.dataBinding?.apply {
            imgCover.load(itemData.pic)
            model=itemData
            executePendingBindings()
        }
    }
}