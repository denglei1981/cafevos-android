package com.changanford.circle.widget.dialog

import android.app.Dialog
import android.content.Context
import android.view.Gravity
import android.view.LayoutInflater
import androidx.databinding.DataBindingUtil
import com.changanford.circle.R
import com.changanford.circle.databinding.DialogCircleReplyBinding
import com.changanford.circle.utils.HideKeyboardUtil
import com.changanford.circle.utils.setDialogParams
import com.changanford.common.util.MineUtils
import com.changanford.common.utilext.toast

/**
 *Author lcw
 *Time on 2021/9/18
 *Purpose 回复弹窗
 */
class ReplyDialog(
    context: Context,
    private val listener: ReplyListener,
    themeResId: Int = R.style.StyleCommonDialog
) :
    Dialog(context, themeResId) {

    private var binding: DialogCircleReplyBinding = DataBindingUtil.inflate(
        LayoutInflater.from(getContext()),
        R.layout.dialog_circle_reply, null, false
    )

    init {
        setContentView(binding.root)
        setDialogParams(context, this, Gravity.BOTTOM)
        initView()
        initListener()
        HideKeyboardUtil.showSoftInput(binding.etContent)
    }

    private fun initView() {
        binding.tvSend.setOnClickListener {
            if (getEditContent().length < 3) {
                "至少输入3个字".toast()
                return@setOnClickListener
            }
            listener.getContent(getEditContent())
            if(!MineUtils.getBindMobileJumpDataType(true)){
                dismiss()
            }
        }
    }

    private fun initListener() {

    }

    private fun getEditContent(): String {
        return binding.etContent.text.toString()
    }

    override fun dismiss() {
        super.dismiss()
        HideKeyboardUtil.hideKeyboard(binding.etContent.windowToken)
    }

    interface ReplyListener {
        fun getContent(content: String)
    }
}