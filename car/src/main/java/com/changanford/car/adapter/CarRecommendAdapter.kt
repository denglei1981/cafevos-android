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
            .load(item.carModelPic,R.mipmap.ic_def_square_img)
        holder.setText(R.id.carname, item.spuName)
    }
}