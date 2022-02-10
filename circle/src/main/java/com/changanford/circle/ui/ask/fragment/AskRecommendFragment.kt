package com.changanford.circle.ui.ask.fragment

import android.view.LayoutInflater
import android.view.View
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import com.changanford.circle.R
import com.changanford.circle.adapter.CircleAdBannerAdapter
import com.changanford.circle.databinding.FragmentAskRecommendBinding
import com.changanford.circle.databinding.HeaderCircleAskRecommendBinding
import com.changanford.circle.databinding.LayoutCircleHeaderHotTopicBinding
import com.changanford.circle.ui.ask.adapter.HotMechanicAdapter
import com.changanford.circle.ui.ask.adapter.RecommendAskAdapter
import com.changanford.circle.ui.ask.request.AskRecommendViewModel
import com.changanford.circle.utils.TestBeanUtil
import com.changanford.circle.viewmodel.CircleDetailsViewModel
import com.changanford.common.basic.BaseFragment
import com.changanford.common.router.path.ARouterCirclePath
import com.changanford.common.router.startARouter
import com.scwang.smart.refresh.layout.api.RefreshLayout
import com.scwang.smart.refresh.layout.listener.OnRefreshListener
import com.zhpan.bannerview.constants.PageStyle

class AskRecommendFragment : BaseFragment<FragmentAskRecommendBinding, AskRecommendViewModel>(),
    OnRefreshListener {

    val recommendAskAdapter: RecommendAskAdapter by lazy {
        RecommendAskAdapter()
    }

    val hotMechanicAdapter: HotMechanicAdapter by lazy {
        HotMechanicAdapter()

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
        binding.ryAsk.adapter = recommendAskAdapter
        recommendAskAdapter.setOnItemClickListener { adapter, view, position ->
            startARouter(ARouterCirclePath.CreateQuestionActivity, true)
        }
        addHeadView()
        viewModel.getInitQuestion()
        viewModel.getQuestionList(false,1)
    }

    override fun onRefresh(refreshLayout: RefreshLayout) {

    }


    var headerBinding: HeaderCircleAskRecommendBinding? = null
    private fun addHeadView() {
        if (headerBinding == null) {
            headerBinding = DataBindingUtil.inflate(
                LayoutInflater.from(requireContext()),
                R.layout.header_circle_ask_recommend,
                binding.ryAsk,
                false
            )
            headerBinding?.let {
                recommendAskAdapter.addHeaderView(it.root)
                it.tvMore.setOnClickListener {
                    startARouter(ARouterCirclePath.HotTopicActivity)
                }
                it.ryTopic.adapter=hotMechanicAdapter

            }

        }
    }

    override fun observe() {
        super.observe()
        viewModel.mechanicLiveData.observe(this, Observer {
            hotMechanicAdapter.setNewInstance(it.tecnicianVoList)
        })
        viewModel.questionListLiveData.observe(this, Observer {
            recommendAskAdapter.setNewInstance(it.data.dataList)
        })

    }


}