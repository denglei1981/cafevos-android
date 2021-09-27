package com.changanford.common.ui

import android.content.Context
import android.widget.TextView
import androidx.databinding.DataBindingUtil
import com.changanford.common.R
import com.changanford.common.databinding.PopConfirmBinding
import razerdp.basepopup.BasePopupWindow

/**
 *  文件名：ConfirmPop
 *  创建者: zcy
 *  创建日期：2020/5/15 17:15
 *  描述: 确认弹框
 *  修改描述：TODO
 */

class ConfirmPop(context: Context) : BasePopupWindow(context) {

    var contentText: TextView
    var cancelBtn: TextView
    var submitBtn: TextView
    var title: TextView
    init {
        var viewDataBinding:PopConfirmBinding = DataBindingUtil.bind(createPopupById(R.layout.pop_confirm))!!
        contentView=viewDataBinding.root
        contentText = viewDataBinding.content
        cancelBtn = viewDataBinding.cancel
        submitBtn = viewDataBinding.submit
        title = viewDataBinding.tvTitle
        cancelBtn.setOnClickListener {
            dismiss()
        }
    }

    override fun onBackPressed(): Boolean {
        return false
    }
}