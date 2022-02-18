package com.changanford.car.adapter

import android.annotation.SuppressLint
import android.app.Activity
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseDataBindingHolder
import com.changanford.car.R
import com.changanford.car.databinding.ItemCarIconBinding
import com.changanford.common.bean.NewCarTagBean
import com.changanford.common.buried.WBuriedUtil
import com.changanford.common.util.JumpUtils
import com.changanford.common.util.MConstant
import com.changanford.common.utilext.GlideUtils
import com.changanford.common.wutil.ScreenUtils


class CarIconAdapter(val activity:Activity): BaseQuickAdapter<NewCarTagBean, BaseDataBindingHolder<ItemCarIconBinding>>(R.layout.item_car_icon){
    private val imgWidth by lazy { (ScreenUtils.getScreenWidth(context)*(MConstant.configBean?.recarDisScale?:0f)).toInt()}
    @SuppressLint("SetTextI18n")
    override fun convert(holder: BaseDataBindingHolder<ItemCarIconBinding>, item: NewCarTagBean) {
        holder.dataBinding?.apply {
//            imgCover.load(item.carModelPic)
            GlideUtils.glideLoadWidth(activity,item.carModelPic,imgCover,imgWidth)
            model=item
            executePendingBindings()
            root.setOnClickListener {
                WBuriedUtil.clickCarEnjoy(item.spuName)
                JumpUtils.instans?.jump(item.jumpDataType,item.jumpDataValue)
            }
        }
    }
}