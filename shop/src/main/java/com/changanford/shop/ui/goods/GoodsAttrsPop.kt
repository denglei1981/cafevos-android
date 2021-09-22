package com.changanford.shop.ui.goods

import android.content.Context
import android.view.animation.Animation
import androidx.databinding.DataBindingUtil
import com.changanford.shop.R
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
    init {
        contentView=viewDataBinding.root
        initView()
    }
    private fun initView(){
        viewDataBinding.imgClose.setOnClickListener { this.dismiss() }
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