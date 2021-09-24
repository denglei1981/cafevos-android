package com.changanford.shop.popupwindow

import android.content.Context
import android.view.animation.Animation
import androidx.databinding.DataBindingUtil
import com.changanford.shop.R
import com.changanford.shop.adapter.goods.GoodsAttributeAdapter
import com.changanford.shop.databinding.PopGoodsSelectattributeBinding
import razerdp.basepopup.BasePopupWindow
import razerdp.util.animation.AnimationHelper
import razerdp.util.animation.TranslationConfig
/**
 * @Author : wenke
 * @Time : 2021/9/22
 * @Description : GoodsAttrsPop
 */
open class GoodsAttrsPop(context: Context?): BasePopupWindow(context) {
    private var viewDataBinding: PopGoodsSelectattributeBinding = DataBindingUtil.bind(createPopupById(R.layout.pop_goods_selectattribute))!!
    private val colorAdapter by lazy { GoodsAttributeAdapter(0) }
    private val specificationAdapter by lazy { GoodsAttributeAdapter(0) }
    init {
        contentView=viewDataBinding.root
        initView()
        initData()
    }
    private fun initView(){
        viewDataBinding.imgClose.setOnClickListener { this.dismiss() }
        viewDataBinding.rvColor.adapter=colorAdapter
        viewDataBinding.rvSpecifications.adapter=specificationAdapter
    }
    private fun initData(){
        colorAdapter.setList(arrayListOf("红色","黑色","蓝色"))
        specificationAdapter.setList(arrayListOf("64G","128G","512G"))
    }
    //动画
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