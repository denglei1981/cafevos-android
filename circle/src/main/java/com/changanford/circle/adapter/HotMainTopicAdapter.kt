package com.changanford.circle.adapter

import android.annotation.SuppressLint
import android.content.Context
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.module.LoadMoreModule
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.changanford.circle.R
import com.changanford.circle.bean.HotPicItemBean
import com.changanford.circle.databinding.ItemHotTopicBinding
import com.changanford.circle.databinding.ItemMainHotTopicBinding
import com.changanford.circle.ext.setCircular
import com.changanford.circle.utils.MUtils
import com.changanford.common.basic.adapter.BaseAdapterOneLayout

/**
 *Author lcw
 *Time on 2021/9/23
 *Purpose
 */
class HotMainTopicAdapter :
    BaseQuickAdapter<HotPicItemBean, BaseViewHolder>(R.layout.item_main_hot_topic), LoadMoreModule {

    @SuppressLint("SetTextI18n")
    override fun convert(holder: BaseViewHolder, item: HotPicItemBean) {
        val binding = DataBindingUtil.bind<ItemMainHotTopicBinding>(holder.itemView)
        binding?.let {
            MUtils.setTopMargin(binding.clContent, 18, holder.layoutPosition)
            binding.tvNum.text = "${item.postsCount}帖子     ${item.heat}热度"
            binding.bean = item
            val position=holder.layoutPosition+1
            when(position){
                1,2,3->{
                    binding.tvIcon.setTextColor(ContextCompat.getColor(context,R.color.color_FC5E42))
                }else->{
                    binding.tvIcon.setTextColor(ContextCompat.getColor(context,R.color.color_D1D2D7))
                }
            }
            binding.tvIcon.text=(holder.layoutPosition+1).toString()


        }
    }
}