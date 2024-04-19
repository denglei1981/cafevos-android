package com.changanford.circle.adapter.circle

import android.annotation.SuppressLint
import androidx.core.view.isVisible
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseDataBindingHolder
import com.changanford.circle.R
import com.changanford.circle.databinding.ItemMycircleBinding
import com.changanford.common.bean.NewCircleBean
import com.changanford.common.utilext.load

class MyCircleAdapter :
    BaseQuickAdapter<NewCircleBean, BaseDataBindingHolder<ItemMycircleBinding>>(R.layout.item_mycircle) {
    @SuppressLint("SetTextI18n")
    override fun convert(
        holder: BaseDataBindingHolder<ItemMycircleBinding>,
        itemData: NewCircleBean
    ) {
        holder.dataBinding?.apply {
            imgCover.load(itemData.pic)
            ivTopTips.isVisible = itemData.star == "YES"
            wtvTitle.text = itemData.name
        }
    }
}