package com.changanford.circle.adapter

import android.content.Context
import android.view.ViewGroup
import androidx.databinding.ViewDataBinding
import com.changanford.circle.R
import com.changanford.circle.bean.User

import com.changanford.circle.databinding.ItemCircleDetailsPersonalBinding

import com.changanford.common.basic.adapter.BaseAdapterOneLayout
import com.changanford.common.util.ext.ImageOptions
import com.changanford.common.util.ext.loadImage
import com.changanford.common.utilext.toIntPx

/**
 *Author lcw
 *Time on 2021/9/22
 *Purpose
 */
class CircleDetailsPersonalAdapter(context: Context) :
    BaseAdapterOneLayout<User>(context, R.layout.item_circle_details_personal) {
    override fun fillData(vdBinding: ViewDataBinding?, item: User, position: Int) {
        val binding = vdBinding as ItemCircleDetailsPersonalBinding
        val params = binding.ivPersonal.layoutParams as ViewGroup.MarginLayoutParams
        if (position == 0) {
            params.leftMargin =
                0
        } else params.leftMargin = -(6.toIntPx())

        binding.ivPersonal.loadImage(
            item.avatar,
            ImageOptions().apply {
                circleCrop = true
                error = R.mipmap.head_default
            })
    }
}