package com.changanford.home.news.activity

import androidx.recyclerview.widget.LinearLayoutManager
import com.alibaba.android.arouter.facade.annotation.Route
import com.changanford.common.basic.BaseActivity
import com.changanford.common.basic.EmptyViewModel
import com.changanford.common.bean.InfoDataBean
import com.changanford.common.router.path.ARouterHomePath
import com.changanford.common.utilext.StatusBarUtil
import com.changanford.home.R
import com.changanford.home.databinding.ActivityHomeNewsPicAdBinding
import com.changanford.home.news.adapter.NewsPicAdAdapter
import com.changanford.home.news.adapter.NewsPicAdBannerAdapter
import com.changanford.home.news.request.NewsDetailViewModel
import com.gyf.immersionbar.ImmersionBar
import java.util.ArrayList

/**
 *  广告。
 * */
@Route(path = ARouterHomePath.NewsPicAdActivity)
class NewsPicAdActivity : BaseActivity<ActivityHomeNewsPicAdBinding, NewsDetailViewModel>() {

    private var mPictureList: MutableList<String> = ArrayList() // 图片存储位置

    val mNewsPicAdAdapter: NewsPicAdAdapter by lazy {
        NewsPicAdAdapter()
    }

    override fun initView() {
        ImmersionBar.with(this).statusBarColor(R.color.white)
        StatusBarUtil.setStatusBarMarginTop(binding.layoutHeader.conHomeBar, this)
        binding.homeRv.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)


        binding.homeRv.adapter = mNewsPicAdAdapter
        binding.banner.setAdapter(NewsPicAdBannerAdapter())
            .setCanLoop(true)
            .setAutoPlay(true)
            .setScrollDuration(500)
            .setIndicatorView(null)
            .create(getPicList())
        binding.layoutHeader.ivBack.setOnClickListener {
            onBackPressed()
        }
    }

    override fun initData() {

    }

    private fun getPicList(): MutableList<String> {
        mPictureList.add("https://img.oushangstyle.com/images/article_img/2021/09/528614463ed76ffa.png")
        mPictureList.add("https://img.oushangstyle.com/images/article_img/2021/09/528614463ed76ffa.png")
        mPictureList.add("https://img.oushangstyle.com/images/article_img/2021/09/528614463ed76ffa.png")
        mPictureList.add("https://img.oushangstyle.com/images/article_img/2021/09/528614463ed76ffa.png")
        return mPictureList
    }
}