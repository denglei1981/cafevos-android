package com.changanford.circle.adapter

import android.content.Context
import androidx.databinding.ViewDataBinding
import com.changanford.circle.R
import com.changanford.circle.databinding.ItemCircleMainCircleBinding
import com.changanford.circle.ext.loadImage
import com.changanford.circle.ext.setCircular
import com.changanford.common.basic.adapter.BaseAdapterOneLayout

/**
 *Author lcw
 *Time on 2021/9/23
 *Purpose
 */
class CircleMainCircleAdapter(context: Context) :
    BaseAdapterOneLayout<String>(context, R.layout.item_circle_main_circle) {
    override fun fillData(vdBinding: ViewDataBinding?, item: String, position: Int) {
        val binding=vdBinding as ItemCircleMainCircleBinding
        binding.ivIcon.setCircular(5)
        binding.ivIcon.loadImage("http://139.186.199.89:8008/images/20210909/1631182101477.jpg")
    }
}