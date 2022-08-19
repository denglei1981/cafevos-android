package com.changanford.common.ui.dialog

import android.content.Context
import android.view.Gravity
import androidx.appcompat.widget.AppCompatTextView
import com.changanford.common.R


/**
 * @Author: lw
 * @Date: 2021/8/18
 * @Des:图片选择
 */
class SelectPicDialog(context: Context, private val listener: ChoosePicListener) : BaseDialog(context) {

    override fun getLayoutId() = R.layout.choose_pic_dialog

    init {
        window?.setGravity(Gravity.BOTTOM)
        setParamWidthMatch()
        findViewById<AppCompatTextView>(R.id.tv_cancel).setOnClickListener {
            dismiss()
        }
        findViewById<AppCompatTextView>(R.id.tv_phone).setOnClickListener {
            listener.chooseByPhone()
            dismiss()
        }
        findViewById<AppCompatTextView>(R.id.tv_default).setOnClickListener {
            listener.chooseByDefault()
            dismiss()
        }
    }

    interface ChoosePicListener {
        fun chooseByPhone()
        fun chooseByDefault()
    }
}