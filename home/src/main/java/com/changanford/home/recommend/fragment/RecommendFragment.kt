package com.changanford.home.recommend.fragment

import android.os.Bundle
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.changanford.common.basic.BaseFragment
import com.changanford.common.basic.BaseLoadSirFragment
import com.changanford.common.router.path.ARouterHomePath
import com.changanford.common.router.startARouter
import com.changanford.common.util.toast.ToastUtils
import com.changanford.home.HomeV2Fragment
import com.changanford.home.adapter.RecommendAdapter
import com.changanford.home.databinding.FragmentRecommendListBinding
import com.changanford.home.recommend.request.RecommendViewModel
import com.scwang.smart.refresh.layout.api.RefreshLayout
import com.scwang.smart.refresh.layout.listener.OnLoadMoreListener


/**
 *  推荐列表
 * */
class RecommendFragment : BaseLoadSirFragment<FragmentRecommendListBinding, RecommendViewModel>(),
    OnLoadMoreListener {

    val recommendAdapter: RecommendAdapter by lazy {
        RecommendAdapter()
    }

    companion object {
        fun newInstance(): RecommendFragment {
            val fg = RecommendFragment()
            val bundle = Bundle()
            fg.arguments = bundle
            return fg
        }
    }

    override fun initView() {
        viewModel.getRecommend(false)
        binding.smartLayout.setEnableRefresh(false)
        binding.recyclerView.layoutManager =
            LinearLayoutManager(requireActivity(), LinearLayoutManager.VERTICAL, false)
        binding.recyclerView.adapter = recommendAdapter
        recommendAdapter.setOnItemClickListener { adapter, view, position ->
            startARouter(ARouterHomePath.NewsPicAdActivity)
        }
        setLoadSir(binding.smartLayout)
    }

    override fun initData() {
        viewModel.recommendLiveData.observe(this, Observer {
            if (it.isSuccess) {
                val dataList = it.data.dataList
                if (it.isLoadMore) {
                    recommendAdapter.addData(dataList)
                    binding.smartLayout.finishLoadMore()
                } else {
                    if (it.data == null || dataList.size == 0) {
                        showEmpty()
                    }
                    showContent()
                    recommendAdapter.setNewInstance(dataList)
                    (parentFragment as HomeV2Fragment).stopRefresh()
                }
                if (dataList.size < it.data.pageSize) { // 没有请求的多, 不加载更多。
                    binding.smartLayout.finishLoadMoreWithNoMoreData()
                }
            } else {
                showFailure(it.message)
                ToastUtils.showShortToast(it.message, requireContext())
            }

        })
    }

    open fun homeRefersh() {
        viewModel.getRecommend(false)
    }

    override fun onLoadMore(refreshLayout: RefreshLayout) {
        viewModel.getRecommend(true)
    }

    override fun onRetryBtnClick() {

    }
}