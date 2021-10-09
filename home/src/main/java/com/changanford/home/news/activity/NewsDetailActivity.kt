package com.changanford.home.news.activity

import com.alibaba.android.arouter.facade.annotation.Route
import com.changanford.common.basic.BaseActivity
import com.changanford.common.constant.JumpConstant
import com.changanford.common.router.path.ARouterHomePath
import com.changanford.home.databinding.ActivityNewsDetailsBinding
import com.changanford.home.databinding.HeaderNewsListBinding
import com.changanford.home.databinding.LayoutHeadlinesHeaderNewsDetailBinding
import com.changanford.home.news.request.NewsDetailViewModel

/**
 *  图文详情。。。
 * */
@Route(path = ARouterHomePath.NewsDetailActivity)
class NewsDetailActivity : BaseActivity<ActivityNewsDetailsBinding, NewsDetailViewModel>() {

    var headerBinding: LayoutHeadlinesHeaderNewsDetailBinding?=null // 头布局。。




    override fun initView() {

    }

    override fun initData() {
        var artId = intent.getStringExtra(JumpConstant.NEWS_ART_ID)





    }
}