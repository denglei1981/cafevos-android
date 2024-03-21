package com.changanford.circle.ui.fragment


import android.view.View
import androidx.core.content.ContextCompat
import com.changanford.circle.R
import com.changanford.circle.adapter.CircleSquareAdapter
import com.changanford.circle.databinding.FragmentSquareBinding
import com.changanford.circle.viewmodel.CircleViewModel
import com.changanford.common.basic.BaseFragment
import com.changanford.common.router.path.ARouterMyPath
import com.changanford.common.router.startARouter
import com.changanford.common.util.JumpUtils
import com.changanford.common.util.MConstant
import com.changanford.common.util.TimeUtils
import com.changanford.common.util.bus.CircleLiveBusKey
import com.changanford.common.util.bus.LiveDataBus
import com.changanford.common.util.bus.LiveDataBusKey
import com.changanford.common.util.gio.GIOUtils
import com.changanford.common.util.gio.GioPageConstant
import com.scwang.smart.refresh.layout.api.RefreshLayout
import com.scwang.smart.refresh.layout.listener.OnRefreshListener

class CircleSquareFragment : BaseFragment<FragmentSquareBinding, CircleViewModel>(),
    OnRefreshListener {

    private val circleSquareAdapter by lazy {
        CircleSquareAdapter(requireContext(), childFragmentManager, lifecycle)
    }

    companion object {
        fun newInstance(): CircleSquareFragment {
            return CircleSquareFragment()
        }
    }

    override fun initView() {
        binding.refreshLayout.setOnRefreshListener(this)

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
        viewModel.topSignBean.observe(this) {
            circleSquareAdapter.topBinding.run {
                tvDaysNum.text = "已连续签到${it.ontinuous}天"
                it.curDate?.let { ss ->
                    tvDays.text = TimeUtils.MillisToStrHM2(it.curDate)
                }
            }
        }
        viewModel.topicBean.observe(this) {
            circleSquareAdapter.run {
                circleSquareAdapter.topicAdapter.setList(it.topics)
                topicAdapter.notifyDataSetChanged()
            }
            binding.refreshLayout.finishRefresh()
        }
        viewModel.circleAdBean.observe(this) {
            binding.refreshLayout.finishRefresh()
            circleSquareAdapter.run {
                if (it.isNotEmpty()) {
                    circleSquareAdapter.topBinding.bViewpager.visibility = View.VISIBLE
                    circleSquareAdapter.topBinding.bViewpager.refreshData(it)
                    if (!it.isNullOrEmpty()) {
                        val item = it[0]
                        it[0].adName?.let { it1 ->
                            GIOUtils.homePageExposure(
                                "广告位banner", 1.toString(),
                                it1, item.maPlanId, item.maJourneyId, item.maJourneyActCtrlId
                            )
                        }
                    }
                } else {
                    circleSquareAdapter.topBinding.bViewpager.visibility = View.GONE
                }

            }
        }
        LiveDataBus.get().withs<Boolean>(CircleLiveBusKey.REFRESH_CIRCLE_MAIN).observe(this) {
            binding.refreshLayout.finishRefresh()
        }
        LiveDataBus.get().with(LiveDataBusKey.MINE_SIGN_SIGNED).observe(this) {
            onResume()
        }
    }

    override fun onResume() {
        super.onResume()
        GioPageConstant.topicEntrance = ""
        viewModel.getSignContinuousDays()
        checkSign()
    }

    private fun checkSign() {
        if (MConstant.userId.isNotEmpty()) {
            viewModel.getDay7Sign { daySignBean ->
                var canSign = daySignBean == null || MConstant.token.isNullOrEmpty()
                daySignBean?.sevenDays?.forEach {
                    if (it.signStatus == 2) {
                        canSign = true
                    }
                }
                if (!canSign) {
                    circleSquareAdapter.topBinding.tvSign.run {
                        setBackgroundResource(R.drawable.shape_e9_15dp)
                        text = "已签到"
                        isEnabled = false
                        setTextColor(ContextCompat.getColor(requireContext(), R.color.color_4d16))
                    }
                } else {
                    circleSquareAdapter.topBinding.tvSign.run {
                        setBackgroundResource(R.drawable.bg_sign_top_topic)
                        text = "签到得福币"
                        isEnabled = true
                        setTextColor(ContextCompat.getColor(requireContext(), R.color.white))
                        if (MConstant.userId.isNotEmpty()) {
                            setOnClickListener {
                                JumpUtils.instans?.jump(37)
                            }
                        } else {
                            setOnClickListener { startARouter(ARouterMyPath.SignUI) }
                        }
                    }
                }
            }
        } else {
            circleSquareAdapter.topBinding.tvSign.run {
                setBackgroundResource(R.drawable.bg_sign_top_topic)
                text = "签到得福币"
                isEnabled = true
                setTextColor(ContextCompat.getColor(requireContext(), R.color.white))
                setOnClickListener { startARouter(ARouterMyPath.SignUI) }
            }

        }
    }

    override fun onRefresh(refreshLayout: RefreshLayout) {
        circleSquareAdapter.run {
            outRefresh()
        }
    }
}