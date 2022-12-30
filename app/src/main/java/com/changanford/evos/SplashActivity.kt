package com.changanford.evos

import android.content.Intent
import android.os.Bundle
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (intent.flags and Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT != 0) {
            finish()
            return
        }
    }

    override fun isUseFullScreenMode(): Boolean {
        return true
    }
}