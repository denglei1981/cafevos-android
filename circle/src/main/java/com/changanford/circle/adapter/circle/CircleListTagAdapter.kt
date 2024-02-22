package com.changanford.circle.adapter.circle

import android.annotation.SuppressLint
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseDataBindingHolder
import com.changanford.circle.R
import com.changanford.circle.databinding.ItemCircleListTagBinding
import com.changanford.common.bean.NewCirceTagBean

class CircleListTagAdapter :
    BaseQuickAdapter<NewCirceTagBean, BaseDataBindingHolder<ItemCircleListTagBinding>>(R.layout.item_circle_list__tag) {
    @SuppressLint("SetTextI18n")
    override fun convert(
        holder: BaseDataBindingHolder<ItemCircleListTagBinding>,
        item: NewCirceTagBean
    ) {
        holder.dataBinding?.apply {
            model = item
            executePendingBindings()
        }
    }
}