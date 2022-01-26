package com.changanford.circle.ui.ask.fragment

import android.os.Bundle
import com.changanford.circle.bean.MultiBean
import com.changanford.circle.databinding.FragmentAskRecommendBinding
import com.changanford.circle.ui.ask.adapter.RecommendAskAdapter
import com.changanford.circle.ui.fragment.CircleRecommendFragment
import com.changanford.circle.viewmodel.CircleDetailsViewModel
import com.changanford.common.basic.BaseFragment
import com.scwang.smart.refresh.layout.api.RefreshLayout
import com.scwang.smart.refresh.layout.listener.OnRefreshListener

class AskRecommendFragment : BaseFragment<FragmentAskRecommendBinding, CircleDetailsViewModel>(),
    OnRefreshListener {

    val recommendAskAdapter: RecommendAskAdapter by lazy {
        RecommendAskAdapter()
    }

    companion object {
        fun newInstance(): AskRecommendFragment {
//            val bundle = Bundle()
//            bundle.putInt("type", type)
            val fragment = AskRecommendFragment()
//            fragment.arguments = bundle
            return fragment
        }
    }

    override fun initView() {

    }

    override fun initData() {
        binding.ryAsk.adapter = recommendAskAdapter.also {
            it.addData(MultiBean())
            it.addData(MultiBean())
            it.addData(MultiBean())
            it.addData(MultiBean())
        }

    }

    override fun onRefresh(refreshLayout: RefreshLayout) {

    }
}