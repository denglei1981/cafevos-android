package com.changanford.circle.adapter.circle

import android.annotation.SuppressLint
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseDataBindingHolder
import com.changanford.circle.R
import com.changanford.circle.databinding.ItemMycircleBinding
import com.changanford.common.bean.NewCircleBean

class MyCircleAdapter: BaseQuickAdapter<NewCircleBean, BaseDataBindingHolder<ItemMycircleBinding>>(R.layout.item_mycircle){
    @SuppressLint("SetTextI18n")
    override fun convert(holder: BaseDataBindingHolder<ItemMycircleBinding>, itemData: NewCircleBean) {
        holder.dataBinding?.apply {
            model=itemData
            executePendingBindings()

        }
    }
}