package com.changanford.home.search.fragment

import android.os.Bundle
import android.view.View
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.listener.OnItemClickListener
import com.changanford.common.basic.BaseFragment
import com.changanford.common.basic.BaseLoadSirFragment
import com.changanford.common.constant.JumpConstant
import com.changanford.common.util.JumpUtils
import com.changanford.common.util.bus.LiveDataBus
import com.changanford.common.util.bus.LiveDataBusKey
import com.changanford.home.PageConstant
import com.changanford.home.R
import com.changanford.home.data.InfoDetailsChangeData
import com.changanford.home.databinding.HomeBaseRecyclerViewBinding
import com.changanford.home.search.adapter.SearchShopResultAdapter
import com.changanford.home.search.data.SearchData
import com.changanford.home.search.request.PolySearchNewsResultViewModel
import com.changanford.home.search.request.PolySearchShopResultViewModel
import com.scwang.smart.refresh.layout.api.RefreshLayout
import com.scwang.smart.refresh.layout.listener.OnLoadMoreListener
import com.scwang.smart.refresh.layout.listener.OnRefreshListener

class SearchShopFragment: BaseLoadSirFragment<HomeBaseRecyclerViewBinding, PolySearchShopResultViewModel>(),OnRefreshListener,OnLoadMoreListener {

    private var selectPosition: Int = -1;// 记录选中的 条目
    val searchShopResultAdapter : SearchShopResultAdapter by lazy {
        SearchShopResultAdapter()
    }
    companion object {
        fun newInstance(skwContent:String): SearchShopFragment {
            val fg = SearchShopFragment()
            val bundle = Bundle()
            bundle.putString(JumpConstant.SEARCH_CONTENT, skwContent)
            fg.arguments = bundle
            return fg
        }
    }

    var searchContent: String? = null
    override fun initView() {
        binding.recyclerView.layoutManager =
            LinearLayoutManager(requireActivity(), LinearLayoutManager.VERTICAL, false)
        searchContent = arguments?.getString(JumpConstant.SEARCH_CONTENT)
        binding.recyclerView.adapter = searchShopResultAdapter
        binding.smartLayout.setOnRefreshListener(this)
        binding.smartLayout.setOnLoadMoreListener(this)

        searchShopResultAdapter.setOnItemChildClickListener { adapter, view, position ->
            selectPosition = position
            when (view.id) {
                R.id.iv_header, R.id.tv_author_name, R.id.tv_sub_title -> {// 去用户主页？
                    JumpUtils.instans!!.jump(35)
                }

            }
        }
        searchShopResultAdapter.setOnItemClickListener(object : OnItemClickListener {
            override fun onItemClick(adapter: BaseQuickAdapter<*, *>, view: View, position: Int) {
                val item = searchShopResultAdapter.getItem(position)
                selectPosition = position
                // todo 跳转到商品
                JumpUtils.instans!!.jump(3,item.mallMallSpuId)
            }
        })
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
                    searchShopResultAdapter.addData(it.data.dataList)
                } else {
                    showContent()
                    binding.smartLayout.finishRefresh()
                    searchShopResultAdapter.setNewInstance(it.data.dataList)
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
        onRefresh(binding.smartLayout)
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