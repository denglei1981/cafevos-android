package com.changanford.common.ui

import android.content.Context
import android.view.animation.Animation
import androidx.databinding.DataBindingUtil
import com.changanford.common.R
import com.changanford.common.bean.WindowMsg
import com.changanford.common.databinding.PopUpdateAgreeBinding
import com.changanford.common.util.MineUtils
import razerdp.basepopup.BasePopupWindow
import razerdp.util.animation.AnimationHelper
import razerdp.util.animation.TranslationConfig

/**
 *Author lcw
 *Time on 2023/2/28
 *Purpose
 */
class UpdateAgreePop(
    context: Context,
    private val windowMsg: WindowMsg,
    private val listener: UpdateAgreePopListener
) :
    BasePopupWindow(context) {

    val viewDataBinding: PopUpdateAgreeBinding? =
        DataBindingUtil.bind(createPopupById(R.layout.pop_update_agree))

    init {
        contentView = viewDataBinding?.root
        isOutSideTouchable = false
        initView()

    }

    override fun onBackPressed(): Boolean {
        return false
    }

    private fun initView() {
        viewDataBinding?.let {
            it.tvTitle.text = windowMsg.msgTitle
            MineUtils.popUpdateAgree(it.tvContent, windowMsg.overMsg.toString(), windowMsg.code)
//            it.tvContent.text = windowMsg.overMsg
            it.tvCancel.setOnClickListener { listener.clickCancel() }
            it.tvSure.setOnClickListener {
                listener.clickSure()
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

    interface UpdateAgreePopListener {
        fun clickCancel()
        fun clickSure()
    }
}