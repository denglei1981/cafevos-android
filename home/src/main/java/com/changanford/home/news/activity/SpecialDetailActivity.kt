package com.changanford.home.news.activity

import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.alibaba.android.arouter.facade.annotation.Route
import com.changanford.common.basic.BaseActivity
import com.changanford.common.basic.EmptyViewModel
import com.changanford.common.router.path.ARouterHomePath
import com.changanford.common.utilext.logE
import com.changanford.home.R
import com.changanford.home.databinding.ActivitySpecialDetailBinding
import com.changanford.home.news.adapter.NewsListAdapter
import com.changanford.home.news.data.NewsData
import com.changanford.home.news.powerfulrecyclerview.DividerDecoration
import com.google.android.material.appbar.AppBarLayout

@Route(path = ARouterHomePath.SpecialDetailActivity)
class SpecialDetailActivity : BaseActivity<ActivitySpecialDetailBinding, EmptyViewModel>() {



    var newsListAdapter: NewsListAdapter?=null


    override fun initView() {
        binding.recyclerView.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        newsListAdapter=NewsListAdapter().apply {
            addData(NewsData())
            addData(NewsData())
            addData(NewsData())
            addData(NewsData())
            addData(NewsData())
            addData(NewsData())
            addData(NewsData())
            addData(NewsData())
        }

        binding.recyclerView.adapter = newsListAdapter


    }

    override fun initData() {
        setAppbarPercent()
        binding.layoutBar.tvTitle.text = "年轻人喜欢什么车"
        binding.layoutBar.ivMenu.visibility = View.VISIBLE
        binding.layoutBar.ivBack.setOnClickListener {
            onBackPressed()
        }
        binding.layoutCollBar.ivBack.setOnClickListener {
            onBackPressed()
        }

    }

    private fun setAppbarPercent() {
        binding.appBar.addOnOffsetChangedListener(AppBarLayout.OnOffsetChangedListener { appBarLayout, verticalOffset ->
            "verticalOffset=$verticalOffset".logE()
            val percent: Float = -verticalOffset / appBarLayout.totalScrollRange.toFloat()//滑动比例
            "percent=$percent".logE()
            if (percent > 0.8) {
                binding.layoutBar.conTitle.visibility = View.VISIBLE
                "conContent=visiable".logE()
            } else {
                binding.layoutBar.conTitle.visibility = View.GONE
                "conContent=gone".logE()
            }
        })

    }
}