package com.changanford.common.util

import android.view.Gravity
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.changanford.common.ui.ConfirmPop

fun showAppPrivacy(context: AppCompatActivity, block: () -> Unit) {
    var pop = ConfirmPop(context)
    MineUtils.popAgreement(pop.contentText)
    pop.title.visibility = View.VISIBLE
    pop.contentText.gravity = Gravity.LEFT
    pop.contentText.setLineSpacing(2f, 1f)
    pop.submitBtn.text = "同意"
    pop.submitBtn.setOnClickListener {
        SPUtils.setParam(context, "isPopAgreement", false)
//        MyApplicationUtil.init()
        block.invoke()
        pop.dismiss()
    }
    pop.cancelBtn.text = "暂不使用"
    pop.cancelBtn.setOnClickListener {
        pop.dismiss()
        context.finish()
    }
//    pop.isAllowDismissWhenTouchOutside = false
    pop.showPopupWindow()
}