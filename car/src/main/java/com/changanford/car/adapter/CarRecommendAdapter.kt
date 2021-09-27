package com.changanford.car.adapter

import android.widget.ImageView
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.changanford.car.R
import com.changanford.common.bean.CarModels
import com.changanford.common.utilext.load

class CarRecommendAdapter :
    BaseQuickAdapter<CarModels, BaseViewHolder>(
        R.layout.item_car_recommend
    ) {
    override fun convert(holder: BaseViewHolder, item: CarModels) {
        holder.getView<ImageView>(R.id.carimg)
            .load("uni-stars-manager/2021/09/22/a07c2ee4aaec45a5a212211f1e9f79b7.png")
        holder.setText(R.id.carname, "车型$")
    }
}