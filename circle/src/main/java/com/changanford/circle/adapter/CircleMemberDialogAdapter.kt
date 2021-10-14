package com.changanford.circle.adapter

import android.content.Context
import android.view.View
import androidx.databinding.ViewDataBinding
import com.changanford.circle.R
import com.changanford.circle.bean.CircleMemberBean
import com.changanford.circle.databinding.ItemCircleMemberDialogBinding
import com.changanford.common.basic.adapter.BaseAdapterOneLayout
import com.changanford.common.utilext.GlideUtils

/**
 * @Author: lcw
 * @Date: 2020/11/17
 * @Des:
 */
class CircleMemberDialogAdapter(context: Context) :
    BaseAdapterOneLayout<CircleMemberBean>(context, R.layout.item_circle_member_dialog) {

    override fun fillData(vdBinding: ViewDataBinding?, item: CircleMemberBean, position: Int) {
        val binding = vdBinding as ItemCircleMemberDialogBinding
        GlideUtils.loadCircle(
            item.avatar,
            binding.ivHead,
            R.mipmap.ic_def_square_img
        )
        binding.masterTv.visibility =
            if (itemCount > 1 || item.starOrderNumStr.isNullOrEmpty()) View.GONE else View.VISIBLE

        binding.bean = item
    }
}