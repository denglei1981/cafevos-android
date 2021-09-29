package com.changanford.circle.adapter

import android.content.Context
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.module.LoadMoreModule
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.changanford.circle.R
import com.changanford.circle.bean.CircleMemberBean
import com.changanford.circle.config.CircleConfig
import com.changanford.circle.databinding.ItemPersonalBinding
import com.changanford.circle.ext.ImageOptions
import com.changanford.circle.ext.loadImage
import com.changanford.circle.ext.toIntPx
import com.changanford.circle.utils.MUtils
import com.changanford.common.basic.adapter.BaseAdapterOneLayout

/**
 *Author lcw
 *Time on 2021/9/22
 *Purpose
 */
class PersonalAdapter :
    BaseQuickAdapter<CircleMemberBean, BaseViewHolder>(R.layout.item_personal),LoadMoreModule {

    override fun convert(holder: BaseViewHolder, item: CircleMemberBean) {
        val binding = DataBindingUtil.bind<ItemPersonalBinding>(holder.itemView)
        binding?.let {
            MUtils.setTopMargin(binding.clItem, 27, holder.layoutPosition)

            binding.ivIcon.loadImage(item.avatar, ImageOptions().apply { circleCrop = true })
            binding.tvName.text = item.nickname
            if (!item.starOrderNumStr.isNullOrEmpty()) {
                binding.tvOwner.visibility = View.VISIBLE
                binding.tvOwner.text = item.starOrderNumStr
            } else {
                binding.tvOwner.visibility = View.GONE
            }
            val labelAdapter = LabelAdapter(context, 20)
            labelAdapter.setItems(item.imags)
            binding.ryImage.adapter = labelAdapter

            if (holder.layoutPosition == 4) {
                binding.tvOut.visibility = View.VISIBLE
            } else {
                binding.tvOut.visibility = View.GONE
            }
            binding.bean = item
        }
    }
}