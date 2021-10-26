package com.changanford.common.ui.dialog

import android.content.Context
import android.view.Gravity
import android.widget.TextView
import com.changanford.common.R
import com.changanford.common.util.JumpUtils
import com.luck.picture.lib.tools.ScreenUtils

/**
 * 三方绑定弹窗 发帖需要弹
 */
class BindDialog(context: Context) :BaseDialog(context) {
    override fun getLayoutId(): Int {
        return R.layout.binddialog
    }

    init {
        window?.setGravity(Gravity.CENTER)
        window?.attributes?.width = ScreenUtils.getScreenWidth(context)*5/6
        findViewById<TextView>(R.id.tv_cancel).setOnClickListener { dismiss() }
        findViewById<TextView>(R.id.tv_bind).setOnClickListener {
            JumpUtils.instans?.jump(18)
            dismiss()
        }
    }



}