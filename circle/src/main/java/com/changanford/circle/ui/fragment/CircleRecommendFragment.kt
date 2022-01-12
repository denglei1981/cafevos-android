package com.changanford.circle.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import com.changanford.circle.R
import com.changanford.circle.adapter.CircleAdBannerAdapter
import com.changanford.circle.adapter.CircleRecommendAdapter
import com.changanford.circle.adapter.CircleRecommendHotTopicAdapter
import com.changanford.circle.databinding.FragmentCircleRecommendBinding
import com.changanford.circle.databinding.LayoutCircleHeaderHotTopicBinding
import com.changanford.circle.viewmodel.CircleDetailsViewModel
import com.changanford.common.basic.BaseFragment
import com.changanford.common.router.path.ARouterCirclePath
import com.changanford.common.router.startARouter
import com.changanford.common.util.bus.CircleLiveBusKey
import com.changanford.common.util.bus.LiveDataBus
import com.scwang.smart.refresh.layout.api.RefreshLayout
import com.scwang.smart.refresh.layout.listener.OnRefreshListener
import com.zhpan.bannerview.constants.PageStyle

/**
 *Author lcw
 *Time on 2021/9/22
 *Purpose
 */
class CircleRecommendFragment :
    BaseFragment<FragmentCircleRecommendBinding, CircleDetailsViewModel>(), OnRefreshListener {


//    private lateinit var mCheckForGapMethod: Method

    private val adapter by lazy { CircleRecommendAdapter(requireContext(), this) }

    private val topicAdapter by lazy { CircleRecommendHotTopicAdapter() }

    private var type = 0
    private var page = 1

    private var checkPosition: Int? = null

    companion object {
        fun newInstance(type: Int): CircleRecommendFragment {
            val bundle = Bundle()
            bundle.putInt("type", type)
            val fragment = CircleRecommendFragment()
            fragment.arguments = bundle
            return fragment
        }
    }

    override fun initView() {
        type = arguments?.getInt("type", 4)!!
//        MUtils.scrollStopLoadImage(binding.ryCircle)
//        mCheckForGapMethod = StaggeredGridLayoutManager::class.java.getDeclaredMethod("checkForGaps")
//        mCheckForGapMethod.isAccessible = true


//        binding.ryCircle.addOnScrollListener(object : RecyclerView.OnScrollListener() {
//            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
//                super.onScrollStateChanged(recyclerView, newState)
//                mCheckForGapMethod.invoke(binding.ryCircle.layoutManager) as Boolean
////                staggeredGridLayoutManager.invalidateSpanAssignments()
//            }
//        })


        binding.ryCircle.adapter = adapter
        adapter.loadMoreModule.setOnLoadMoreListener {
            page++
            viewModel.getRecommendPostData(type, page)
        }
        adapter.setOnItemClickListener { _, view, position ->
            val bundle = Bundle()
            bundle.putString("postsId", adapter.getItem(position).postsId.toString())
            startARouter(ARouterCirclePath.PostDetailsActivity, bundle)
            checkPosition = position
        }
        binding.refreshLayout.setOnRefreshListener(this)
        viewModel.communityTopic()
        bus()
        addHeadView()
    }

    override fun initData() {
        viewModel.getRecommendPostData(type, 1)
        viewModel.getRecommendTopic()
    }

    var headerBinding: LayoutCircleHeaderHotTopicBinding? = null
    private fun addHeadView() {
        if (headerBinding == null) {
            headerBinding = DataBindingUtil.inflate(
                LayoutInflater.from(requireContext()),
                R.layout.layout_circle_header_hot_topic,
                binding.ryCircle,
                false
            )
            headerBinding?.let {
                adapter.addHeaderView(it.root, 0)
                it.tvTopicMore.setOnClickListener {
                    startARouter(ARouterCirclePath.HotTopicActivity)
                }
                it.bViewpager.visibility = View.GONE
                val recommendAdAdapter = CircleAdBannerAdapter()
                it.bViewpager.setAdapter(recommendAdAdapter)
                it.bViewpager.setCanLoop(true)
                it.bViewpager.setIndicatorView(it.drIndicator)
                it.bViewpager.setAutoPlay(true)
                it.bViewpager.setScrollDuration(500)
                it.bViewpager.setPageStyle(PageStyle.MULTI_PAGE_SCALE)
                it.bViewpager.create()
            }

//            setIndicator()
        }
    }

    override fun observe() {
        super.observe()
        viewModel.recommondBean.observe(this, Observer {
            if (page == 1) {
                binding.refreshLayout.finishRefresh()
                adapter.setList(it.dataList)
            } else {
                adapter.addData(it.dataList)
                adapter.loadMoreModule.loadMoreComplete()
            }
            if (it.dataList.size != 20) {
                adapter.loadMoreModule.loadMoreEnd()
            }
        })
        viewModel.topicBean.observe(this, Observer {
            headerBinding?.let { hb ->
                hb.ryTopic.adapter = topicAdapter
                topicAdapter.setList(it.topics)
                topicAdapter.setOnItemClickListener { adapter, view, position ->
                    val bundle = Bundle()
                    bundle.putString("topicId", topicAdapter.getItem(position).topicId.toString())
                    startARouter(ARouterCirclePath.TopicDetailsActivity, bundle)
                }
            }
        })
        viewModel.circleAdBean.observe(this, Observer {
            if (it.isNotEmpty()) {
                headerBinding?.bViewpager?.visibility = View.VISIBLE
                headerBinding?.bViewpager?.refreshData(it)
            }else{
                headerBinding?.bViewpager?.visibility = View.GONE
            }

        })
    }

    private fun bus() {
        LiveDataBus.get().withs<Int>(CircleLiveBusKey.REFRESH_POST_LIKE).observe(this, {
            val bean = checkPosition?.let { it1 -> adapter.getItem(it1) }
            bean?.let { _ ->
                bean.isLike = it
                if (bean.isLike == 1) {
                    bean.likesCount++
                } else {
                    bean.likesCount--
                }
            }
            checkPosition?.let { it1 -> adapter.notifyItemChanged(it1) }
        })
//        LiveDataBus.get().withs<Boolean>(CircleLiveBusKey.REFRESH_CIRCLE_BOTTOM_FRAGMENT)
//            .observe(this, {
//                page = 1
//                viewModel.getData(type, page)
//            })
        LiveDataBus.get().withs<Boolean>(CircleLiveBusKey.DELETE_CIRCLE_POST).observe(this, {
            checkPosition?.let { it1 -> adapter.data.removeAt(it1) }
            checkPosition?.let { it1 -> adapter.notifyItemRemoved(it1) }
            checkPosition?.let { it1 -> adapter.notifyItemRangeChanged(it1, adapter.itemCount) }
        })
    }

    override fun onRefresh(refreshLayout: RefreshLayout) {
        page = 1
        viewModel.getRecommendPostData(type, page)

    }
}