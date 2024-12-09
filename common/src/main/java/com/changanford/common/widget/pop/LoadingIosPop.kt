package com.changanford.common.widget.pop

import android.graphics.drawable.AnimationDrawable
import android.view.Gravity
import android.view.animation.Animation
import androidx.databinding.DataBindingUtil
import com.changanford.common.R
import com.changanford.common.basic.BaseApplication
import com.changanford.common.databinding.PopLoadingIosLayoutBinding
import razerdp.basepopup.BasePopupWindow
import razerdp.util.animation.AnimationHelper
import razerdp.util.animation.TranslationConfig


/**
 * @author: niubobo
 * @date: 2024/12/5
 * @description：跟ios一样的loading帧动画
 */
class LoadingIosPop :
    BasePopupWindow(BaseApplication.curActivity) {

    val viewDataBinding: PopLoadingIosLayoutBinding? =
        DataBindingUtil.bind(createPopupById(R.layout.pop_loading_ios_layout))

    init {
        contentView = viewDataBinding?.root
        initView()
        popupGravity = Gravity.CENTER
    }

    private fun initView() {
        viewDataBinding?.apply {
            ivLoading.setBackgroundResource(R.drawable.anim_loading_ios)
            val animationDrawable = ivLoading.background as AnimationDrawable
            animationDrawable.start()
        }
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