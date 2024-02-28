package com.changanford.common.widget.pop

import android.view.animation.Animation
import androidx.core.view.isVisible
import androidx.databinding.DataBindingUtil
import com.changanford.common.R
import com.changanford.common.basic.BaseApplication
import com.changanford.common.databinding.PopJoinCircleAuthenticationBinding
import razerdp.basepopup.BasePopupWindow
import razerdp.util.animation.AnimationHelper
import razerdp.util.animation.TranslationConfig

/**
 *Author lcw
 *Time on 2023/12/8
 *Purpose 福特派温馨提示弹窗
 */
class FordTipsPop(
    private val content: String,
    private val bottomContent: String,
    private val isShowBottomTwo: Boolean
) :
    BasePopupWindow(BaseApplication.curActivity) {

    val viewDataBinding: PopJoinCircleAuthenticationBinding? =
        DataBindingUtil.bind(createPopupById(R.layout.pop_join_circle_authentication))

    init {
        contentView = viewDataBinding?.root
        initView()

    }

    private fun initView() {
        viewDataBinding?.apply {
            tvContent.text = content
            tvAu.text = bottomContent
            tvNoAu.isVisible = isShowBottomTwo
            tvAu.setOnClickListener {
                dismiss()
            }
            tvNoAu.setOnClickListener {
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