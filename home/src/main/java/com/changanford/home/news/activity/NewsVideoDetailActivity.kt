package com.changanford.home.news.activity

import android.view.LayoutInflater
import android.view.View
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import com.alibaba.android.arouter.facade.annotation.Route
import com.changanford.common.basic.BaseActivity
import com.changanford.common.basic.EmptyViewModel
import com.changanford.common.router.path.ARouterHomePath
import com.changanford.home.R
import com.changanford.home.databinding.ActivityHomeNewsVideoDetailBinding
import com.changanford.home.databinding.IncludeHomePicVideoNewsContentBinding
import com.changanford.home.news.adapter.HomeNewsCommentAdapter


@Route(path = ARouterHomePath.NewsVideoDetailActivity)
class NewsVideoDetailActivity : BaseActivity<ActivityHomeNewsVideoDetailBinding, EmptyViewModel>() {

    val homeNewsCommentAdapter: HomeNewsCommentAdapter by lazy {
        HomeNewsCommentAdapter(this)
    }

    override fun initView() {
        binding.homeRvContent.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        binding.homeRvContent.adapter=homeNewsCommentAdapter
        var list= arrayListOf<String>()
        list.add("12")
        list.add("12")
        list.add("12")
        list.add("12")
        list.add("12")
        homeNewsCommentAdapter.setItems(list)
    }

    override fun initData() {

    }

    var inflateHeader: IncludeHomePicVideoNewsContentBinding? = null
    fun addHeaderView() {
        if (inflateHeader == null) {
            inflateHeader = DataBindingUtil.inflate<IncludeHomePicVideoNewsContentBinding>(
                LayoutInflater.from(this),
                R.layout.include_home_pic_video_news_content,
                binding.homeRvContent,
                false
            )
        }
    }
}