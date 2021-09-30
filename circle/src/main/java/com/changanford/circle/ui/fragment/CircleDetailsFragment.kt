package com.changanford.circle.ui.fragment

import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.listener.OnItemClickListener
import com.changanford.circle.R
import com.changanford.circle.adapter.CircleMainBottomAdapter
import com.changanford.circle.databinding.FragmentCircleDetailsBinding
import com.changanford.circle.utils.MUtils
import com.changanford.circle.viewmodel.CircleDetailsViewModel
import com.changanford.common.basic.BaseFragment
import com.changanford.common.router.path.ARouterCirclePath
import com.changanford.common.router.startARouter
import java.lang.reflect.Method

/**
 *Author lcw
 *Time on 2021/9/22
 *Purpose
 */
class CircleDetailsFragment : BaseFragment<FragmentCircleDetailsBinding, CircleDetailsViewModel>() {

    private lateinit var staggeredGridLayoutManager: StaggeredGridLayoutManager
    private lateinit var mCheckForGapMethod: Method

    //    private val adapter by lazy { CircleDetailsBarAdapter(requireContext()) }
    private val adapter by lazy { CircleMainBottomAdapter(requireContext()) }

    private var type = "4"
    private var page = 1
    private var topicId = ""
    private var circleId = ""

    companion object {
        fun newInstance(
            type: String,
            topicId: String,
            circleId: String = ""
        ): CircleDetailsFragment {
            val bundle = Bundle()
            bundle.putString("type", type)
            bundle.putString("topicId", topicId)
            bundle.putString("circleId", circleId)
            val fragment = CircleDetailsFragment()
            fragment.arguments = bundle
            return fragment
        }
    }

    override fun initView() {
//        MUtils.scrollStopLoadImage(binding.ryCircle)

        mCheckForGapMethod =
            StaggeredGridLayoutManager::class.java.getDeclaredMethod("checkForGaps")
        mCheckForGapMethod.isAccessible = true

        type = arguments?.getString("type", "4").toString()
        topicId = arguments?.getString("topicId", "").toString()
        circleId = arguments?.getString("circleId", "").toString()

        staggeredGridLayoutManager = StaggeredGridLayoutManager(
            2,
            StaggeredGridLayoutManager.VERTICAL
        )
        staggeredGridLayoutManager.spanCount
        binding.ryCircle.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                mCheckForGapMethod.invoke(binding.ryCircle.layoutManager) as Boolean
//                staggeredGridLayoutManager.invalidateSpanAssignments()
            }
        })
        binding.ryCircle.layoutManager = staggeredGridLayoutManager

        adapter.loadMoreModule.setOnLoadMoreListener {
            page++
            viewModel.getListData(type.toInt(), topicId, circleId, page)
        }
        binding.ryCircle.adapter = adapter

        adapter.setOnItemClickListener { _, view, position ->
            val bundle=Bundle()
            bundle.putString("postsId",adapter.getItem(position).postsId.toString())
            startARouter(ARouterCirclePath.PostDetailsActivity,bundle)
        }
    }

    override fun initData() {
        viewModel.getListData(type.toInt(), topicId, circleId, page)
    }

    override fun observe() {
        super.observe()
        viewModel.listBean.observe(this, {
            if (page == 1) {
                adapter.setList(it.dataList)
                if (it.dataList.size == 0) {
                    adapter.setEmptyView(R.layout.circle_empty_layout)
                }
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