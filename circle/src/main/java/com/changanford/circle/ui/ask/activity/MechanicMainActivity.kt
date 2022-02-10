package com.changanford.circle.ui.ask.activity


import android.view.View
import com.alibaba.android.arouter.facade.annotation.Route
import com.changanford.circle.databinding.ActivityMechainicMainBinding
import com.changanford.circle.ui.ask.request.MechanicMainViewModel
import com.changanford.circle.ui.ask.request.QuestionViewModel
import com.changanford.common.basic.BaseActivity
import com.changanford.common.router.path.ARouterCirclePath
import com.changanford.common.utilext.StatusBarUtil


// 技师个人主页。
@Route(path = ARouterCirclePath.MechanicMainActivity)
class MechanicMainActivity: BaseActivity<ActivityMechainicMainBinding, MechanicMainViewModel>() {
    override fun initView() {
        StatusBarUtil.setStatusBarMarginTop(binding.layoutTitle.conTitle, this)
        binding.layoutTitle.tvTitle.text = "个人信息"
        binding.layoutTitle.barTvOther.text = "保存"
        binding.layoutTitle.barTvOther.visibility= View.VISIBLE
    }

    override fun initData() {

    }
}