package com.changanford.my.widget

import android.app.Activity
import android.view.animation.Animation
import androidx.databinding.DataBindingUtil
import com.changanford.my.R
import com.changanford.my.databinding.PopDeleteCarBinding
import razerdp.basepopup.BasePopupWindow
import razerdp.util.animation.AnimationHelper
import razerdp.util.animation.TranslationConfig

class DeleteCarPop(
    val fragment: Activity,
    private val listener: deleteCar,
    val tips: String,
) : BasePopupWindow(fragment) {
    val viewDataBinding: PopDeleteCarBinding =
        DataBindingUtil.bind(createPopupById(R.layout.pop_delete_car))!!

    init {
        contentView = viewDataBinding.root
        initView()
    }

    private fun initView() {
        viewDataBinding.apply {
            textContent.text = tips
            btnCancel.setOnClickListener {
                listener.cancle()
                dismiss()
            }
            btnComfir.setOnClickListener {
                listener.delete()
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

    interface deleteCar {
        fun cancle()
        fun delete()
    }
}