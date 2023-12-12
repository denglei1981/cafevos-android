package com.changanford.circle.adapter

import android.content.Context
import android.view.View
import androidx.databinding.ViewDataBinding
import com.changanford.circle.R
import com.changanford.circle.bean.CircleMainMenuBean
import com.changanford.circle.databinding.ItemCircleMainMenuBinding
import com.changanford.circle.ext.loadImage
import com.changanford.circle.utils.MUtils
import com.changanford.common.basic.adapter.BaseAdapterOneLayout

/**
 *Author lcw
 *Time on 2021/9/24
 *Purpose
 */
class CircleMainMenuAdapter(context: Context, private val isCircleDetails: Boolean = false) :
    BaseAdapterOneLayout<CircleMainMenuBean>(context, R.layout.item_circle_main_menu) {
    override fun fillData(vdBinding: ViewDataBinding?, item: CircleMainMenuBean, position: Int) {
        val binding = vdBinding as ItemCircleMainMenuBinding
        MUtils.setTopMargin(binding.llContent, 20, position)

        item.pic?.let {
            if (item.pic.toString().length != 1) {
                binding.ivIcon.loadImage(item.pic)
                binding.ivIcon.visibility = View.VISIBLE
            } else {
                binding.ivIcon.visibility = View.GONE
            }
        }
        binding.tvContent.text = item.content
    }
}