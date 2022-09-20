package com.changanford.common.widget.pop

import android.content.Context
import android.view.Gravity
import androidx.databinding.DataBindingUtil
import com.changanford.common.R
import com.changanford.common.databinding.PopHomeGuideBinding
import razerdp.basepopup.BasePopupWindow

/**
 *Author lcw
 *Time on 2022/9/20
 *Purpose
 */
class HomeGuidePop(mContext: Context) : BasePopupWindow(mContext) {

    private var binding: PopHomeGuideBinding =
        DataBindingUtil.bind(createPopupById(R.layout.pop_home_guide))!!

    init {
        contentView = binding.root
        popupGravity = Gravity.TOP or Gravity.RIGHT
        initView()
    }

    private fun initView() {
        binding.vClose.setOnClickListener {
            dismiss()
        }
    }
}