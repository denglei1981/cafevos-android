package com.changanford.car.adapter

import android.annotation.SuppressLint
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseDataBindingHolder
import com.changanford.car.R
import com.changanford.car.databinding.ItemCarServiceBinding
import com.changanford.common.bean.NewCarTagBean
import com.changanford.common.buried.WBuriedUtil
import com.changanford.common.util.JumpUtils
import com.changanford.common.util.gio.GIOUtils
import com.changanford.common.utilext.load

class CarServiceAdapter: BaseQuickAdapter<NewCarTagBean, BaseDataBindingHolder<ItemCarServiceBinding>>(R.layout.item_car_service){
    @SuppressLint("SetTextI18n")
    override fun convert(holder: BaseDataBindingHolder<ItemCarServiceBinding>, item: NewCarTagBean) {
        holder.dataBinding?.apply {
            imgCover.load(item.iconImg)
            model=item
            executePendingBindings()
            root.setOnClickListener {
                WBuriedUtil.clickCarBuyService(item.iconName)
                GIOUtils.homePageClick(
                    "购车服务",
                    (holder.adapterPosition + 1).toString(),
                    item.iconName
                )
                JumpUtils.instans?.jump(item.jumpDataType,item.jumpDataValue)
            }
        }
    }
}