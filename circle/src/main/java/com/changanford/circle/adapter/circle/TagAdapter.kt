package com.changanford.circle.adapter.circle

import android.annotation.SuppressLint
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseDataBindingHolder
import com.changanford.circle.R
import com.changanford.circle.databinding.ItemCircleTagBinding
import com.changanford.common.bean.NewCirceTagBean

class TagAdapter(private val isCircleHome:Boolean=false) :
    BaseQuickAdapter<NewCirceTagBean, BaseDataBindingHolder<ItemCircleTagBinding>>(R.layout.item_circle_tag) {
    @SuppressLint("SetTextI18n")
    override fun convert(
        holder: BaseDataBindingHolder<ItemCircleTagBinding>,
        item: NewCirceTagBean
    ) {
        holder.dataBinding?.apply {
            if (!isCircleHome){
                tvTag.setBackgroundResource(R.drawable.shadow_f2f4f9_8dp)
            }else{
                tvTag.setBackgroundResource(R.drawable.bg_081700f4_100)
            }
            model = item
            executePendingBindings()
        }
    }
}