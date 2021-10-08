package com.changanford.home.shot.fragment

import android.os.Bundle
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.changanford.common.basic.BaseLoadSirFragment
import com.changanford.home.PageConstant.DEFAULT_PAGE_SIZE_THIRTY
import com.changanford.home.databinding.FragmentBigShotBinding
import com.changanford.home.news.data.SpecialData
import com.changanford.home.shot.adapter.BigShotPostListAdapter
import com.changanford.home.shot.adapter.BigShotUserListAdapter
import com.changanford.home.shot.request.BigShotListViewModel
import com.scwang.smart.refresh.layout.api.RefreshLayout
import com.scwang.smart.refresh.layout.listener.OnLoadMoreListener
import com.scwang.smart.refresh.layout.listener.OnRefreshListener

/**
 *  大咖
 * */
class BigShotFragment : BaseLoadSirFragment<FragmentBigShotBinding, BigShotListViewModel>(),
    OnRefreshListener, OnLoadMoreListener {


    private val bigShotUserListAdapter: BigShotUserListAdapter by lazy {
        BigShotUserListAdapter()
    }
    private val bigShotPostListAdapter: BigShotPostListAdapter by lazy {
        BigShotPostListAdapter()
    }

    companion object {
        fun newInstance(): BigShotFragment {
            val fg = BigShotFragment()
            val bundle = Bundle()
            fg.arguments = bundle
            return fg
        }
    }

    override fun initView() {

        setLoadSir(binding.refreshLayout)
        binding.recyclerViewH.adapter = bigShotUserListAdapter
        binding.recyclerViewH.layoutManager =
            LinearLayoutManager(requireActivity(), LinearLayoutManager.HORIZONTAL, false)
        binding.recyclerViewV.adapter = bigShotPostListAdapter
        binding.recyclerViewV.layoutManager =
            LinearLayoutManager(requireActivity(), LinearLayoutManager.VERTICAL, false)
        onRefresh(binding.refreshLayout)
    }

    override fun observe() {
        super.observe()
        viewModel.bigShotsLiveData.observe(this, Observer {
            if (it.isSuccess) {
                showContent()
                bigShotUserListAdapter.addData(it.data)
            } else {
                binding.refreshLayout.finishRefresh()
                showFailure(it.message)
            }
        })
        // 大咖帖子列表。
        viewModel.bigShotPostLiveData.observe(this, Observer {
            if (it.isSuccess) {
                showContent()
                binding.refreshLayout.finishRefresh()
                if (it.isLoadMore) {
                    binding.refreshLayout.finishLoadMore()
                    bigShotPostListAdapter.addData(it.data.dataList)
                } else {
                    bigShotPostListAdapter.setNewInstance(it.data.dataList)
                }
                if (it.data.dataList.size < DEFAULT_PAGE_SIZE_THIRTY) { // 是否能加载更多。
                    binding.refreshLayout.setEnableLoadMore(false)
                } else {
                    binding.refreshLayout.setEnableLoadMore(true)
                }
            } else {
                showFailure(it.message)
            }

        })

    }

    override fun initData() {


    }

    override fun onRetryBtnClick() {

    }

    override fun onRefresh(refreshLayout: RefreshLayout) {
        viewModel.getRecommendList()
        viewModel.getBigShotPost(false)
    }

    override fun onLoadMore(refreshLayout: RefreshLayout) {

        viewModel.getBigShotPost(true)

    }
}