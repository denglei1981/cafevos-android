package com.changanford.circle.ui.fragment


import android.view.View
import com.changanford.circle.adapter.CircleSquareAdapter
import com.changanford.circle.databinding.FragmentSquareBinding
import com.changanford.circle.viewmodel.CircleViewModel
import com.changanford.common.basic.BaseFragment
import com.changanford.common.util.bus.CircleLiveBusKey
import com.changanford.common.util.bus.LiveDataBus

class CircleSquareFragment : BaseFragment<FragmentSquareBinding, CircleViewModel>()  {

    private val circleSquareAdapter by lazy {
        CircleSquareAdapter(requireContext(), childFragmentManager)
    }
    companion object {
        fun newInstance(): CircleSquareFragment {
//            val bundle = Bundle()
            //            fragment.arguments = bundle
            return CircleSquareFragment()
        }
    }
    override fun initView() {

    }

    override fun initData() {
        initRecyclerData()
        viewModel.getRecommendTopic()
        viewModel.communityTopic()
    }

    private fun initRecyclerData() {
        val list = arrayListOf("", "")
        circleSquareAdapter.setItems(list)
        binding.ryCircle.adapter = circleSquareAdapter
    }

    override fun observe() {
        super.observe()

        viewModel.topicBean.observe(this, {
            circleSquareAdapter.run {
                circleSquareAdapter.topicAdapter.setList(it.topics)
                topicAdapter.notifyDataSetChanged()
            }
            binding.refreshLayout.finishRefresh()
        })
        viewModel.circleAdBean.observe(this,  {
            circleSquareAdapter.run {
                if (it.isNotEmpty()) {
                    circleSquareAdapter.topBinding.bViewpager.visibility = View.VISIBLE
                    circleSquareAdapter.topBinding.bViewpager.refreshData(it)
                } else {
                    circleSquareAdapter.topBinding.bViewpager.visibility = View.GONE
                }

            }
        })
        LiveDataBus.get().withs<Boolean>(CircleLiveBusKey.REFRESH_CIRCLE_MAIN).observe(this, {
            binding.refreshLayout.finishRefresh()
        })
    }
}