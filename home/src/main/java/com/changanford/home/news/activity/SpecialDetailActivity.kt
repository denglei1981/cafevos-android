package com.changanford.home.news.activity

import android.view.View
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.alibaba.android.arouter.facade.annotation.Route
import com.changanford.common.basic.BaseLoadSirActivity
import com.changanford.common.bean.AuthorBaseVo
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


    val newsListAdapter: NewsListAdapter by lazy {
        NewsListAdapter(this)
    }


    override fun initView() {
        binding.layoutEmpty.llEmpty.visibility=View.GONE
        binding.recyclerView.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        binding.recyclerView.adapter = newsListAdapter
    }

    override fun initData() {
        var topicId = intent.getStringExtra(JumpConstant.SPECIAL_TOPIC_ID) // 跳过来的详情。
        StatusBarUtil.setStatusBarMarginTop(binding.layoutBar.conTitle, this)
        setAppbarPercent()
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
                GlideUtils.loadBD(it.data.getPicUrl(), binding.layoutCollBar.ivHeader)
                GlideUtils.loadBD(it.data.getPicUrl(), binding.layoutCollBar.ivTopBg)
                binding.layoutBar.tvTitle.text = it.data.title
                if (it.data.articles != null&& it.data.articles!!.isNotEmpty()) {
                    newsListAdapter.setNewInstance(it.data.articles as? MutableList<InfoDataBean>?)
                } else {
//                    showEmpty()
                    binding.recyclerView.visibility=View.GONE
                    binding.layoutEmpty.llEmpty.visibility=View.VISIBLE
                }
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