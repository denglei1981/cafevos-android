package com.changanford.circle.widget.dialog

import android.content.Context
import android.view.Gravity
import android.widget.TextView
import com.changanford.circle.R
import com.changanford.common.ui.dialog.BaseDialog

/**
 * @Des: 退去圈子提示框
 */
class QuitCircleDialog(context: Context, quit: () -> Unit) : BaseDialog(context) {

    override fun getLayoutId(): Int {
        return R.layout.dialog_quit_circle
    }

    init {
        window?.setGravity(Gravity.CENTER)
        setParamWidthMatch()
        findViewById<TextView>(R.id.miss_tv).setOnClickListener { dismiss() }
        findViewById<TextView>(R.id.quit_tv).setOnClickListener {
            quit()
            dismiss()
        }
    }

}