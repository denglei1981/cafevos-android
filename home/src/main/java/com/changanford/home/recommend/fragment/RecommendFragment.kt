package com.changanford.home.recommend.fragment

import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import com.changanford.common.basic.BaseFragment
import com.changanford.common.basic.EmptyViewModel
import com.changanford.home.adapter.RecommendAdapter
import com.changanford.home.data.RecommendData
import com.changanford.home.databinding.FragmentRecommendListBinding


/**
 *  推荐列表
 * */
class RecommendFragment : BaseFragment<FragmentRecommendListBinding, EmptyViewModel>() {

    val  recommendAdapter: RecommendAdapter by lazy {
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
        binding.smartLayout.setEnableRefresh(false)
        binding.recyclerView.layoutManager=LinearLayoutManager(requireActivity(),LinearLayoutManager.VERTICAL,false)
        binding.recyclerView.adapter=RecommendAdapter().apply {
            addData(RecommendData())
            addData(RecommendData())
            addData(RecommendData())
            addData(RecommendData())
            addData(RecommendData())
            addData(RecommendData())
            addData(RecommendData())
            addData(RecommendData())
        }

    }

    override fun initData() {

    }
}