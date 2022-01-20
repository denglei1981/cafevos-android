package com.changanford.car.adapter

import android.annotation.SuppressLint
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseDataBindingHolder
import com.changanford.car.R
import com.changanford.car.databinding.ItemCarServiceBinding
import com.changanford.common.bean.NewCarTagBean

class CarServiceAdapter: BaseQuickAdapter<NewCarTagBean, BaseDataBindingHolder<ItemCarServiceBinding>>(R.layout.item_car_service){
    @SuppressLint("SetTextI18n")
    override fun convert(holder: BaseDataBindingHolder<ItemCarServiceBinding>, item: NewCarTagBean) {
        holder.dataBinding?.apply {
            model=item
            executePendingBindings()
        }
    }
}