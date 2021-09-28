package com.changanford.home.recommend.fragment

import android.os.Bundle
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.changanford.common.basic.BaseFragment
import com.changanford.home.adapter.RecommendAdapter
import com.changanford.home.databinding.FragmentRecommendListBinding
import com.changanford.home.recommend.request.RecommendViewModel
import com.scwang.smart.refresh.layout.api.RefreshLayout
import com.scwang.smart.refresh.layout.listener.OnLoadMoreListener


/**
 *  推荐列表
 * */
class RecommendFragment : BaseFragment<FragmentRecommendListBinding, RecommendViewModel>(),OnLoadMoreListener {

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
        viewModel.getRecommend( 10, false)
        binding.smartLayout.setEnableRefresh(false)
        binding.recyclerView.layoutManager =
            LinearLayoutManager(requireActivity(), LinearLayoutManager.VERTICAL, false)
        binding.recyclerView.adapter = recommendAdapter
    }

    override fun initData() {
        viewModel.recommendLiveData.observe(this, Observer {
            recommendAdapter.addData(it.dataList)
        })
    }

    override fun onLoadMore(refreshLayout: RefreshLayout) {
        viewModel.getRecommend(10,true)
    }
}