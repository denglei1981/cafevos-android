package com.changanford.home.widget

import android.app.Dialog
import android.content.Context
import android.view.Gravity
import android.view.LayoutInflater
import androidx.databinding.DataBindingUtil
import com.changanford.common.util.HideKeyboardUtil
import com.changanford.common.util.setDialogParams

import com.changanford.common.utilext.toast
import com.changanford.home.R
import com.changanford.home.databinding.DialogNewsReplyBinding

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

    private var binding: DialogNewsReplyBinding = DataBindingUtil.inflate(
        LayoutInflater.from(getContext()),
        R.layout.dialog_news_reply, null, false
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
            dismiss()
        }
    }

    private fun initListener() {

    }

    private fun getEditContent(): String {
        return binding.etContent.text.toString()
    }

    override fun dismiss() {
        HideKeyboardUtil.hideKeyboard(binding.etContent.windowToken)
        super.dismiss()

    }

    interface ReplyListener {
        fun getContent(content: String)
    }
}