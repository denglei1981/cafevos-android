package com.changanford.home.news.activity

import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.alibaba.android.arouter.facade.annotation.Route
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.listener.OnItemClickListener
import com.changanford.common.basic.BaseActivity
import com.changanford.common.basic.BaseLoadSirActivity
import com.changanford.common.router.path.ARouterHomePath
import com.changanford.common.router.startARouter
import com.changanford.common.utilext.StatusBarUtil
import com.changanford.home.databinding.ActivityHomeBaseSmRvBinding
import com.changanford.home.news.adapter.SpecialListAdapter
import com.changanford.home.news.data.SpecialData
import com.changanford.home.news.request.SpecialListViewModel
import com.scwang.smart.refresh.layout.api.RefreshLayout
import com.scwang.smart.refresh.layout.listener.OnRefreshListener

@Route(path = ARouterHomePath.SpecialListActivity)
class SpecialListActivity : BaseLoadSirActivity<ActivityHomeBaseSmRvBinding, SpecialListViewModel>(),
    OnRefreshListener {
    private val  specialListAdapter: SpecialListAdapter by lazy {
         SpecialListAdapter()
    }

    override fun initView() {
        binding.layoutTitle.tvTitle.text="什么专题什么名"
        StatusBarUtil.setStatusBarMarginTop(binding.layoutTitle.conTitle, this)
        binding.smartLayout.setOnRefreshListener(this)

        binding.recyclerView.adapter = specialListAdapter
        binding.recyclerView.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        binding.layoutTitle.ivBack.setOnClickListener {
            onBackPressed()
        }

    }

    override fun initData() {


    }

    override fun onRefresh(refreshLayout: RefreshLayout) {

    }

    override fun onRetryBtnClick() {

    }
}