package com.changanford.circle.widget.dialog

import android.app.Dialog
import android.content.Context
import android.view.Gravity
import android.view.LayoutInflater
import androidx.databinding.DataBindingUtil
import com.changanford.circle.R
import com.changanford.circle.databinding.DialogApplicationCircleManagementBinding
import com.changanford.circle.databinding.DialogCircleReplyBinding
import com.changanford.circle.utils.HideKeyboardUtil
import com.changanford.circle.utils.setDialogParams

/**
 *Author lcw
 *Time on 2021/9/22
 *Purpose 申请圈子管理
 */
class ApplicationCircleManagementDialog(
    context: Context,
    themeResId: Int = R.style.StyleCommonDialog
) : Dialog(context, themeResId) {

    private var binding: DialogApplicationCircleManagementBinding = DataBindingUtil.inflate(
        LayoutInflater.from(getContext()),
        R.layout.dialog_application_circle_management, null, false
    )

    init {
        setContentView(binding.root)
        setDialogParams(context, this, Gravity.BOTTOM)
        initView()
        initListener()
    }

    private fun initView(){}

    private fun initListener(){

    }

    override fun dismiss() {
        super.dismiss()
        HideKeyboardUtil.hideKeyboard(binding.llView.windowToken)
    }
}