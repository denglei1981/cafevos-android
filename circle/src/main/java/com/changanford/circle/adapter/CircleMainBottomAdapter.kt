package com.changanford.circle.adapter

import android.content.Context
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.listener.OnItemClickListener
import com.chad.library.adapter.base.module.LoadMoreModule
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.changanford.circle.R
import com.changanford.circle.bean.CircleMainBottomItemBean
import com.changanford.circle.config.CircleConfig
import com.changanford.circle.databinding.ItemCircleDetailsBottomBinding
import com.changanford.circle.databinding.ItemCircleMainBottomBinding
import com.changanford.circle.ext.ImageOptions
import com.changanford.circle.ext.loadImage
import com.changanford.circle.ext.setCircular
import com.changanford.circle.ext.toIntPx
import com.changanford.common.basic.adapter.BaseAdapterOneLayout
import com.changanford.common.util.DensityUtils
import com.changanford.common.utilext.GlideUtils
import com.changanford.common.utilext.toast
import com.luck.picture.lib.tools.ScreenUtils

/**
 *Author lcw
 *Time on 2021/9/22
 *Purpose
 */
class CircleMainBottomAdapter(context: Context) :
    BaseQuickAdapter<CircleMainBottomItemBean, BaseViewHolder>(R.layout.item_circle_main_bottom),
    LoadMoreModule {

    private val imgWidth by lazy {
        (ScreenUtils.getScreenWidth(context) - DensityUtils.dip2px(60F)) / 2
    }

    override fun convert(holder: BaseViewHolder, item: CircleMainBottomItemBean) {
        val binding = DataBindingUtil.bind<ItemCircleMainBottomBinding>(holder.itemView)
        binding?.let {
            binding.ivBg.setCircular(5)

            val params = binding.clContent.layoutParams as ViewGroup.MarginLayoutParams
            if (holder.layoutPosition == 0 || holder.layoutPosition == 1) {
                params.topMargin =
                    10.toIntPx()
            } else params.topMargin = 0

            binding.tvLikeNum.text = "${if (item.likesCount > 0) item.likesCount else "0"}"
//             item.isLike == 1//点赞

            if (item.type == 3) {//视频
                binding.ivPlay.visibility = View.VISIBLE
            } else {
                binding.ivPlay.visibility = View.GONE
            }

            if (item.city.isNullOrEmpty()) {
                binding.tvCity.visibility = View.GONE
            } else {
                binding.tvCity.visibility = View.VISIBLE
                binding.tvCity.text = item.city
            }

            if (holder.layoutPosition == 2) {
                binding.ivVery.visibility = View.VISIBLE
            } else {
                binding.ivVery.visibility = View.GONE
            }

            if (item.itemImgHeight == 0) {
                item.itemImgHeight = imgWidth//默认正方形
                if (item.pics.isNotEmpty()) {
                    val lastIndex = item.pics.lastIndexOf("androidios") + 10
                    val lastdot = item.pics.lastIndexOf(".")
                    if (lastIndex != -1 && lastdot != -1) {
                        val wh = item.pics.substring(lastIndex, lastdot).split("_")
                        if (wh.size == 2) {
                            item.itemImgHeight =
                                (imgWidth * wh[1].toDouble() / wh[0].toDouble()).toInt()
                        }
                    }
                }
            }
            binding.ivBg.layoutParams?.height = item.itemImgHeight

            binding.ivHead.loadImage(
                CircleConfig.TestUrl,
                ImageOptions().apply { circleCrop = true })
            GlideUtils.loadBD(GlideUtils.handleImgUrl(item.pics), binding.ivBg)

            val content = if (!item.title.isNullOrEmpty()) {
                item.title
            } else item.content
            binding.tvTitle.text = content

            val labelAdapter = LabelAdapter(context, 15)
            labelAdapter.setItems(item.authorBaseVo?.imags)
            binding.ryLabel.adapter = labelAdapter

            binding.bean = item
        }
    }
}