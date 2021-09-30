package com.changanford.circle.ui.activity

import com.alibaba.android.arouter.facade.annotation.Route
import com.changanford.circle.databinding.ChooseconversationBinding
import com.changanford.common.basic.BaseActivity
import com.changanford.common.basic.EmptyViewModel
import com.changanford.common.router.path.ARouterCirclePath
import com.changanford.common.util.AppUtils

/**
 * 选择话题
 */
@Route(path = ARouterCirclePath.ChooseConversationActivity)
class ChooseConversationActivity : BaseActivity<ChooseconversationBinding, EmptyViewModel>() {
    override fun initView() {
        AppUtils.setStatusBarPaddingTop(binding.title.commTitleBar,this)
        binding.title.barTvTitle.text = "选择话题"
    }

    override fun initData() {
    }
}