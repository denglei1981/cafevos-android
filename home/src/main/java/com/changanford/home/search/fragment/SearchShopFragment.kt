package com.changanford.home.search.fragment

import android.os.Bundle
import androidx.lifecycle.Observer
import com.changanford.common.basic.BaseLoadSirFragment
import com.changanford.common.bean.GioPreBean
import com.changanford.common.constant.JumpConstant
import com.changanford.common.router.path.ARouterShopPath
import com.changanford.common.router.startARouter
import com.changanford.common.util.JumpUtils
import com.changanford.common.util.bus.LiveDataBus
import com.changanford.common.util.bus.LiveDataBusKey
import com.changanford.common.util.gio.GioPageConstant
import com.changanford.home.PageConstant
import com.changanford.home.R
import com.changanford.home.databinding.HomeBaseRecyclerViewBinding
import com.changanford.home.search.activity.PloySearchResultActivity
import com.changanford.home.search.adapter.SearchShopResultAdapter
import com.changanford.home.search.request.PolySearchShopResultViewModel
import com.scwang.smart.refresh.layout.api.RefreshLayout
import com.scwang.smart.refresh.layout.listener.OnLoadMoreListener
import com.scwang.smart.refresh.layout.listener.OnRefreshListener

class SearchShopFragment :
    BaseLoadSirFragment<HomeBaseRecyclerViewBinding, PolySearchShopResultViewModel>(),
    OnRefreshListener, OnLoadMoreListener {

    private var selectPosition: Int = -1;// 记录选中的 条目
    val searchShopResultAdapter: SearchShopResultAdapter by lazy {
        SearchShopResultAdapter()
    }

    companion object {
        fun newInstance(skwContent: String): SearchShopFragment {
            val fg = SearchShopFragment()
            val bundle = Bundle()
            bundle.putString(JumpConstant.SEARCH_CONTENT, skwContent)
            fg.arguments = bundle
            return fg
        }
    }

    var searchContent: String? = null
    override fun initView() {
//        binding.recyclerView.layoutManager =
//            LinearLayoutManager(, LinearLayoutManager.VERTICAL, false)
        //        searchContent = arguments?.getString(JumpConstant.SEARCH_CONTENT)
        searchContent = (activity as PloySearchResultActivity).searchContent
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
        searchShopResultAdapter.setOnItemClickListener { adapter, view, position ->
            val item = searchShopResultAdapter.getItem(position)
            selectPosition = position
            val bundle = Bundle()
            bundle.putString("spuId", item.mallMallSpuId)
            bundle.putParcelable(GioPageConstant.shopPreBean, GioPreBean("搜索结果页", "搜索结果页"))
            startARouter(ARouterShopPath.ShopGoodsActivity, bundle)
        }

        LiveDataBus.get().withs<String>(LiveDataBusKey.UPDATE_SEARCH_RESULT).observe(this){
            outRefresh(it)
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
                    searchShopResultAdapter.addData(it.data.dataList)
                } else {
                    showContent()
                    binding.smartLayout.finishRefresh()
                    searchShopResultAdapter.setNewInstance(it.data.dataList)
                    if (it.data.dataList.size == 0) {
                        showResultEmpty()
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

   private fun outRefresh(keyWord: String) { // 暴露给外部的耍新
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