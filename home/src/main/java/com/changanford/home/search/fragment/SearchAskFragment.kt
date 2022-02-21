package com.changanford.home.search.fragment

import android.os.Bundle
import androidx.lifecycle.Observer
import com.changanford.common.basic.BaseLoadSirFragment
import com.changanford.common.constant.JumpConstant
import com.changanford.common.constant.SearchTypeConstant
import com.changanford.common.util.JumpUtils
import com.changanford.home.PageConstant
import com.changanford.home.databinding.FragmentSearchAskBinding
import com.changanford.home.databinding.HomeBaseRecyclerViewBinding
import com.changanford.home.search.adapter.SearchAskResultAdapter
import com.changanford.home.search.adapter.SearchUserResultAdapter
import com.changanford.home.search.request.PolySearchAskViewModel
import com.changanford.home.search.request.PolySearchUserViewModel
import com.scwang.smart.refresh.layout.api.RefreshLayout
import com.scwang.smart.refresh.layout.listener.OnLoadMoreListener
import com.scwang.smart.refresh.layout.listener.OnRefreshListener

class SearchAskFragment :
    BaseLoadSirFragment<FragmentSearchAskBinding, PolySearchAskViewModel>(),
    OnLoadMoreListener, OnRefreshListener {
    var searchContent: String? = null
    val searchAskResultAdapter: SearchAskResultAdapter by lazy {
        SearchAskResultAdapter(this)
    }

    companion object {
        fun newInstance(skwContent: String): SearchAskFragment {
            val fg = SearchAskFragment()
            val bundle = Bundle()
            bundle.putString(JumpConstant.SEARCH_CONTENT, skwContent)
            fg.arguments = bundle
            return fg
        }
    }

    override fun initView() {
        binding.recyclerView.adapter = searchAskResultAdapter
        searchContent = arguments?.getString(JumpConstant.SEARCH_CONTENT)
        searchAskResultAdapter.setOnItemClickListener { adapter, view, position ->
            val item = searchAskResultAdapter.getItem(position)
            JumpUtils.instans!!.jump(item.jumpType.toIntOrNull(),item.jumpValue)
        }

    }

    override fun initData() {
        setLoadSir(binding.smartLayout)
        onRefresh(binding.smartLayout)
    }

    override fun observe() {
        super.observe()
        viewModel.searchHistoryLiveData.observe(this, Observer {
            if (it.isSuccess) {
                if (it.isLoadMore) {
                    binding.smartLayout.finishLoadMore()
                    searchAskResultAdapter.addData(it.data.dataList)
                } else {
                    showContent()
                    binding.smartLayout.finishRefresh()
                    searchAskResultAdapter.setNewInstance(it.data.dataList)
                    if (it.data.dataList.size == 0) {
                        showEmpty()
                    }
                }
                if (it.data.dataList.size < PageConstant.DEFAULT_PAGE_SIZE_THIRTY) {
                    binding.smartLayout.setEnableLoadMore(false)
                } else {
                    binding.smartLayout.setEnableLoadMore(true)
                }
            } else {
                showFailure(it.message)
            }
        })
    }

    override fun onLoadMore(refreshLayout: RefreshLayout) {
        searchContent?.let {
            viewModel.getSearchContent(SearchTypeConstant.SEARCH_ACTION_ASK, it, true)
        }
    }
    fun outRefresh(keyWord: String) { // 暴露给外部的耍新
        searchContent = keyWord
        onRefresh(binding.smartLayout)
    }
    override fun onRefresh(refreshLayout: RefreshLayout) {
        searchContent?.let {
            viewModel.getSearchContent(SearchTypeConstant.SEARCH_ACTION_ASK, it, false)
        }
    }

    override fun onRetryBtnClick() {

    }
}