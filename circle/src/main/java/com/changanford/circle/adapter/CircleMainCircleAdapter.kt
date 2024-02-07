package com.changanford.circle.adapter

import android.content.Context
import androidx.databinding.ViewDataBinding
import com.changanford.circle.R
import com.changanford.circle.bean.AllCircle
import com.changanford.circle.databinding.ItemCircleMainCircleBinding
import com.changanford.common.basic.adapter.BaseAdapterOneLayout
import com.changanford.common.util.ext.loadImage
import com.changanford.common.util.ext.setCircular

/**
 *Author lcw
 *Time on 2021/9/23
 *Purpose
 */
class CircleMainCircleAdapter(private val context: Context) :
    BaseAdapterOneLayout<AllCircle>(context, R.layout.item_circle_main_circle) {
    override fun fillData(vdBinding: ViewDataBinding?, item: AllCircle, position: Int) {
        val binding = vdBinding as ItemCircleMainCircleBinding
        binding.ivIcon.setCircular(5)
        binding.ivIcon.loadImage(item.pic)
        binding.tvName.text = item.name

//        val vto: ViewTreeObserver = binding.tvName.viewTreeObserver
//        vto.addOnGlobalLayoutListener(object : OnGlobalLayoutListener {
//            override fun onGlobalLayout() {
//                binding.tvName.viewTreeObserver.removeOnGlobalLayoutListener(this)
//                BlurBitmapUtil.blur(binding.ivIcon, binding.tvName, 4f, 8f)
//            }
//        })
    }
}