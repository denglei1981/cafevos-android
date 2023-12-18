package com.changanford.circle.adapter

import android.annotation.SuppressLint
import android.view.View
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.module.LoadMoreModule
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.changanford.circle.R
import com.changanford.circle.bean.HotPicItemBean
import com.changanford.circle.databinding.ItemMainHotTopicBinding
import com.changanford.circle.utils.MUtils
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
            MUtils.setTopMargin(binding.clContent, 18, holder.layoutPosition)
            binding.tvNum.text = "${item.postsCount}帖子     ${item.viewsCount}浏览"
            binding.bean = item
            binding.ivPic.loadCompress(item.pic)
            binding.ivPic.setCircular(5)
            binding.ivHintIcon.setColorFilter(ContextCompat.getColor(context,R.color.color_1700F4))
            when (val position = holder.layoutPosition + 1) {
                1 -> {
                    binding.ivIcon.setImageResource(R.drawable.icon_huati_one)
                    binding.ivIcon.visibility = View.VISIBLE
                    binding.tvIcon.visibility = View.GONE
                }
                2 -> {
                    binding.ivIcon.setImageResource(R.drawable.icon_huati_two)
                    binding.ivIcon.visibility = View.VISIBLE
                    binding.tvIcon.visibility = View.GONE
                }
                3 -> {
                    binding.ivIcon.setImageResource(R.drawable.icon_huati_three)
                    binding.ivIcon.visibility = View.VISIBLE
                    binding.tvIcon.visibility = View.GONE
                }
                else -> {
                    binding.ivIcon.setImageResource(R.drawable.icon_huati_three)
                    binding.ivIcon.visibility = View.GONE
                    binding.tvIcon.visibility = View.VISIBLE
                    binding.tvIcon.text = position.toString()
                }
            }


        }
    }
}