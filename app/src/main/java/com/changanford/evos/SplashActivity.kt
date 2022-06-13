package com.changanford.evos

import com.alibaba.android.arouter.facade.annotation.Route
import com.changanford.common.basic.EmptyViewModel
import com.changanford.common.router.path.ARouterHomePath
import com.changanford.evos.databinding.ActivitySplashBinding
import com.changanford.my.BaseMineUI

@Route(path = ARouterHomePath.SplashActivity)
class SplashActivity : BaseMineUI<ActivitySplashBinding, EmptyViewModel>() {

    override fun initView() {
//        makeStateBarTransparent(true)
//        ImmersionBar.with(this).statusBarDarkFont(isDarkFont).init()

    }

    override fun initData() {
//
    }

    override fun isUseFullScreenMode(): Boolean {
        return true
    }
}