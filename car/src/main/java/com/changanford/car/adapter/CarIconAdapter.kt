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
import com.changanford.common.util.gio.GIOUtils
import com.changanford.common.utilext.GlideUtils
import com.changanford.common.wutil.ScreenUtils


class CarIconAdapter(val activity:Activity): BaseQuickAdapter<NewCarTagBean, BaseDataBindingHolder<ItemCarIconBinding>>(R.layout.item_car_icon){
    private val dp20 by lazy { ScreenUtils.dp2px(context,20f) }
    //当recarDisScale设置的为屏幕的宽度比例时 即 控件宽度=屏幕有效宽度（总宽度-控件之间的间距）*recarDisScale
//    private val imgWidth by lazy {
//        MConstant.configBean?.recarDisScale?.let {widthMultiple ->
//            val multiple:Float= 1/widthMultiple
//            val spacing=(multiple+1)*dp20
//            ((ScreenUtils.getScreenWidth(context)-spacing)/multiple).toInt()
//        }
//    }
    private val imgWidth by lazy {
        MConstant.configBean?.recarDisScale?.let {number ->
            val widthMultiple=if(number!=0f)number else 2f
            val multiple:Int= widthMultiple.toInt()
            val spacing=(multiple+1)*dp20
            ((ScreenUtils.getScreenWidth(context)-spacing)/widthMultiple).toInt()
        }
    }
    @SuppressLint("SetTextI18n")
    override fun convert(holder: BaseDataBindingHolder<ItemCarIconBinding>, item: NewCarTagBean) {
        holder.dataBinding?.apply {
            val position=holder.absoluteAdapterPosition
//            imgCover.load(item.carModelPic)
//            WCommonUtil.setMargin(layoutRoot,if(0==position)dp20 else 0,0,dp20,0)
            layoutRoot.setPadding(if(0==position)dp20 else 0,0,dp20,0)
            GlideUtils.glideLoadWidth(activity,item.carModelPic,imgCover,imgWidth?:ScreenUtils.getScreenWidth(context)/2)
            model=item
            executePendingBindings()
            root.setOnClickListener {
                WBuriedUtil.clickCarEnjoy(item.spuName)
                GIOUtils.carClick(item.spuName,item.spuCode)
                JumpUtils.instans?.jump(item.jumpDataType,item.jumpDataValue)
            }
        }
    }
}