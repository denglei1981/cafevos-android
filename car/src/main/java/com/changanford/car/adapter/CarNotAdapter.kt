package com.changanford.car.adapter

import android.annotation.SuppressLint
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseDataBindingHolder
import com.changanford.car.R
import com.changanford.car.databinding.ItemCarBannerBinding
import com.changanford.car.databinding.ItemCarPaddingBinding
import com.changanford.common.basic.BaseApplication
import com.changanford.common.utilext.toIntPx
import com.gyf.immersionbar.ImmersionBar


class CarNotAdapter :
    BaseQuickAdapter<String, BaseDataBindingHolder<ItemCarPaddingBinding>>(R.layout.item_car_padding) {
    @SuppressLint("SetTextI18n")
    override fun convert(holder: BaseDataBindingHolder<ItemCarPaddingBinding>, item: String) {
        val paddingTop = ImmersionBar.getStatusBarHeight(BaseApplication.curActivity)
        val layoutParams = holder.dataBinding?.vLine?.layoutParams
        layoutParams?.height = paddingTop + 70.toIntPx()
        holder.dataBinding?.vLine?.layoutParams = layoutParams
    }
}