package com.changanford.common.ui

import android.content.Context
import android.view.animation.Animation
import androidx.databinding.DataBindingUtil
import com.changanford.common.R
import com.changanford.common.databinding.PopNewEstOneBinding
import com.changanford.common.router.path.ARouterCirclePath
import com.changanford.common.router.startARouter
import razerdp.basepopup.BasePopupWindow
import razerdp.util.animation.AnimationHelper
import razerdp.util.animation.TranslationConfig

/**
 *Author lcw
 *Time on 2023/1/10
 *Purpose
 */
class HoldCirclePop(context: Context) :
    BasePopupWindow(context) {

    val viewDataBinding: PopNewEstOneBinding? =
        DataBindingUtil.bind(createPopupById(R.layout.pop_new_est_one))

    init {
        contentView = viewDataBinding?.root
        initView()

    }

    private fun initView() {
        viewDataBinding?.let {
            viewDataBinding.ivBg.adjustViewBounds = true
            viewDataBinding.ivBg.setImageResource(R.mipmap.ic_hold_circle_pop)
            it.ivClose.setOnClickListener { dismiss() }
            it.ivBg.setOnClickListener {
                startARouter(ARouterCirclePath.HoldCircleActivity)
                dismiss()
            }
        }

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