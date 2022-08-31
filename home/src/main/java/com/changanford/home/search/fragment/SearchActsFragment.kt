package com.changanford.home.search.fragment

import android.os.Bundle
import android.view.View
import androidx.lifecycle.Observer
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.listener.OnItemClickListener
import com.changanford.common.adapter.SearchActsResultAdapter
import com.changanford.common.basic.BaseLoadSirFragment
import com.changanford.common.bean.ActBean
import com.changanford.common.constant.JumpConstant
import com.changanford.common.util.JumpUtils
import com.changanford.home.PageConstant
import com.changanford.home.R
import com.changanford.home.databinding.HomeBaseRecyclerViewBinding
import com.changanford.home.search.request.PolySearchActsResultViewModel
import com.scwang.smart.refresh.layout.api.RefreshLayout
import com.scwang.smart.refresh.layout.listener.OnLoadMoreListener
import com.scwang.smart.refresh.layout.listener.OnRefreshListener

class SearchActsFragment :
    BaseLoadSirFragment<HomeBaseRecyclerViewBinding, PolySearchActsResultViewModel>(),
    OnRefreshListener, OnLoadMoreListener {

    val searchActsResultAdapter: SearchActsResultAdapter by lazy {
        SearchActsResultAdapter()
    }

    companion object {
        fun newInstance(skwContent: String): SearchActsFragment {
            val fg = SearchActsFragment()
            val bundle = Bundle()
            bundle.putString(JumpConstant.SEARCH_CONTENT, skwContent)
            fg.arguments = bundle
            return fg
        }
    }

    private var selectPosition: Int = -1;// 记录选中的 条目
    var searchContent: String? = null
    override fun initView() {

        searchContent = arguments?.getString(JumpConstant.SEARCH_CONTENT)
        binding.recyclerView.adapter = searchActsResultAdapter
        binding.smartLayout.setOnRefreshListener(this)
        binding.smartLayout.setOnLoadMoreListener(this)
        searchActsResultAdapter.setOnItemChildClickListener { adapter, view, position ->
            selectPosition = position
            when (view.id) {
                R.id.iv_header, R.id.tv_author_name, R.id.tv_sub_title -> {// 去用户主页？
                    JumpUtils.instans!!.jump(35)
                }
            }
        }
        searchActsResultAdapter.setOnItemClickListener(object : OnItemClickListener {
            override fun onItemClick(adapter: BaseQuickAdapter<*, *>, view: View, position: Int) {
                val item: ActBean = searchActsResultAdapter.getItem(position)
//                CommonUtils.jumpActDetail(item.jumpType, item.jumpVal)
                JumpUtils.instans?.jump(item.jumpDto.jumpCode,item.jumpDto.jumpVal)
//                if (item.jumpType == 2||item.jumpType==1) {
                if (item.outChain == "YES") {
                    viewModel.AddACTbrid(searchActsResultAdapter.getItem(position).wonderfulId)
                }
            }
        })
        searchActsResultAdapter.sSetLogHistory{
            viewModel.AddACTbrid(it)
        }
    }

    override fun initData() {
        setLoadSir(binding.smartLayout)
        onRefresh(binding.smartLayout)
    }

    override fun observe() {
        super.observe()
        viewModel.newsListLiveData.observe(this, Observer {
            if (it.isSuccess) {
                if (it.isLoadMore) {
                    binding.smartLayout.finishLoadMore()
                    searchActsResultAdapter.addData(it.data.dataList)
                } else {
                    showContent()
                    binding.smartLayout.finishRefresh()
                    searchActsResultAdapter.setNewInstance(it.data.dataList)
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

    override fun onRetryBtnClick() {

    }

    fun outRefresh(keyWord: String) { // 暴露给外部的耍新
        searchContent = keyWord
        searchContent?.let {
            viewModel.getSearchContent(it, false)
        }
    }

    override fun onRefresh(refreshLayout: RefreshLayout) {
        searchContent?.let {
            viewModel.getSearchContent(it, false)
        }
    }

    override fun onLoadMore(refreshLayout: RefreshLayout) {
        searchContent?.let {
            viewModel.getSearchContent(it, true)
        }
    }


}