package com.changanford.circle.utils

import android.os.Bundle
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.LinearSnapHelper
import androidx.recyclerview.widget.RecyclerView
import com.changanford.circle.R
import com.changanford.circle.adapter.CircleHomeBottomAdapter
import com.changanford.circle.adapter.CircleTopRecommendAdapter
import com.changanford.circle.adapter.HomeMidRankAdapter
import com.changanford.circle.databinding.FragmentFordPaiCircleBinding
import com.changanford.circle.databinding.LayoutHomeHotCielceHeaderBinding
import com.changanford.circle.ui.activity.circle.HotListActivity
import com.changanford.circle.viewmodel.circle.NewCircleViewModel
import com.changanford.common.bean.NewCircleBean
import com.changanford.common.router.path.ARouterCirclePath
import com.changanford.common.router.startARouter
import com.changanford.common.util.MConstant
import com.changanford.common.util.bus.LiveDataBus
import com.changanford.common.util.bus.LiveDataBusKey
import com.changanford.common.util.gio.GioPageConstant

/**
 * @author: niubobo
 * @date: 2024/3/13
 * @description：
 */
class CommunityHotHelper(
    private val headBinding: LayoutHomeHotCielceHeaderBinding,
    private val binding: FragmentFordPaiCircleBinding,
    private val viewModel: NewCircleViewModel,
    private val fragment: Fragment
) {

    private val topCircleAdapter by lazy { CircleTopRecommendAdapter() }
    private val midRankAdapter by lazy { HomeMidRankAdapter() }
    private val bottomCircleAdapter by lazy { CircleHomeBottomAdapter() }
    private val provinceLinearSnapHelper = LinearSnapHelper()

    fun initCommunity() {
        initData()
        provinceLinearSnapHelper.attachToRecyclerView(headBinding.ryRank)
        headBinding.ryCircleTop.adapter = topCircleAdapter
        headBinding.ryRank.adapter = midRankAdapter
        headBinding.ryCircleBottom.adapter = bottomCircleAdapter
        setIndicator()
        initListener()
        observe()
    }

    fun initData() {
        viewModel.getCircleHomeData()
        viewModel.getYouLikeData()
    }

    private fun observe() {
        viewModel.cirCleHomeData.observe(fragment) {
            LiveDataBus.get().with(LiveDataBusKey.HOME_CIRCLE_HOT_BEAN).postValue(it)
            topCircleAdapter.setList(it?.circleTypes)
            midRankAdapter.setList(it?.topList)
            val pageSize = it?.topList?.size
            pageSize?.let { it1 ->
                headBinding.drIndicator.setPageSize(it1)
                headBinding.drIndicator.isVisible = pageSize > 1
            }
            myCircles.value = it?.myCircles
            if (MConstant.token.isEmpty() || it?.myCircles.isNullOrEmpty()) {
                binding.clTab.isVisible = false
                headBinding.cardNoCircle.isVisible = true
                binding.layoutHot.root.isVisible = true
                binding.layoutCircle.root.isVisible = false
            } else {
                binding.clTab.isVisible = true
                headBinding.cardNoCircle.isVisible = false
                binding.layoutHot.root.isVisible = false
                binding.layoutCircle.root.isVisible = true
            }
        }

        viewModel.youLikeData.observe(fragment) {
            bottomCircleAdapter.setList(it)
        }
    }

    var myCircles = MutableLiveData<ArrayList<NewCircleBean>?>()

    private fun initListener() {
        headBinding?.run {
            tvJoinTips.setOnClickListener {
                startARouter(ARouterCirclePath.CircleListActivity)
            }
            ryRank.addOnScrollListener(object : RecyclerView.OnScrollListener() {
                override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                    super.onScrolled(recyclerView, dx, dy)
                    provinceLinearSnapHelper.findSnapView(headBinding.ryRank.layoutManager)?.let {
                        val position = recyclerView.getChildAdapterPosition(it)
                        drIndicator.onPageSelected(position)
                    }
                }
            })
            bottomCircleAdapter.setOnItemClickListener { _, _, position ->
                val bundle = Bundle()
                bundle.putString("circleId", bottomCircleAdapter.data[position].circleId)
                startARouter(ARouterCirclePath.CircleDetailsActivity, bundle)
            }
            tvMoreRank.setOnClickListener {
                GioPageConstant.hotCircleEntrance = "社区-圈子-热门榜单-更多"
                //全部热门榜单
                HotListActivity.start()
            }
            tvAllCircle.setOnClickListener {
                startARouter(ARouterCirclePath.CircleListActivity)
            }
            tvSearch.setOnClickListener {
                startARouter(ARouterCirclePath.SearchCircleActivity)
            }
        }


    }

    private fun setIndicator() {
        val dp6 = fragment.requireContext().resources.getDimensionPixelOffset(R.dimen.dp_6)
        headBinding.drIndicator.setIndicatorDrawable(
            R.drawable.shape_home_banner_normal,
            R.drawable.shape_home_banner_focus
        ).setIndicatorSize(
            dp6,
            dp6,
            fragment.requireContext().resources.getDimensionPixelOffset(R.dimen.dp_20),
            dp6
        )
            .setIndicatorGap(fragment.requireContext().resources.getDimensionPixelOffset(R.dimen.dp_5))
    }
}