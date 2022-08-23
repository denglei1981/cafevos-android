package com.changanford.common.ui.dialog

import android.content.Context
import android.view.Gravity
import androidx.appcompat.widget.AppCompatTextView
import com.changanford.common.R


/**
 * 底部选择
 */
class BottomSelectDialog(context: Context, save:()->Unit = {},notSave:()->Unit = {}) : BaseDialog(context) {

    override fun getLayoutId() = R.layout.dialog_bottom_choose

    init {
        window?.setGravity(Gravity.BOTTOM)
        setParamWidthMatch()
        findViewById<AppCompatTextView>(R.id.notsave).setOnClickListener {
            dismiss()
        }
        findViewById<AppCompatTextView>(R.id.save).setOnClickListener {
            save()
            dismiss()
        }
        findViewById<AppCompatTextView>(R.id.tv_default).setOnClickListener {
            notSave()
            dismiss()
        }
    }
}