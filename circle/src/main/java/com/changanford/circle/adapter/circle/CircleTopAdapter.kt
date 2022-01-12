package com.changanford.circle.adapter.circle

import android.annotation.SuppressLint
import androidx.core.content.ContextCompat
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseDataBindingHolder
import com.changanford.circle.R
import com.changanford.circle.databinding.ItemCircleHotlistLayoutBinding
import com.changanford.common.bean.NewCircleBean
import com.changanford.common.utilext.load

class CircleTopAdapter: BaseQuickAdapter<NewCircleBean, BaseDataBindingHolder<ItemCircleHotlistLayoutBinding>>(R.layout.item_circle_hotlist_layout){
    @SuppressLint("SetTextI18n")
    override fun convert(holder: BaseDataBindingHolder<ItemCircleHotlistLayoutBinding>, item: NewCircleBean) {
        holder.dataBinding?.apply {
            val position=holder.absoluteAdapterPosition
            wtvRanking.apply {
                text="$position"
                setTextColor(ContextCompat.getColor(context,if(position<3)R.color.color_FC5E42 else R.color.color_D1D2D7))
            }
            imgCover.load(item.pic)
            model=item
            executePendingBindings()
        }
    }
}