package com.changanford.common.widget.pop

import android.view.animation.Animation
import androidx.databinding.DataBindingUtil
import com.changanford.common.R
import com.changanford.common.basic.BaseApplication
import com.changanford.common.databinding.PopFordPaiCircleBinding
import razerdp.basepopup.BasePopupWindow
import razerdp.util.animation.AnimationHelper
import razerdp.util.animation.TranslationConfig

/**
 *Author lcw
 *Time on 2024/2/16
 *Purpose
 */
class FordPaiCirclePop :
    BasePopupWindow(BaseApplication.curActivity) {

    val viewDataBinding: PopFordPaiCircleBinding? =
        DataBindingUtil.bind(createPopupById(R.layout.pop_ford_pai_circle))

    init {
        contentView = viewDataBinding?.root
        initView()

    }

    fun setData(title: String, content: String, bottomContent: String) {
        viewDataBinding?.tvTips?.text = title
        viewDataBinding?.tvContent?.text = content
        viewDataBinding?.tvBottom?.text = bottomContent
    }

    private fun initView() {
        viewDataBinding?.apply {
            tvBottom.setOnClickListener {
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