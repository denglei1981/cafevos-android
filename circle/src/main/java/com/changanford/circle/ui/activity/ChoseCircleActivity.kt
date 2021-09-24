package com.changanford.circle.ui.activity

import com.changanford.circle.databinding.ChooseCircleBinding
import com.changanford.common.basic.BaseActivity
import com.changanford.common.basic.EmptyViewModel
import com.changanford.common.util.AppUtils


class ChoseCircleActivity: BaseActivity<ChooseCircleBinding, EmptyViewModel>() {
    override fun initView() {
        AppUtils.setStatusBarPaddingTop(binding.title.commTitleBar,this)

    }

    override fun initData() {
    }
}