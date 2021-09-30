package com.changanford.shop.base

import android.content.Context
import android.view.Gravity
import android.view.animation.Animation
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import razerdp.basepopup.BasePopupFlag
import razerdp.basepopup.BasePopupWindow
import razerdp.util.animation.AnimationHelper
import razerdp.util.animation.TranslationConfig







/**
* @Author : wenke
* @Time : 2021/9/22 0022
* @Description : BasePopup
*/
abstract class BasePopup<V : ViewDataBinding>(context: Context?) :
    BasePopupWindow(context) {
    protected lateinit var viewDataBinding: V
    init {
        onCreateContentView()
    }
    private fun onCreateContentView(){
        viewDataBinding = DataBindingUtil.bind(createPopupById(getLayoutId()))!!
        contentView=viewDataBinding.root
    }

    fun showBottom() {
        this.setPopupGravity(Gravity.BOTTOM).setOverlayNavigationBar(false).setOutSideDismiss(false)
            .showPopupWindow()
    }

    fun showCenter() {
        this.setPopupGravity(Gravity.CENTER)
            .setBackPressEnable(false)
            .setOutSideDismiss(false)
            .setOverlayNavigationBar(true)
            .setOverlayNavigationBarMode(BasePopupFlag.OVERLAY_CONTENT or BasePopupFlag.OVERLAY_MASK)
            .showPopupWindow()
    }
    abstract fun getLayoutId(): Int
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