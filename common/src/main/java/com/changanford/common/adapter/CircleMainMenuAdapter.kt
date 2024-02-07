package com.changanford.common.adapter

import android.content.Context
import android.view.View
import androidx.databinding.ViewDataBinding
import com.changanford.common.R
import com.changanford.common.basic.adapter.BaseAdapterOneLayout
import com.changanford.common.bean.CircleMainMenuBean
import com.changanford.common.databinding.ItemCircleMainMenuBinding
import com.changanford.common.util.MUtils
import com.changanford.common.util.ext.loadImage

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