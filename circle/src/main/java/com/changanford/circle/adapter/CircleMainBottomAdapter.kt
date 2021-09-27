package com.changanford.circle.adapter

import android.content.Context
import android.view.View
import android.view.ViewGroup
import androidx.databinding.ViewDataBinding
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
import com.luck.picture.lib.tools.ScreenUtils

/**
 *Author lcw
 *Time on 2021/9/22
 *Purpose
 */
class CircleMainBottomAdapter(context: Context) :
    BaseAdapterOneLayout<CircleMainBottomItemBean>(context, R.layout.item_circle_main_bottom) {

    private val imgWidth by lazy {
        (ScreenUtils.getScreenWidth(context) - DensityUtils.dip2px(60F)) / 2
    }

    override fun fillData(vdBinding: ViewDataBinding?, item: CircleMainBottomItemBean, position: Int) {
        val binding = vdBinding as ItemCircleMainBottomBinding
        binding.ivBg.setCircular(10)

        val params = binding.clContent.layoutParams as ViewGroup.MarginLayoutParams
        if (position == 0 || position == 1) {
            params.topMargin =
                10.toIntPx()
        } else params.topMargin = 0

        if (position == 4) {
            binding.ivPlay.visibility = View.VISIBLE
        } else {
            binding.ivPlay.visibility = View.GONE
        }

        if (position == 2) {
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

        binding.ivHead.loadImage(CircleConfig.TestUrl, ImageOptions().apply { circleCrop = true })
        GlideUtils.loadBD(GlideUtils.handleImgUrl(item.pics), binding.ivBg)
        binding.bean = item
    }
}