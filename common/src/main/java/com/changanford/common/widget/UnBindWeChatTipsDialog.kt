package com.changanford.common.widget

import android.content.Context
import android.view.Gravity
import androidx.core.view.isVisible
import androidx.databinding.DataBindingUtil
import com.changanford.common.R
import com.changanford.common.databinding.DialogUnbindWechatTipsBinding
import razerdp.basepopup.BasePopupWindow

class UnBindWeChatTipsDialog(val context: Context) : BasePopupWindow(context) {
    private var binding: DialogUnbindWechatTipsBinding =
        DataBindingUtil.bind(createPopupById(R.layout.dialog_unbind_wechat_tips))!!

    init {
        contentView = binding.root
        popupGravity = Gravity.CENTER
        initView()
    }

    fun setContent(int: Int = 2): UnBindWeChatTipsDialog {
        when (int) {
            1 -> {
                binding.title.text = "您已取消微信登录授权，是否保留当前账号的头像和昵称？"
                binding.textContent.text = "（若不保留，头像和昵称重置为APP默认样式）"
                binding.layout1.isVisible = true
                binding.layout2.isVisible = false
            }
            2 -> {
                binding.title.text = "您正在解绑微信，是否保留当前账号的头像和昵称？"
                binding.textContent.text = "（若不保留，头像和昵称重置为APP默认样式）"
                binding.layout1.isVisible = false
                binding.layout2.isVisible = true
            }
        }
        return this
    }
    var clickNeg:()->Unit = {}
    var clickPos:()->Unit = {}
    fun setClickListener(clickNeg:()->Unit ,clickPos:()->Unit) :UnBindWeChatTipsDialog{
        this.clickPos = clickPos
        this.clickNeg = clickNeg
        return this
    }

    private fun initView() {
        binding.btnCancel.setOnClickListener {
            dismiss()
        }
        binding.btnStay1.setOnClickListener {
            clickPos()
            dismiss()
        }
        binding.btnUnstay1.setOnClickListener {
            clickNeg()
            dismiss()
        }
        binding.btnStay2.setOnClickListener {
            clickPos()
            dismiss()
        }
        binding.btnUnstay2.setOnClickListener {
            clickNeg()
            dismiss()
        }
    }
}