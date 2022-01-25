package com.changanford.circle.ui.ask.adapter

import androidx.databinding.DataBindingUtil
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.module.LoadMoreModule
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.changanford.circle.R
import com.changanford.circle.bean.CircleMemberBean
import com.changanford.circle.databinding.ItemHotMechanicBinding

/**
 *Author lcw
 *Time on 2021/9/22
 *Purpose
 */
class HotMechanicAdapter :
    BaseQuickAdapter<CircleMemberBean, BaseViewHolder>(R.layout.item_hot_mechanic), LoadMoreModule {



    override fun convert(holder: BaseViewHolder, item: CircleMemberBean) {
        val binding = DataBindingUtil.bind<ItemHotMechanicBinding>(holder.itemView)
        binding?.let {

        }
    }

}