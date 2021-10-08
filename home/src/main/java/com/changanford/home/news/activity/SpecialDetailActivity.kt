package com.changanford.home.news.activity

import android.view.View
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.alibaba.android.arouter.facade.annotation.Route
import com.changanford.common.basic.BaseActivity
import com.changanford.common.basic.BaseLoadSirActivity
import com.changanford.common.basic.EmptyViewModel
import com.changanford.common.bean.InfoDataBean
import com.changanford.common.constant.JumpConstant
import com.changanford.common.router.path.ARouterHomePath
import com.changanford.common.utilext.GlideUtils
import com.changanford.common.utilext.StatusBarUtil
import com.changanford.common.utilext.logE
import com.changanford.home.databinding.ActivitySpecialDetailBinding
import com.changanford.home.news.adapter.NewsListAdapter
import com.changanford.home.news.request.SpecialDetailViewModel
import com.google.android.material.appbar.AppBarLayout

@Route(path = ARouterHomePath.SpecialDetailActivity)
class SpecialDetailActivity :
    BaseLoadSirActivity<ActivitySpecialDetailBinding, SpecialDetailViewModel>() {


    var newsListAdapter: NewsListAdapter? = null


    override fun initView() {

        binding.recyclerView.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        newsListAdapter = NewsListAdapter().apply {

        }

        binding.recyclerView.adapter = newsListAdapter


    }

    override fun initData() {
        var topicId = intent.getStringExtra(JumpConstant.SPECIAL_TOPIC_ID) // 跳过来的详情。


        StatusBarUtil.setStatusBarMarginTop(binding.layoutBar.conTitle, this)
        setAppbarPercent()
        binding.layoutBar.tvTitle.text = "年轻人喜欢什么车"
        binding.layoutBar.ivMenu.visibility = View.VISIBLE
        binding.layoutBar.ivBack.setOnClickListener {
            onBackPressed()
        }
        binding.layoutCollBar.ivBack.setOnClickListener {
            onBackPressed()
        }
        if (topicId != null) {
            viewModel.getSpecialDetail(topicId)
            setLoadSir(binding.recyclerView)
        }
    }

    override fun observe() {
        super.observe()
        viewModel.specialDetailLiveData.observe(this, Observer {
            if (it.isSuccess) {
                showContent()
                binding.specialDetailData = it.data
                GlideUtils.loadBD(it.data.getPicUrl(),binding.layoutCollBar.ivHeader)
                GlideUtils.loadBD(it.data.getPicUrl(),binding.layoutCollBar.ivTopBg)

            } else {
                showFailure(it.message)
            }
        })

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

    override fun onRetryBtnClick() {

    }
}