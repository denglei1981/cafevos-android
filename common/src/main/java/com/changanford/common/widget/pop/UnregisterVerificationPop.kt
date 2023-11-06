package com.changanford.common.widget.pop

import android.content.Context
import android.view.animation.Animation
import androidx.databinding.DataBindingUtil
import com.changanford.common.R
import com.changanford.common.databinding.PopUnregisterVerificationBinding
import razerdp.basepopup.BasePopupWindow
import razerdp.util.animation.AnimationHelper
import razerdp.util.animation.TranslationConfig

/**
 *Author lcw
 *Time on 2023/11/6
 *Purpose 注销验证
 */
class UnregisterVerificationPop(
    context: Context,
    private val msg: String,
    private val block: () -> Unit
) : BasePopupWindow(context) {

    val viewDataBinding: PopUnregisterVerificationBinding? =
        DataBindingUtil.bind(createPopupById(R.layout.pop_unregister_verification))

    init {
        contentView = viewDataBinding?.root
        initView()

    }

    private fun initView() {
        viewDataBinding?.apply {
            tvContent.text = msg
            tvCancel.setOnClickListener { dismiss() }
            tvUnregister.setOnClickListener {
                block.invoke()
                dismiss()
            }
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