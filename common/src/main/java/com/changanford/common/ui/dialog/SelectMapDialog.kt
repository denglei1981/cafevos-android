package com.changanford.common.ui.dialog

import android.content.Context
import android.view.Gravity
import androidx.appcompat.widget.AppCompatTextView
import com.changanford.common.R


/**
 * @Author: lw
 * @Date: 2020/11/18
 * @Des:地图选择
 */
class SelectMapDialog(context: Context, private val listener: CheckedView) : BaseDialog(context) {

    override fun getLayoutId() = R.layout.select_map_dialog

    init {
        window?.setGravity(Gravity.BOTTOM)
        setParamWidthMatch()
        findViewById<AppCompatTextView>(R.id.tv_cancel).setOnClickListener {
            listener.checkCancel()
            dismiss()
        }
        findViewById<AppCompatTextView>(R.id.tv_gd_map).setOnClickListener {
            listener.checkGaoDe()
            dismiss()
        }
        findViewById<AppCompatTextView>(R.id.tv_bd_map).setOnClickListener {
            listener.checkBaiDu()
            dismiss()
        }
    }

    interface CheckedView {
        fun checkBaiDu()
        fun checkGaoDe()
        fun checkCancel()
    }
}