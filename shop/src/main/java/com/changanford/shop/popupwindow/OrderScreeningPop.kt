package com.changanford.shop.popupwindow

import android.content.Context
import android.view.View
import android.view.animation.Animation
import androidx.databinding.DataBindingUtil
import com.changanford.shop.R
import com.changanford.shop.databinding.PopOrderScreeningBinding
import razerdp.basepopup.BasePopupWindow
import razerdp.util.animation.AnimationHelper
import razerdp.util.animation.TranslationConfig
/**
 * @Author : wenke
 * @Time : 2021/9/22
 * @Description :所有订单-筛选
 */
open class OrderScreeningPop(context: Context?): BasePopupWindow(context), View.OnClickListener {
    private var viewDataBinding: PopOrderScreeningBinding = DataBindingUtil.bind(createPopupById(R.layout.pop_order_screening))!!
    private var listener: OnSelectListener?=null
    init {
        contentView=viewDataBinding.root
        initView()
    }
    private fun initView(){
        viewDataBinding.tvCancel.setOnClickListener(this)
        viewDataBinding.tvOrderGoods.setOnClickListener(this)
        viewDataBinding.tvOrderCar.setOnClickListener(this)
        viewDataBinding.tvOrderTestDrive.setOnClickListener(this)
    }
    fun show(listener: OnSelectListener?){
        this.listener=listener
        this.showPopupWindow()
    }
    override fun onClick(v: View?) {
        when(v?.id){
            //0 商品订单
            R.id.tv_order_goods->listener?.onSelectBackListener(0)
            //1 购车订单
            R.id.tv_order_car->listener?.onSelectBackListener(1)
            //2 试驾订单
            R.id.tv_order_testDrive->listener?.onSelectBackListener(2)
        }
        this.dismiss()
    }
    interface OnSelectListener {
        fun onSelectBackListener(type:Int)
    }
    override fun onCreateShowAnimation(): Animation? {
        return AnimationHelper.asAnimation()
            .withTranslation(TranslationConfig.FROM_BOTTOM)
            .toShow()
    }
    override fun onCreateDismissAnimation(): Animation? {
        return AnimationHelper.asAnimation()
            .withTranslation(TranslationConfig.TO_BOTTOM)
            .toDismiss()
    }

}