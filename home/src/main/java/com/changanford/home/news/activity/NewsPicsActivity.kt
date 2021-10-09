package com.changanford.home.news.activity

import com.alibaba.android.arouter.facade.annotation.Route
import com.changanford.common.basic.BaseActivity
import com.changanford.common.router.path.ARouterHomePath
import com.changanford.common.utilext.StatusBarUtil
import com.changanford.home.R
import com.changanford.home.databinding.ActivityNewsPicDetailsBinding
import com.changanford.home.news.request.NewsDetailViewModel

/**
 *  图片详情。
 * */
@Route(path = ARouterHomePath.NewsPicsActivity)
class NewsPicsActivity : BaseActivity<ActivityNewsPicDetailsBinding, NewsDetailViewModel>() {

    override fun initView() {
        StatusBarUtil.setStatusBarColor(this, R.color.white)
        StatusBarUtil.setStatusBarMarginTop(binding.layoutHeader.conHomeBar, this)
    }

    override fun initData() {

    }
}