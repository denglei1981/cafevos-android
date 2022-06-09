package com.changanford.common.ui.dialog

import android.content.Context
import android.view.Gravity
import androidx.appcompat.widget.AppCompatTextView
import com.changanford.common.R


/**
 */
class SelectCoverDialog(context: Context, private val listener: CheckedView) : BaseDialog(context) {

    override fun getLayoutId() = R.layout.select_my_cover_dialog

    init {
        window?.setGravity(Gravity.BOTTOM)
        setParamWidthMatch()
        findViewById<AppCompatTextView>(R.id.tv_cancel).setOnClickListener {
            listener.checkCancel()
            dismiss()
        }
        findViewById<AppCompatTextView>(R.id.tv_cover).setOnClickListener {
            listener.checkGaoDe()
            dismiss()
        }

    }

    interface CheckedView {
        fun checkGaoDe()
        fun checkCancel()
    }
}