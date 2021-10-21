package com.changanford.evos

import com.alibaba.android.arouter.facade.annotation.Route
import com.changanford.common.basic.BaseActivity
import com.changanford.common.basic.EmptyViewModel
import com.changanford.common.router.path.ARouterHomePath
import com.changanford.evos.databinding.ActivitySplashBinding
import com.gyf.immersionbar.ImmersionBar

@Route(path = ARouterHomePath.SplashActivity)
class SplashActivity : BaseActivity<ActivitySplashBinding, EmptyViewModel>() {
    override fun initView() {
        makeStateBarTransparent(true)
        ImmersionBar.with(this).statusBarDarkFont(isDarkFont).init()

    }

    override fun initData() {

    }
}