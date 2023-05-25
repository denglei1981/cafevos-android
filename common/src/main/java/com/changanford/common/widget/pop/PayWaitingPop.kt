package com.changanford.common.widget.pop

import android.content.Context
import android.graphics.drawable.AnimationDrawable
import androidx.databinding.DataBindingUtil
import com.changanford.common.R
import com.changanford.common.databinding.PopPayWaitingBinding
import razerdp.basepopup.BasePopupWindow

/**
 *Author lcw
 *Time on 2023/4/11
 *Purpose
 */
class PayWaitingPop(context: Context) : BasePopupWindow(context) {
    private var viewBinding: PopPayWaitingBinding =
        DataBindingUtil.bind(createPopupById(R.layout.pop_pay_waiting))!!

    init {
        contentView = viewBinding.root
        initView()
        isOutSideTouchable = false
    }

    private fun initView() {
        val anim = viewBinding.ivAnim.drawable as AnimationDrawable
        anim.start()
    }

    fun setContent(content: String) {
        viewBinding.tvHint.text = content
    }

    override fun onBackPressed(): Boolean {
        return false
    }
}