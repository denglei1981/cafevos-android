package com.changanford.common.widget.pop

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.view.animation.Animation
import androidx.core.content.ContextCompat.startActivity
import androidx.databinding.DataBindingUtil
import com.changanford.common.R
import com.changanford.common.basic.BaseApplication
import com.changanford.common.bean.CmcStatePhoneBean
import com.changanford.common.databinding.PopValidateLogonBinding
import com.changanford.common.utilext.toast
import razerdp.basepopup.BasePopupWindow
import razerdp.util.animation.AnimationHelper
import razerdp.util.animation.TranslationConfig

/**
 *Author lcw
 *Time on 2023/11/6
 *Purpose 登录/注册验证
 */
class ValidateLogonPop(private val bean: CmcStatePhoneBean) :
    BasePopupWindow(BaseApplication.curActivity) {

    val viewDataBinding: PopValidateLogonBinding? =
        DataBindingUtil.bind(createPopupById(R.layout.pop_validate_logon))

    init {
        contentView = viewDataBinding?.root
        initView()

    }

    private fun initView() {
        viewDataBinding?.apply {
            ivClose.setOnClickListener { dismiss() }
            tvCallPhone.setOnClickListener {
                if (bean.LRPhone.isNullOrEmpty()){
                    "电话为空".toast()
                    return@setOnClickListener
                }
                val intent = Intent()
                intent.action = Intent.ACTION_CALL
                val data = Uri.parse("tel:${bean.LRPhone}")
                intent.data = data
                BaseApplication.curActivity.startActivity(intent)
            }
            tvContent.text = bean.LRContent
            tvPhone.text = bean.LRPhone
            tvTime.text = bean.LRTime
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