package com.changanford.common.widget

import android.content.Context
import android.widget.TextView
import com.changanford.common.R
import com.changanford.common.basic.BaseDialog
import com.changanford.common.databinding.DialogBindingPhoneBinding
import com.changanford.common.router.path.ARouterMyPath
import com.changanford.common.router.startARouter

/**
 * @Des:
 */
class BindingPhoneDialog(context: Context) : BaseDialog<DialogBindingPhoneBinding>(context) {

    override fun initView() {
        var whith = context.resources.displayMetrics.widthPixels * 0.8f
        window?.attributes?.width = whith.toInt()
        findViewById<TextView>(R.id.cancel_tv)?.setOnClickListener { dismiss() }
        findViewById<TextView>(R.id.go_tv)?.setOnClickListener {
            dismiss()
            startARouter(ARouterMyPath.MineBindMobileUI)
        }
    }

    override fun initData() {
    }
}