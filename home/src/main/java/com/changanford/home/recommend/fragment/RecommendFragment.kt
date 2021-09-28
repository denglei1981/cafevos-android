package com.changanford.home.recommend.fragment

import android.os.Bundle
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.changanford.common.basic.BaseFragment
import com.changanford.common.utilext.logD
import com.changanford.home.adapter.RecommendAdapter
import com.changanford.home.databinding.FragmentRecommendListBinding
import com.changanford.home.recommend.request.RecommendViewModel


/**
 *  推荐列表
 * */
class RecommendFragment : BaseFragment<FragmentRecommendListBinding, RecommendViewModel>() {

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
        viewModel.getRecommend(1, 10, false)
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
}