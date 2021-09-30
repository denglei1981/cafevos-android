package com.changanford.evos

import com.changanford.common.basic.BaseActivity
import com.changanford.common.basic.EmptyViewModel
import com.changanford.evos.databinding.ActivitySplashBinding
import com.gyf.immersionbar.ImmersionBar

class SplashActivity : BaseActivity<ActivitySplashBinding, EmptyViewModel>() {
    override fun initView() {
        makeStateBarTransparent(true)
        ImmersionBar.with(this).statusBarDarkFont(isDarkFont).init()

    }

    override fun initData() {

    }
}