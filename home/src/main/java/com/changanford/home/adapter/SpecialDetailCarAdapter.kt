package com.changanford.home.adapter

import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.changanford.home.R
import com.changanford.home.bean.SpecialCarListBean
import com.changanford.home.databinding.ItemSpecialDetailCarBinding

/**
 *Author lcw
 *Time on 2023/12/15
 *Purpose
 */
class SpecialDetailCarAdapter :
    BaseQuickAdapter<SpecialCarListBean, BaseViewHolder>(R.layout.item_special_detail_car) {
    override fun convert(holder: BaseViewHolder, item: SpecialCarListBean) {
        val binding = DataBindingUtil.bind<ItemSpecialDetailCarBinding>(holder.itemView)
        binding?.let {
            binding.tvCarName.text = item.carModelName
            if (!item.isCheck){
                binding.tvCarName.background=ContextCompat.getDrawable(context,R.drawable.bg_081700f4_100)
            }else{
                binding.tvCarName.background=ContextCompat.getDrawable(context,R.drawable.bg_081700f4_100_st_1a1700)
            }
        }
    }
}