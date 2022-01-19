package com.changanford.car.adapter

import android.annotation.SuppressLint
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseDataBindingHolder
import com.changanford.car.R
import com.changanford.car.databinding.ItemCarBannerBinding


class CarNotAdapter: BaseQuickAdapter<String, BaseDataBindingHolder<ItemCarBannerBinding>>(R.layout.item_car_banner){
    @SuppressLint("SetTextI18n")
    override fun convert(holder: BaseDataBindingHolder<ItemCarBannerBinding>, item: String) {
    }
}