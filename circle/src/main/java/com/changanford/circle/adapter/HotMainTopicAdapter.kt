package com.changanford.circle.adapter

import android.annotation.SuppressLint
import androidx.core.content.ContextCompat
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
import com.changanford.common.utilext.GlideUtils.loadCompress

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
            binding.tvNum.text = "${ CountUtils.formatNum(
                item.postsCount.toString(),
                false
            )}帖子     ${
                CountUtils.formatNum(
                    item.viewsCount.toString(),
                    false
                )
            }浏览"
            binding.bean = item
            binding.ivPic.loadCompress(item.pic)
            binding.ivPic.setCircular(12)
            binding.ivHintIcon.setColorFilter(ContextCompat.getColor(context, R.color.color_1700F4))
            when (val position = holder.layoutPosition + 1) {
                1 -> {

                }

                2 -> {

                }

                3 -> {

                }

                else -> {

                }
            }


        }
    }
}