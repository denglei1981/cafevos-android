package com.changanford.circle.ui.activity

import com.alibaba.android.arouter.facade.annotation.Route
import com.changanford.circle.databinding.ActivityCircleDetailsBinding
import com.changanford.circle.viewmodel.CircleDetailsViewModel
import com.changanford.common.basic.BaseActivity
import com.changanford.common.router.path.ARouterCirclePath

/**
 *Author lcw
 *Time on 2021/9/18
 *Purpose
 */
@Route(path = ARouterCirclePath.CircleDetailsActivity)
class CircleDetailsActivity:BaseActivity<ActivityCircleDetailsBinding,CircleDetailsViewModel>() {

    override fun initView() {
        
    }

    override fun initData() {
        
    }
}