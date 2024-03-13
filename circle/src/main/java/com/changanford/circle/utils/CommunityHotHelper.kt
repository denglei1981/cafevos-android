package com.changanford.circle.utils

import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearSnapHelper
import androidx.recyclerview.widget.RecyclerView
import com.changanford.circle.R
import com.changanford.circle.adapter.CircleHomeBottomAdapter
import com.changanford.circle.adapter.CircleTopRecommendAdapter
import com.changanford.circle.adapter.HomeMidRankAdapter
import com.changanford.circle.databinding.LayoutHomeHotCielceHeaderBinding
import com.changanford.circle.ui.activity.circle.HotListActivity
import com.changanford.circle.viewmodel.circle.NewCircleViewModel
import com.changanford.common.util.gio.GioPageConstant

/**
 * @author: niubobo
 * @date: 2024/3/13
 * @description：
 */
class CommunityHotHelper(
    private val headBinding: LayoutHomeHotCielceHeaderBinding,
    private val viewModel: NewCircleViewModel,
    private val fragment: Fragment
) {

    private val topCircleAdapter by lazy { CircleTopRecommendAdapter() }
    private val midRankAdapter by lazy { HomeMidRankAdapter() }
    private val bottomCircleAdapter by lazy { CircleHomeBottomAdapter() }
    private val provinceLinearSnapHelper = LinearSnapHelper()

    fun initCommunity() {
        viewModel.getCircleHomeData()
        viewModel.getYouLikeData()
        provinceLinearSnapHelper.attachToRecyclerView(headBinding.ryRank)
        headBinding.ryCircleTop.adapter = topCircleAdapter
        headBinding.ryRank.adapter = midRankAdapter
        headBinding.ryCircleBottom.adapter = bottomCircleAdapter
        setIndicator()
        initListener()
        observe()
    }

    private fun observe() {
        viewModel.cirCleHomeData.observe(fragment) {
            topCircleAdapter.setList(it?.circleTypes)
            midRankAdapter.setList(it?.topList)
            val pageSize = it?.topList?.size
            pageSize?.let { it1 ->
                headBinding.drIndicator.setPageSize(it1)
                headBinding.drIndicator.isVisible = pageSize > 1
            }

        }

        viewModel.youLikeData.observe(fragment) {
            bottomCircleAdapter.setList(it)
        }
    }

    private fun initListener() {
        headBinding?.run {
            ryRank.addOnScrollListener(object : RecyclerView.OnScrollListener() {
                override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                    super.onScrolled(recyclerView, dx, dy)
//                    val layoutManager = recyclerView.layoutManager as LinearLayoutManager
//                    val fistVisibilityPosition = layoutManager.findFirstVisibleItemPosition()
//                    val current = fistVisibilityPosition
//                    drIndicator.post {
//                        drIndicator.onPageSelected(current)
//                    }

                    provinceLinearSnapHelper.findSnapView(headBinding.ryRank.layoutManager)?.let {
                        val position = recyclerView.getChildAdapterPosition(it)
                        drIndicator.onPageSelected(position)
                    }
                }
            })
            tvMoreRank.setOnClickListener {
                GioPageConstant.hotCircleEntrance = "社区-圈子-热门榜单-更多"
                //全部热门榜单
                HotListActivity.start()
            }
//            ryCircleBottom.addOnItemTouchListener(object : RecyclerView.OnItemTouchListener {
//                override fun onInterceptTouchEvent(rv: RecyclerView, e: MotionEvent): Boolean {
//                    view?.parent?.requestDisallowInterceptTouchEvent(true)
//                    return false
//                }
//
//                override fun onTouchEvent(rv: RecyclerView, e: MotionEvent) {}
//
//                override fun onRequestDisallowInterceptTouchEvent(disallowIntercept: Boolean) {}
//            })
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