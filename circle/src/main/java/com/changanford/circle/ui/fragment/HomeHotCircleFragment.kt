package com.changanford.circle.ui.fragment

import android.view.LayoutInflater
import androidx.core.view.isVisible
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.changanford.circle.R
import com.changanford.circle.adapter.CircleHomeBottomAdapter
import com.changanford.circle.adapter.CircleTopRecommendAdapter
import com.changanford.circle.adapter.HomeMidRankAdapter
import com.changanford.circle.databinding.FragmentHomeHotCircleBinding
import com.changanford.circle.databinding.LayoutHomeHotCielceHeaderBinding
import com.changanford.circle.ui.activity.circle.HotListActivity
import com.changanford.circle.viewmodel.circle.NewCircleViewModel
import com.changanford.common.basic.BaseLoadSirFragment
import com.changanford.common.util.gio.GioPageConstant

/**
 * @author: niubobo
 * @date: 2024/3/12
 * @description：
 */
class HomeHotCircleFragment :
    BaseLoadSirFragment<FragmentHomeHotCircleBinding, NewCircleViewModel>() {

    private val topCircleAdapter by lazy { CircleTopRecommendAdapter() }
    private val midRankAdapter by lazy { HomeMidRankAdapter() }
    private val bottomCircleAdapter by lazy { CircleHomeBottomAdapter() }
    private val emptyAdapter by lazy { CircleHomeBottomAdapter() }
    private var headBinding: LayoutHomeHotCielceHeaderBinding? = null
    override fun onRetryBtnClick() {

    }

    override fun initView() {
        setLoadSir(binding.ryHot)
        showLoading()
        binding.ryHot.adapter = emptyAdapter
        emptyAdapter.setList(null)
        setHeadView()
        headBinding?.ryCircleTop?.adapter = topCircleAdapter
        headBinding?.ryRank?.adapter = midRankAdapter
        headBinding?.ryCircleBottom?.adapter = bottomCircleAdapter
        setIndicator()
        initListener()
    }

    private fun setHeadView() {
        if (headBinding == null) {
            headBinding = DataBindingUtil.inflate(
                LayoutInflater.from(requireContext()),
                R.layout.layout_home_hot_cielce_header,
                binding.ryHot,
                false
            )

        }
        headBinding?.let {
            emptyAdapter.addHeaderView(it.root)
        }
    }

    private fun initListener() {
        headBinding?.run {
            ryRank.addOnScrollListener(object : RecyclerView.OnScrollListener() {
                override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                    super.onScrolled(recyclerView, dx, dy)
                    val layoutManager = recyclerView.layoutManager as LinearLayoutManager
                    val fistVisibilityPosition = layoutManager.findFirstVisibleItemPosition()
                    val current = fistVisibilityPosition
                    drIndicator.post {
                        drIndicator.onPageSelected(current)
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

    override fun initData() {
        viewModel.getCircleHomeData()
        viewModel.getYouLikeData()
    }

    override fun observe() {
        super.observe()
        viewModel.cirCleHomeData.observe(this) {
            topCircleAdapter.setList(it?.circleTypes)
            midRankAdapter.setList(it?.topList)
            val pageSize = it?.topList?.size
            pageSize?.let { it1 ->
                headBinding?.drIndicator?.setPageSize(it1)
                headBinding?.drIndicator?.isVisible = pageSize > 1
            }

            showContent()
        }

        viewModel.youLikeData.observe(this) {
//            bottomCircleAdapter.setList(it)
        }
    }

    private fun setIndicator() {
        val dp6 = requireContext().resources.getDimensionPixelOffset(R.dimen.dp_6)
        headBinding?.drIndicator?.setIndicatorDrawable(
            R.drawable.shape_home_banner_normal,
            R.drawable.shape_home_banner_focus
        )?.setIndicatorSize(
            dp6,
            dp6,
            requireContext().resources.getDimensionPixelOffset(R.dimen.dp_20),
            dp6
        )
            ?.setIndicatorGap(requireContext().resources.getDimensionPixelOffset(R.dimen.dp_5))
    }
}