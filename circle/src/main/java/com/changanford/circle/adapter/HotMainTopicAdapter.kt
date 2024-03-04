package com.changanford.circle.adapter

import android.annotation.SuppressLint
import androidx.core.view.isVisible
import androidx.databinding.DataBindingUtil
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.module.LoadMoreModule
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.changanford.circle.R
import com.changanford.circle.bean.HotPicItemBean
import com.changanford.circle.databinding.ItemMainHotTopicBinding
import com.changanford.common.util.CountUtils
import com.changanford.common.util.MUtils
import com.changanford.common.util.ext.setCircular
import com.changanford.common.util.ext.setDrawableColor
import com.changanford.common.utilext.GlideUtils.loadCompress
import com.changanford.common.utilext.setDrawableNull
import com.changanford.common.utilext.setDrawableRight

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
            MUtils.setTopMargin(binding.clContent, 8, holder.layoutPosition)
            if (item.isHot == 1) {
                binding.tvTalk.setDrawableRight(R.mipmap.ic_se_topic_hot)
            } else if (item.isNew == "YES") {
                binding.tvTalk.setDrawableRight(R.mipmap.ic_se_topic_new)
            } else {
                binding.tvTalk.setDrawableNull()
            }
            binding.tvNum.text = "${
                CountUtils.formatNum(
                    item.postsCount.toString(),
                    false
                )
            }帖子     ${
                CountUtils.formatNum(
                    item.viewsCount.toString(),
                    false
                )
            }浏览"
            binding.bean = item
            binding.ivPic.loadCompress(item.pic)
            binding.ivPic.setCircular(12)
            binding.tvPosition.isVisible = true
            when (val position = holder.layoutPosition + 1) {
                1 -> {
                    binding.tvPosition.setDrawableColor(R.color.color_E67400)
                    binding.tvPosition.text = position.toString()
                }

                2 -> {
                    binding.tvPosition.setDrawableColor(R.color.color_1700F4)
                    binding.tvPosition.text = position.toString()
                }

                3 -> {
                    binding.tvPosition.setDrawableColor(R.color.color_009987)
                    binding.tvPosition.text = position.toString()
                }

                else -> {
                    binding.tvPosition.isVisible = false
                }
            }


        }
    }
}