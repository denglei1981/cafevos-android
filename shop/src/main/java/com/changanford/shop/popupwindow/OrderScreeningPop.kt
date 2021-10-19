package com.changanford.shop.popupwindow

import android.view.animation.Animation
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.changanford.common.bean.OrderTypeItemBean
import com.changanford.common.bean.OrderTypesBean
import com.changanford.common.util.JumpUtils
import com.changanford.shop.R
import com.changanford.shop.adapter.order.OrderTypeAdapter
import com.changanford.shop.databinding.PopOrderScreeningBinding
import com.changanford.shop.utils.WCommonUtil
import com.changanford.shop.viewmodel.OrderViewModel
import com.google.gson.Gson
import razerdp.basepopup.BasePopupWindow
import razerdp.util.animation.AnimationHelper
import razerdp.util.animation.TranslationConfig
/**
 * @Author : wenke
 * @Time : 2021/9/22
 * @Description :所有订单-筛选
 */
open class OrderScreeningPop(val activity: AppCompatActivity,val viewModel: OrderViewModel): BasePopupWindow(activity) {
    private var viewDataBinding: PopOrderScreeningBinding = DataBindingUtil.bind(createPopupById(R.layout.pop_order_screening))!!
    private var listener: OnSelectListener?=null
    private val mAdapter by lazy { OrderTypeAdapter() }
    init {
        contentView=viewDataBinding.root
        initView()
        initData()
    }
    private fun initView(){
        viewDataBinding.tvCancel.setOnClickListener{this.dismiss()}
        viewDataBinding.recyclerView.adapter=mAdapter
        mAdapter.setOnItemClickListener { _, _, position ->
            mAdapter.data[position].let {
                JumpUtils.instans?.jump(it.jumpDataType,it.jumpDataValue)
            }
            this.dismiss()
        }
    }
    private fun initData(){
        viewModel.orderTypesLiveData.observe(activity,{
            mAdapter.setList(it?:getDefaultData())
        })
        viewModel.getOrderKey()
    }
    private fun getDefaultData():List<OrderTypeItemBean>{
        val jsonStr= WCommonUtil.getAssetsJson("OrderTypes.json", activity)
        return Gson().fromJson(jsonStr, OrderTypesBean::class.java)
    }
    fun show(listener: OnSelectListener?){
        this.listener=listener
        this.showPopupWindow()
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