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
import com.changanford.circle.databinding.FragmentCircleRecommendV2Binding
import com.changanford.circle.databinding.FragmentCircleV2Binding
import com.changanford.circle.databinding.LayoutCircleHeaderHotTopicBinding
import com.changanford.circle.viewmodel.CircleDetailsViewModel
import com.changanford.common.basic.BaseFragment
import com.changanford.common.manger.UserManger
import com.changanford.common.router.path.ARouterCirclePath
import com.changanford.common.router.startARouter
import com.changanford.common.util.bus.CircleLiveBusKey
import com.changanford.common.util.bus.LiveDataBus
import com.changanford.common.util.bus.LiveDataBusKey
import com.changanford.common.util.gio.GIOUtils
import com.changanford.common.util.gio.GioPageConstant
import com.scwang.smart.refresh.layout.api.RefreshLayout
import com.scwang.smart.refresh.layout.listener.OnRefreshListener
import com.zhpan.bannerview.constants.PageStyle

/**
 *Author lcw
 *Time on 2021/9/22
 *Purpose
 */
class CircleRecommendV2Fragment :
    BaseFragment<FragmentCircleRecommendV2Binding, CircleDetailsViewModel>() {


//    private lateinit var mCheckForGapMethod: Method

    private val adapter by lazy { CircleRecommendAdapter(requireContext(), this) }


    private var type = 0
    private var page = 1

    private var checkPosition: Int? = null

    companion object {
        fun newInstance(type: Int): CircleRecommendV2Fragment {
            val bundle = Bundle()
            bundle.putInt("type", type)
            val fragment = CircleRecommendV2Fragment()
            fragment.arguments = bundle
            return fragment
        }
    }

    override fun initView() {
        type = arguments?.getInt("type", 1)!!
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
            GioPageConstant.postEntrance = "社区-广场-信息流"
            val bundle = Bundle()
            bundle.putString("postsId", adapter.getItem(position).postsId.toString())
            startARouter(ARouterCirclePath.PostDetailsActivity, bundle)
            checkPosition = position
            GIOUtils.homePageClick(
                "广场信息流",
                (position + 1).toString(),
                adapter.getItem(position).title
            )
        }

        viewModel.communityTopic()
        bus()

    }

    override fun initData() {
        viewModel.getRecommendPostData(type, 1)

    }


    override fun observe() {
        super.observe()
        viewModel.recommondBean.observe(this, Observer {
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
        //登录回调
        LiveDataBus.get()
            .with(LiveDataBusKey.USER_LOGIN_STATUS, UserManger.UserLoginStatus::class.java)
            .observe(this) {
                viewModel.getRecommendPostData(type, 1)
            }

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

//    override fun onRefresh(refreshLayout: RefreshLayout) {
//
//
//    }

    fun outRefresh() {
        page = 1
        viewModel.getRecommendPostData(type, page)
    }
}