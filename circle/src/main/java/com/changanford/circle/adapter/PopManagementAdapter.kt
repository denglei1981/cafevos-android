package com.changanford.circle.adapter

import android.content.Context
import androidx.databinding.ViewDataBinding
import com.changanford.circle.R
import com.changanford.circle.bean.CircleStarRoleDto
import com.changanford.circle.databinding.ItemPopManagementBinding
import com.changanford.circle.utils.MUtils
import com.changanford.common.basic.adapter.BaseAdapterOneLayout

/**
 *Author lcw
 *Time on 2021/9/23
 *Purpose
 */
class PopManagementAdapter(context: Context) :
    BaseAdapterOneLayout<CircleStarRoleDto>(context, R.layout.item_pop_management) {
    override fun fillData(vdBinding: ViewDataBinding?, item: CircleStarRoleDto, position: Int) {
        val binding = vdBinding as ItemPopManagementBinding
        MUtils.setTopMargin(binding.llContent, 16, position)
        binding.bean = item
    }
}