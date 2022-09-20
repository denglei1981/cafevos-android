package com.changanford.common.widget.pop

import android.content.Context
import android.view.Gravity
import androidx.databinding.DataBindingUtil
import com.changanford.common.R
import com.changanford.common.databinding.PopMineGuideBinding
import razerdp.basepopup.BasePopupWindow

/**
 *Author lcw
 *Time on 2022/9/20
 *Purpose
 */
class MineGuidePop(mContext: Context) : BasePopupWindow(mContext) {
    private var binding: PopMineGuideBinding =
        DataBindingUtil.bind(createPopupById(R.layout.pop_mine_guide))!!

    init {
        contentView = binding.root
        popupGravity = Gravity.CENTER
        initView()
    }

    private fun initView() {
        binding.vClose.setOnClickListener { dismiss() }
    }
}