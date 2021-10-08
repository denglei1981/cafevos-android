package com.changanford.home.news.activity

import android.view.View
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.alibaba.android.arouter.facade.annotation.Route
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.listener.OnItemClickListener
import com.changanford.common.basic.BaseLoadSirActivity
import com.changanford.common.router.path.ARouterHomePath
import com.changanford.common.router.startARouter
import com.changanford.common.util.JumpUtils
import com.changanford.common.utilext.StatusBarUtil
import com.changanford.home.databinding.ActivityHomeBaseSmRvBinding
import com.changanford.home.news.adapter.SpecialListAdapter
import com.changanford.home.news.request.SpecialListViewModel
import com.scwang.smart.refresh.layout.api.RefreshLayout
import com.scwang.smart.refresh.layout.listener.OnLoadMoreListener
import com.scwang.smart.refresh.layout.listener.OnRefreshListener

@Route(path = ARouterHomePath.SpecialListActivity)
class SpecialListActivity :
    BaseLoadSirActivity<ActivityHomeBaseSmRvBinding, SpecialListViewModel>(),
    OnRefreshListener, OnLoadMoreListener {
    private val specialListAdapter: SpecialListAdapter by lazy {
        SpecialListAdapter()
    }

    override fun initView() {
        binding.layoutTitle.tvTitle.text = "专题列表"
        StatusBarUtil.setStatusBarMarginTop(binding.layoutTitle.conTitle, this)
        binding.smartLayout.setOnRefreshListener(this)
        binding.smartLayout.setOnLoadMoreListener(this)
        binding.recyclerView.adapter = specialListAdapter
        binding.recyclerView.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        binding.layoutTitle.ivBack.setOnClickListener {
            onBackPressed()
        }
        specialListAdapter.setOnItemClickListener(object :OnItemClickListener{
            override fun onItemClick(adapter: BaseQuickAdapter<*, *>, view: View, position: Int) {

                var item = specialListAdapter.getItem(position)
                JumpUtils.instans?.jump(8,item.artId)
//                startARouter(ARouterHomePath.SpecialDetailActivity)

            }
        })

    }

    override fun initData() {
        setLoadSir(binding.smartLayout)
        onRefresh(binding.smartLayout)
    }

    override fun observe() {
        super.observe()
        viewModel.specialListLiveData.observe(this, Observer {
            if (it.isSuccess) {
                if (it.isLoadMore) {
                    binding.smartLayout.finishLoadMore()
                    specialListAdapter.addData(it.data.dataList)
                } else {
                    showContent()
                    binding.smartLayout.finishRefresh()
                    specialListAdapter.setNewInstance(it.data.dataList)
                }
                if (viewModel.pageNo >= it.data.totalPage) {// 当前的页面  大于等于 总的页面-- 没有更多了。
                    binding.smartLayout.setEnableLoadMore(false)
                } else {
                    binding.smartLayout.setEnableLoadMore(true)
                }
            } else {
                showFailure(it.message)
            }
        })
    }
    override fun onRefresh(refreshLayout: RefreshLayout) {
        binding.smartLayout.setEnableLoadMore(true)
        viewModel.getSpecialList(false)
    }

    override fun onRetryBtnClick() {

    }

    override fun onLoadMore(refreshLayout: RefreshLayout) {
        viewModel.getSpecialList(true)

    }
}