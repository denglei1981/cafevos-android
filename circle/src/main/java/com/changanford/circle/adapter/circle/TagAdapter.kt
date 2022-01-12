package com.changanford.circle.adapter.circle

import android.annotation.SuppressLint
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseDataBindingHolder
import com.changanford.circle.R
import com.changanford.circle.databinding.ItemCircleTagBinding
import com.changanford.common.bean.NewCirceTagBean

class TagAdapter: BaseQuickAdapter<NewCirceTagBean, BaseDataBindingHolder<ItemCircleTagBinding>>(R.layout.item_circle_tag){
    @SuppressLint("SetTextI18n")
    override fun convert(holder: BaseDataBindingHolder<ItemCircleTagBinding>, item: NewCirceTagBean) {
        holder.dataBinding?.apply {
            model=item
            executePendingBindings()
        }
    }
}