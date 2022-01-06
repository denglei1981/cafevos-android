package com.changanford.circle.adapter.circle

import android.annotation.SuppressLint
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseDataBindingHolder
import com.changanford.circle.R
import com.changanford.circle.databinding.ItemCreateCircleTagBinding
import com.changanford.common.bean.NewCirceTagBean

class CircleTagAdapter: BaseQuickAdapter<NewCirceTagBean, BaseDataBindingHolder<ItemCreateCircleTagBinding>>(R.layout.item_create_circle_tag){
    @SuppressLint("SetTextI18n")
    override fun convert(holder: BaseDataBindingHolder<ItemCreateCircleTagBinding>, item: NewCirceTagBean) {
        holder.dataBinding?.apply {
            model=item
            executePendingBindings()
            checkBox.setOnClickListener {
                item.isCheck=checkBox.isChecked
            }
        }
    }
}