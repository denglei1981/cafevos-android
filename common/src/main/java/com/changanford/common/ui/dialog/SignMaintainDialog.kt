package com.changanford.common.ui.dialog

import android.content.Context
import com.changanford.common.databinding.DialogSignMaintainBinding

class SignMaintainDialog(context: Context): com.changanford.common.basic.BaseDialog<DialogSignMaintainBinding>(context) {
    override fun initView() {
        binding.textView4.setOnClickListener {
            dismiss()
        }
    }

    override fun initData() {

    }
}