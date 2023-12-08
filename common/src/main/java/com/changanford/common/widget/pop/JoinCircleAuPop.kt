package com.changanford.common.widget.pop

import android.view.animation.Animation
import androidx.databinding.DataBindingUtil
import com.changanford.common.R
import com.changanford.common.basic.BaseApplication
import com.changanford.common.databinding.PopJoinCircleAuthenticationBinding
import com.changanford.common.util.JumpUtils
import razerdp.basepopup.BasePopupWindow
import razerdp.util.animation.AnimationHelper
import razerdp.util.animation.TranslationConfig

/**
 *Author lcw
 *Time on 2023/12/8
 *Purpose 加圈车主认证弹窗
 */
class JoinCircleAuPop(private val bean: String) :
    BasePopupWindow(BaseApplication.curActivity) {

    val viewDataBinding: PopJoinCircleAuthenticationBinding? =
        DataBindingUtil.bind(createPopupById(R.layout.pop_join_circle_authentication))

    init {
        contentView = viewDataBinding?.root
        initView()

    }

    private fun initView() {
        viewDataBinding?.apply {
            tvContent.text = bean
            tvAu.setOnClickListener {
                JumpUtils.instans?.jump(17)
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