package com.changanford.circle.adapter

import android.content.Context
import androidx.databinding.ViewDataBinding
import com.changanford.circle.R
import com.changanford.circle.bean.Imag
import com.changanford.circle.databinding.ItemLabelCircleBinding
import com.changanford.circle.ext.loadImage
import com.changanford.common.basic.adapter.BaseAdapterOneLayout
import com.changanford.common.util.DisplayUtil
import com.changanford.common.util.JumpUtils

/**
 *Author lcw
 *Time on 2021/9/28
 *Purpose
 */
class LabelAdapter(private val context: Context, var size: Int) : BaseAdapterOneLayout<Imag>(
    context,
    R.layout.item_label_circle
) {
    override fun fillData(vdBinding: ViewDataBinding?, item: Imag, position: Int) {
        val binding =vdBinding as ItemLabelCircleBinding
        val params = binding.labelIcon.layoutParams
        params.width =
            DisplayUtil.dip2px(context, size.toFloat())
        params.height =
            DisplayUtil.dip2px(context, size.toFloat())
        binding.labelIcon.layoutParams = params
        binding.labelIcon.loadImage(item.img)
        binding.labelIcon.setOnClickListener {
            JumpUtils.instans?.jump(item.jumpDataType, item.jumpDataValue)
        }
    }
}