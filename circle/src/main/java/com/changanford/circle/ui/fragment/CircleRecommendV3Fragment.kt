package com.changanford.circle.ui.fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.paging.LoadState
import androidx.paging.PagingData
import androidx.paging.map
import com.changanford.circle.R
import com.changanford.circle.adapter.CircleAdBannerAdapter
import com.changanford.circle.adapter.CircleRecommendAdapter
import com.changanford.circle.adapter.CircleRecommendHotTopicAdapter
import com.changanford.circle.adapter.ItemCircleRecommendView
import com.changanford.circle.databinding.FragmentCircleRecommendBinding
import com.changanford.circle.databinding.FragmentCircleRecommendV2Binding
import com.changanford.circle.databinding.FragmentCircleV2Binding
import com.changanford.circle.databinding.LayoutCircleHeaderHotTopicBinding
import com.changanford.circle.utils.MUtils
import com.changanford.circle.viewmodel.CircleDetailsViewModel
import com.changanford.common.basic.BaseFragment
import com.changanford.common.basic.adapter.OnRecyclerViewItemClickListener
import com.changanford.common.bean.PostDataBean
import com.changanford.common.manger.UserManger
import com.changanford.common.paging.HeadAdapter
import com.changanford.common.paging.LoadMoreAdapter
import com.changanford.common.paging.PagingAdapter
import com.changanford.common.paging.PagingItemView
import com.changanford.common.router.path.ARouterCirclePath
import com.changanford.common.router.startARouter
import com.changanford.common.util.bus.CircleLiveBusKey
import com.changanford.common.util.bus.LiveDataBus
import com.changanford.common.util.bus.LiveDataBusKey
import com.changanford.common.util.gio.GIOUtils
import com.changanford.common.util.gio.GioPageConstant
import com.scwang.smart.refresh.layout.api.RefreshLayout
import com.scwang.smart.refresh.layout.listener.OnRefreshListener
import com.xiaomi.push.it
import com.zhpan.bannerview.constants.PageStyle
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

/**
 *Author lcw
 *Time on 2023/2/2
 *Purpose
 */
class CircleRecommendV3Fragment :
    BaseFragment<FragmentCircleRecommendV2Binding, CircleDetailsViewModel>() {


//    private lateinit var mCheckForGapMethod: Method

    //    private val adapter by lazy { CircleRecommendAdapter(requireContext(), this) }
    private val adapter by lazy {
        PagingAdapter(requireActivity())
    }

    private var type = 0
    private var page = 1

    private var checkPosition: Int? = null

    companion object {
        fun newInstance(type: Int): CircleRecommendV3Fragment {
            val bundle = Bundle()
            bundle.putInt("type", type)
            val fragment = CircleRecommendV3Fragment()
            fragment.arguments = bundle
            return fragment
        }
    }

    override fun initView() {
        type = arguments?.getInt("type", 1)!!
        viewModel.recommendType = type
//        MUtils.scrollStopLoadImage(binding.ryCircle)

        binding.ryCircle.adapter =
            adapter.withLoadStateHeaderAndFooter(HeadAdapter(), LoadMoreAdapter { adapter.retry() })

//        binding.ryCircle.adapter = adapter

        adapter.setOnItemClickListener(object : OnRecyclerViewItemClickListener {
            override fun onItemClick(view: View?, position: Int) {
//                GioPageConstant.postEntrance = "社区-广场-信息流"
//                val bundle = Bundle()
//                bundle.putString("postsId", adapter.getItem(position).postsId.toString())
//                startARouter(ARouterCirclePath.PostDetailsActivity, bundle)
//                checkPosition = position
//                GIOUtils.homePageClick(
//                    "广场信息流",
//                    (position + 1).toString(),
//                    adapter.getItem(position).title
//                )
            }

        })
        viewModel.communityTopic()
        bus()

    }

    override fun initData() {
//        viewModel.getRecommendPostData(type, 1)

    }


    override fun observe() {
        super.observe()
        lifecycleScope.launch {
            viewModel.pager.collect { it ->
                binding.ryCircle.visibility = View.VISIBLE
                binding.topView.visibility = View.GONE
                adapter.submitData(it.map {
                    ItemCircleRecommendView(
                        it,
                        this@CircleRecommendV3Fragment
                    ) as PagingItemView<Any>
                })
            }
        }
        //登录回调
        LiveDataBus.get()
            .with(LiveDataBusKey.USER_LOGIN_STATUS, UserManger.UserLoginStatus::class.java)
            .observe(this) {
                viewModel.getRecommendPostData(type, 1)
            }

    }

    private fun bus() {
//        LiveDataBus.get().withs<Int>(CircleLiveBusKey.REFRESH_POST_LIKE).observe(this) {
//            val bean = checkPosition?.let { it1 -> adapter.getItem(it1) }
//            bean?.let { _ ->
//                bean.isLike = it
//                if (bean.isLike == 1) {
//                    bean.likesCount++
//                } else {
//                    bean.likesCount--
//                }
//            }
//            checkPosition?.let { it1 -> adapter.notifyItemChanged(it1) }
//        }
//        LiveDataBus.get().withs<Boolean>(CircleLiveBusKey.REFRESH_CIRCLE_BOTTOM_FRAGMENT)
//            .observe(this, {
//                page = 1
//                viewModel.getData(type, page)
//            })
        LiveDataBus.get().withs<Boolean>(CircleLiveBusKey.DELETE_CIRCLE_POST).observe(this) {
//            checkPosition?.let { it1 -> adapter.data.removeAt(it1) }
            checkPosition?.let { it1 -> adapter.notifyItemRemoved(it1) }
            checkPosition?.let { it1 -> adapter.notifyItemRangeChanged(it1, adapter.itemCount) }
        }
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