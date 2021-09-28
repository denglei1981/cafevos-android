package com.changanford.circle.ui.fragment

import android.os.Bundle
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.changanford.circle.adapter.CircleMainBottomAdapter
import com.changanford.circle.databinding.FragmentCircleDetailsMainBinding
import com.changanford.circle.viewmodel.CircleDetailsViewModel
import com.changanford.common.basic.BaseFragment
import com.changanford.common.router.path.ARouterCirclePath
import com.changanford.common.router.startARouter
import com.changanford.common.utilext.toast
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

    private var type = 0
    private var page = 1

    companion object {
        fun newInstance(type: Int): CircleDetailsMainFragment {
            val bundle = Bundle()
            bundle.putInt("type", type)
            val fragment = CircleDetailsMainFragment()
            fragment.arguments = bundle
            return fragment
        }
    }

    override fun initView() {
        type = arguments?.getInt("type", 4)!!
//        MUtils.scrollStopLoadImage(binding.ryCircle)

        mCheckForGapMethod =
            StaggeredGridLayoutManager::class.java.getDeclaredMethod("checkForGaps")
        mCheckForGapMethod.isAccessible = true

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
        adapter.loadMoreModule.setOnLoadMoreListener {
            page++
            viewModel.getData(type, page)
        }
        adapter.setOnItemClickListener { adapter, view, position ->
            startARouter(ARouterCirclePath.PostGraphicActivity)
        }
    }

    override fun initData() {
        viewModel.getData(type, 1)
    }

    override fun observe() {
        super.observe()
        viewModel.circleBean.observe(this, {
            if (page == 1) {
                adapter.setList(it.dataList)
            } else {
                adapter.addData(it.dataList)
                adapter.loadMoreModule.loadMoreComplete()
            }
            if (it.dataList.size != 20) {
                adapter.loadMoreModule.loadMoreEnd()
            }
        })
    }
}