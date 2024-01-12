package com.changanford.car.adapter

import androidx.core.view.isVisible
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseDataBindingHolder
import com.changanford.car.R
import com.changanford.car.databinding.ItemHomeCarHistoryBinding
import com.changanford.common.bean.InfoDataBean
import com.changanford.common.bean.PostDataBean
import com.changanford.common.utilext.GlideUtils

/**
 *Author lcw
 *Time on 2024/1/8
 *Purpose
 */
class CarHomeTipsAdapter :
    BaseQuickAdapter<InfoDataBean, BaseDataBindingHolder<ItemHomeCarHistoryBinding>>(R.layout.item_home_car_history) {

    override fun convert(
        holder: BaseDataBindingHolder<ItemHomeCarHistoryBinding>,
        item: InfoDataBean
    ) {
        val picAdapter = CarHomePicAdapter()
        holder.dataBinding?.let {
            if (!item.pics.isNullOrEmpty()) {
                val layoutManager = LinearLayoutManager(context)
                it.ryPc.layoutManager = layoutManager

                it.ryPc.adapter = picAdapter
                picAdapter.setList(listOf(item.pics))
                it.ryPc.isVisible = true

                GlideUtils.loadCircle(item.authors?.avatar, it.ivIcon)
                it.tvName.text = item.authors?.nickname
                it.tvTime.text = item.timeStr
                it.tvContent.text = item.title
            } else {
                it.ryPc.isVisible = false
            }
        }
    }

}