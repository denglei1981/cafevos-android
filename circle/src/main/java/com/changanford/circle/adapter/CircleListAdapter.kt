package com.changanford.circle.adapter

import android.content.Context
import android.view.ViewGroup
import androidx.databinding.ViewDataBinding
import com.changanford.circle.R
import com.changanford.circle.config.CircleConfig
import com.changanford.circle.databinding.ItemCircleListBinding
import com.changanford.circle.ext.*
import com.changanford.common.basic.adapter.BaseAdapterOneLayout

/**
 *Author lcw
 *Time on 2021/9/18
 *Purpose
 */
class CircleListAdapter(context: Context) :
    BaseAdapterOneLayout<String>(context, R.layout.item_circle_list) {
    override fun fillData(vdBinding: ViewDataBinding?, item: String, position: Int) {
        val binding = vdBinding as ItemCircleListBinding
        //第一个item追加加一个margin
        val params = binding.clItem.layoutParams as ViewGroup.MarginLayoutParams
        if (position == 0) {
            params.topMargin =
                17.toIntPx()
        } else params.topMargin = 0
        binding.ivIcon.setCircular(5)
        binding.ivIcon.loadImage(CircleConfig.TestUrl)
        binding.bean = item
    }
}