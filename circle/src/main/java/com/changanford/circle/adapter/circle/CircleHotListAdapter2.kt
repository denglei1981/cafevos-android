package com.changanford.circle.adapter.circle

import android.annotation.SuppressLint
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseDataBindingHolder
import com.changanford.circle.R
import com.changanford.circle.databinding.Item2CircleHotlistBinding
import com.changanford.common.bean.NewCircleBean

class CircleHotListAdapter2: BaseQuickAdapter<NewCircleBean, BaseDataBindingHolder<Item2CircleHotlistBinding>>(R.layout.item2_circle_hotlist){
    @SuppressLint("SetTextI18n")
    override fun convert(holder: BaseDataBindingHolder<Item2CircleHotlistBinding>, item: NewCircleBean) {
        holder.dataBinding?.apply {
            model=item
            executePendingBindings()
        }
    }

}