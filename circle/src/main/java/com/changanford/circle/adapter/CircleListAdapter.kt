package com.changanford.circle.adapter

import android.annotation.SuppressLint
import androidx.databinding.DataBindingUtil
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.module.LoadMoreModule
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.changanford.circle.R
import com.changanford.circle.bean.ChoseCircleBean
import com.changanford.circle.databinding.ItemCircleListBinding
import com.changanford.circle.ext.*
import com.changanford.circle.utils.MUtils

/**
 *Author lcw
 *Time on 2021/9/18
 *Purpose
 */
class CircleListAdapter :
    BaseQuickAdapter<ChoseCircleBean, BaseViewHolder>(R.layout.item_circle_list),LoadMoreModule {

    @SuppressLint("SetTextI18n")
    override fun convert(holder: BaseViewHolder, item: ChoseCircleBean) {
        val binding = DataBindingUtil.bind<ItemCircleListBinding>(holder.itemView)
        binding?.let {
            MUtils.setTopMargin(binding.clItem, 17, holder.layoutPosition)
            binding.ivIcon.setCircular(5)
            binding.tvNum.text = "${item.userCount} 成员     ${item.postsCount} 帖子"
            binding.bean = item
        }

    }
}