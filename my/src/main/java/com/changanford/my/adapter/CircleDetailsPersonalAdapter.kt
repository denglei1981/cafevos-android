package com.changanford.my.adapter

import android.content.Context
import android.view.ViewGroup
import androidx.databinding.ViewDataBinding


import com.changanford.common.basic.adapter.BaseAdapterOneLayout
import com.changanford.common.bean.User
import com.changanford.common.utilext.GlideUtils
import com.changanford.my.R
import com.changanford.my.databinding.ItemCircleDetailsPersonalBinding

/**
 *Author lcw
 *Time on 2021/9/22
 *Purpose
 */
class CircleDetailsPersonalAdapter(var context: Context) :
    BaseAdapterOneLayout<User>(context, R.layout.item_circle_details_personal) {
    override fun fillData(vdBinding: ViewDataBinding?, item: User, position: Int) {
        val binding = vdBinding as ItemCircleDetailsPersonalBinding
        val params = binding.ivPersonal.layoutParams as ViewGroup.MarginLayoutParams
        if (position == 0) {
            params.leftMargin =
                0
        } else params.leftMargin = (-(dpToPx(context,6f))).toInt()

        GlideUtils.loadBD(item.avatar,binding.ivPersonal)

    }
}
fun dpToPx(context: Context, dp: Float): Float {
    return dp * context.resources.displayMetrics.density
}