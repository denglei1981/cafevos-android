package com.changanford.circle.ui.fragment

import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.SimpleItemAnimator
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.changanford.circle.R
import com.changanford.circle.adapter.CircleRecommendAdapterV2
import com.changanford.circle.databinding.FragmentCircleDetailsV2Binding
import com.changanford.circle.viewmodel.CircleDetailsViewModel
import com.changanford.circle.viewmodel.shareBackUpHttp
import com.changanford.common.basic.BaseFragment
import com.changanford.common.router.path.ARouterCirclePath
import com.changanford.common.router.startARouter
import com.changanford.common.util.bus.CircleLiveBusKey
import com.changanford.common.util.bus.LiveDataBus
import com.changanford.common.util.bus.LiveDataBusKey
import com.changanford.common.util.gio.GIOUtils
import com.changanford.common.util.gio.GioPageConstant
import com.changanford.common.util.toast.ToastUtils
import java.lang.reflect.Method
import java.util.*

/**
 *Author lcw
 *Time on 2021/9/22
 *Purpose
 */
class CircleDetailsFragmentV2 :
    BaseFragment<FragmentCircleDetailsV2Binding, CircleDetailsViewModel>() {

    //    private lateinit var staggeredGridLayoutManager: StaggeredGridLayoutManager
    private lateinit var mCheckForGapMethod: Method

    //    private val adapter by lazy { CircleDetailsBarAdapter(requireContext()) }
    private val adapter by lazy { CircleRecommendAdapterV2(requireContext(), this) }

    private var type = "4"
    private var page = 1
    private var topicId = ""
    private var circleId = ""
    private var userId = ""

    private var checkPosition: Int? = null

    companion object {
        fun newInstance(
            type: String,
            topicId: String,
            circleId: String = "",
            userId: String? = ""
        ): CircleDetailsFragmentV2 {
            val bundle = Bundle()
            bundle.putString("type", type)
            bundle.putString("topicId", topicId)
            bundle.putString("circleId", circleId)
            bundle.putString("userId", userId)
            val fragment = CircleDetailsFragmentV2()
            fragment.arguments = bundle
            return fragment
        }
    }

    override fun initView() {
//        MUtils.scrollStopLoadImage(binding.ryCircle)
        bus()
        mCheckForGapMethod =
            StaggeredGridLayoutManager::class.java.getDeclaredMethod("checkForGaps")
        mCheckForGapMethod.isAccessible = true

        // 解决调用 notifyItemChanged 闪烁问题,取消默认动画
        (Objects.requireNonNull<RecyclerView.ItemAnimator>(binding.ryCircle.itemAnimator) as SimpleItemAnimator).supportsChangeAnimations =
            false
        binding.ryCircle.itemAnimator = null
        type = arguments?.getString("type", "4").toString()
        topicId = arguments?.getString("topicId", "").toString()
        circleId = arguments?.getString("circleId", "").toString()
        userId = arguments?.getString("userId", "").toString()

        adapter.isTopic = !topicId.isNullOrEmpty()
//        staggeredGridLayoutManager = StaggeredGridLayoutManager(
//            2,
//            StaggeredGridLayoutManager.VERTICAL
//        )
//        staggeredGridLayoutManager.spanCount
//        binding.ryCircle.addOnScrollListener(object : RecyclerView.OnScrollListener() {
////            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
////                super.onScrollStateChanged(recyclerView, newState)
////                mCheckForGapMethod.invoke(binding.ryCircle.layoutManager) as Boolean
//////                staggeredGridLayoutManager.invalidateSpanAssignments()
////            }
////        })
//        binding.ryCircle.layoutManager = staggeredGridLayoutManager

        binding.refreshLayout.setOnRefreshListener {
            page = 1
            initData()
            it.finishRefresh()
        }

        adapter.loadMoreModule.setOnLoadMoreListener {
            page++
            viewModel.getListData(type.toInt(), topicId, circleId, page, userId)
        }
        binding.ryCircle.adapter = adapter

        adapter.setOnItemClickListener { _, view, position ->
            if (topicId.isNotEmpty()) {
                GioPageConstant.postEntrance = "话题详情页"
            } else {
                GioPageConstant.postEntrance = "圈子详情页"
                GIOUtils.circleDetailPageResourceClick(
                    "帖子信息流",
                    (position + 1).toString(),
                    adapter.getItem(position).title
                )
            }
            val bundle = Bundle()
            bundle.putString("postsId", adapter.getItem(position).postsId.toString())
            startARouter(ARouterCirclePath.PostDetailsActivity, bundle)
            checkPosition = position
        }
        adapter.setOnItemChildClickListener { _, view, position ->
            if (view.id == R.id.tv_all_comment) {
                val bundle = Bundle()
                bundle.putString("postsId", adapter.getItem(position).postsId.toString())
                bundle.putBoolean("isScroll", true)
                startARouter(ARouterCirclePath.PostDetailsActivity, bundle)
                checkPosition = position
            }
        }
    }

    override fun initData() {
        viewModel.getListData(type.toInt(), topicId, circleId, page, userId)
    }

    override fun observe() {
        super.observe()
        viewModel.listBean.observe(this) {
            if (page == 1) {
                adapter.setList(it.dataList)
                binding.ryCircle.visibility = View.VISIBLE
                binding.topView.visibility = View.GONE
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
        }
    }

    private fun bus() {
        LiveDataBus.get().withs<Int>(CircleLiveBusKey.REFRESH_POST_LIKE).observe(this) {
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
        }
        LiveDataBus.get().withs<Boolean>(CircleLiveBusKey.DELETE_CIRCLE_POST).observe(this) {
            checkPosition?.let { it1 -> adapter.data.removeAt(it1) }
            checkPosition?.let { it1 -> adapter.notifyItemRemoved(it1) }
            checkPosition?.let { it1 -> adapter.notifyItemRangeChanged(it1, adapter.itemCount) }
        }
        //分享回调
        LiveDataBus.get().with(LiveDataBusKey.WX_SHARE_BACK).observe(this) {
            val item = adapter.checkPostDataBean
            item?.let { item ->
                if (it == 0) {
                    ToastUtils.reToast(R.string.str_shareSuccess)
                    shareBackUpHttp(this, item.shares, 1)
                    item.shareCount += 1
                    adapter.notifyDataSetChanged()
                }
            }
        }
    }
}