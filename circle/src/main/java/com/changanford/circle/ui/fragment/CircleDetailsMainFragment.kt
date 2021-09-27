package com.changanford.circle.ui.fragment

import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.bumptech.glide.Glide
import com.changanford.circle.adapter.CircleMainBottomAdapter
import com.changanford.circle.databinding.FragmentCircleDetailsMainBinding
import com.changanford.circle.utils.MUtils
import com.changanford.circle.viewmodel.CircleDetailsViewModel
import com.changanford.common.basic.BaseFragment
import com.changanford.common.basic.adapter.OnRecyclerViewItemClickListener
import com.changanford.common.router.path.ARouterCirclePath
import com.changanford.common.router.startARouter
import java.lang.reflect.Method

/**
 *Author lcw
 *Time on 2021/9/22
 *Purpose
 */
class CircleDetailsMainFragment :
    BaseFragment<FragmentCircleDetailsMainBinding, CircleDetailsViewModel>() {

    private lateinit var staggeredGridLayoutManager: StaggeredGridLayoutManager
    private lateinit var mCheckForGapMethod: Method

    private val adapter by lazy { CircleMainBottomAdapter(requireContext()) }

    companion object {
        fun newInstance(type: String): CircleDetailsMainFragment {
            val bundle = Bundle()
            bundle.putString("type", type)
            val fragment = CircleDetailsMainFragment()
            fragment.arguments = bundle
            return fragment
        }
    }

    override fun initView() {

        MUtils.scrollStopLoadImage(binding.ryCircle)

        mCheckForGapMethod =
            StaggeredGridLayoutManager::class.java.getDeclaredMethod("checkForGaps")
        mCheckForGapMethod.isAccessible=true

        staggeredGridLayoutManager = StaggeredGridLayoutManager(
            2,
            StaggeredGridLayoutManager.VERTICAL
        )
        staggeredGridLayoutManager.gapStrategy = StaggeredGridLayoutManager.GAP_HANDLING_NONE
        binding.ryCircle.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                mCheckForGapMethod.invoke(binding.ryCircle.layoutManager) as Boolean
//                staggeredGridLayoutManager.invalidateSpanAssignments()
            }
        })
        binding.ryCircle.layoutManager = staggeredGridLayoutManager

        binding.ryCircle.adapter = adapter

        adapter.setOnItemClickListener(object : OnRecyclerViewItemClickListener {
            override fun onItemClick(view: View?, position: Int) {
                startARouter(ARouterCirclePath.PostGraphicActivity)
            }

        })
    }

    override fun initData() {
        viewModel.getData()
    }

    override fun observe() {
        super.observe()
        viewModel.circleBean.observe(this, {
            adapter.setItems(it.dataList)
            adapter.notifyDataSetChanged()
        })
    }
}