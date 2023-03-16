package com.changanford.common.ui

import android.content.Context
import android.view.animation.Animation
import androidx.databinding.DataBindingUtil
import com.changanford.common.R
import com.changanford.common.bean.NewEstOneBean
import com.changanford.common.bean.NewEstOneItemBean
import com.changanford.common.databinding.PopNewEstOneBinding
import com.changanford.common.util.JumpUtils
import com.changanford.common.utilext.GlideUtils
import razerdp.basepopup.BasePopupWindow
import razerdp.util.animation.AnimationHelper
import razerdp.util.animation.TranslationConfig

/**
 *Author lcw
 *Time on 2023/1/10
 *Purpose
 */
class NewEstOnePop(context: Context, private val estOneBean: NewEstOneItemBean) :
    BasePopupWindow(context) {

    val viewDataBinding: PopNewEstOneBinding? =
        DataBindingUtil.bind(createPopupById(R.layout.pop_new_est_one))

    init {
        contentView = viewDataBinding?.root
        initView()

    }

    private fun initView() {
        estOneBean?.let {data->
            viewDataBinding?.let {
                GlideUtils.loadBD(data.adImg, viewDataBinding.ivBg)
                it.ivClose.setOnClickListener { dismiss() }
                it.ivBg.setOnClickListener {
                    JumpUtils.instans?.jump(data.jumpDataType, data.jumpDataValue)
                    dismiss()
                }
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