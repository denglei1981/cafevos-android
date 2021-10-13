package com.changanford.home.search.fragment

import android.os.Bundle
import android.view.View
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.listener.OnItemClickListener
import com.changanford.common.basic.BaseLoadSirFragment
import com.changanford.common.constant.JumpConstant
import com.changanford.common.constant.SearchTypeConstant
import com.changanford.common.util.JumpUtils
import com.changanford.home.PageConstant
import com.changanford.home.databinding.HomeBaseRecyclerViewBinding
import com.changanford.home.search.adapter.SearchUserResultAdapter
import com.changanford.home.search.request.PolySearchUserViewModel
import com.scwang.smart.refresh.layout.api.RefreshLayout
import com.scwang.smart.refresh.layout.listener.OnLoadMoreListener
import com.scwang.smart.refresh.layout.listener.OnRefreshListener

class SearchUserFragment :
    BaseLoadSirFragment<HomeBaseRecyclerViewBinding, PolySearchUserViewModel>(),
    OnLoadMoreListener, OnRefreshListener {
    var searchContent: String? = null
    val searchUserResultAdapter: SearchUserResultAdapter by lazy {
        SearchUserResultAdapter(this)
    }

    companion object {
        fun newInstance(skwContent: String): SearchUserFragment {
            val fg = SearchUserFragment()
            val bundle = Bundle()
            bundle.putString(JumpConstant.SEARCH_CONTENT, skwContent)
            fg.arguments = bundle
            return fg
        }
    }

    override fun initView() {
        binding.recyclerView.layoutManager =
            LinearLayoutManager(requireActivity(), LinearLayoutManager.VERTICAL, false)
        binding.recyclerView.adapter = searchUserResultAdapter
        searchContent = arguments?.getString(JumpConstant.SEARCH_CONTENT)
        searchUserResultAdapter.setOnItemClickListener(object :OnItemClickListener{
            override fun onItemClick(adapter: BaseQuickAdapter<*, *>, view: View, position: Int) {
                  JumpUtils.instans!!.jump(35) // 跳转到他人主页。
            }
        })

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
                    searchUserResultAdapter.addData(it.data.dataList)
                } else {
                    showContent()
                    binding.smartLayout.finishRefresh()
                    searchUserResultAdapter.setNewInstance(it.data.dataList)
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
            viewModel.getSearchContent(SearchTypeConstant.SEARCH_USER, it, true)
        }
    }

    override fun onRefresh(refreshLayout: RefreshLayout) {
        searchContent?.let {
            viewModel.getSearchContent(SearchTypeConstant.SEARCH_USER, it, false)
        }
    }

    override fun onRetryBtnClick() {

    }
}