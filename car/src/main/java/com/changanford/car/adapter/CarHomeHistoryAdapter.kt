package com.changanford.car.adapter

import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseDataBindingHolder
import com.changanford.car.R
import com.changanford.car.databinding.ItemHomeCarHistoryBinding

/**
 *Author lcw
 *Time on 2024/1/8
 *Purpose
 */
class CarHomeHistoryAdapter :
    BaseQuickAdapter<String, BaseDataBindingHolder<ItemHomeCarHistoryBinding>>(R.layout.item_home_car_history) {

    override fun convert(holder: BaseDataBindingHolder<ItemHomeCarHistoryBinding>, item: String) {
        val picAdapter = CarHomePicAdapter()
        holder.dataBinding?.let {

        }
    }

}