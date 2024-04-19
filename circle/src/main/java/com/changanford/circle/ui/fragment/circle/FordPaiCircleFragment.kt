package com.changanford.circle.ui.fragment.circle

import android.os.Bundle
import androidx.databinding.DataBindingUtil
import com.changanford.circle.R
import com.changanford.circle.adapter.CircleRecommendAdapterV2
import com.changanford.circle.databinding.FragmentFordPaiCircleBinding
import com.changanford.circle.databinding.FragmentNewFordPaiCircleBinding
import com.changanford.circle.utils.CommunityHotHelper
import com.changanford.circle.viewmodel.circle.NewCircleViewModel
import com.changanford.common.basic.BaseFragment
import com.changanford.common.manger.UserManger
import com.changanford.common.router.path.ARouterCirclePath
import com.changanford.common.router.startARouter
import com.changanford.common.util.bus.CircleLiveBusKey
import com.changanford.common.util.bus.LiveDataBus
import com.changanford.common.util.bus.LiveDataBusKey
import com.changanford.common.util.gio.GIOUtils
import com.changanford.common.util.gio.updateCircleDetailsData


/**
 * @author: niubobo
 * @date: 2024/3/12
 * @description：
 */
class FordPaiCircleFragment : BaseFragment<FragmentNewFordPaiCircleBinding, NewCircleViewModel>() {

    private var checkPosition: Int? = null
    private lateinit var headBinding: FragmentFordPaiCircleBinding
    private val headView by lazy {
        layoutInflater.inflate(R.layout.fragment_ford_pai_circle, null)
    }
    private val adapter by lazy {
        CircleRecommendAdapterV2(requireContext(), this)
    }
    private lateinit var communityHotHelper: CommunityHotHelper
    private var isLoginChange = false
    private var isFirst = true

    override fun initView() {

    }

    private fun initListener() {

        binding.srl.setOnRefreshListener {
//            page = 1
//            communityCircleHelper.initCommunity(nowCircleId)
            communityHotHelper.initData()
            it.finishRefresh()
        }
        adapter.setOnItemClickListener { _, view, position ->
            GIOUtils.circleDetailPageResourceClick(
                "帖子信息流",
                (position + 1).toString(),
                adapter.getItem(position).title
            )
            updateCircleDetailsData(adapter.getItem(position).title.toString(), "帖子详情页")
            val bundle = Bundle()
            bundle.putString("postsId", adapter.getItem(position).postsId.toString())
            startARouter(ARouterCirclePath.PostDetailsActivity, bundle)
            checkPosition = position
        }
    }

    override fun initData() {

    }


    override fun observe() {
        super.observe()

    }

    private fun initMyObServe() {
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
    }

    override fun onResume() {
        super.onResume()
        if (isFirst) {
            isFirst = false
            binding.ryFragment.adapter = adapter
            headBinding = DataBindingUtil.bind(headView)!!
            adapter.addHeaderView(headView)
            communityHotHelper = CommunityHotHelper(
                headBinding.layoutHot,
                headBinding,
                viewModel,
                this@FordPaiCircleFragment
            )
            communityHotHelper.initCommunity()

            initListener()
            addLiveDataBus()
            adapter.headerWithEmptyEnable = true

            initMyObServe()
        }
        if (isLoginChange) {
            isLoginChange = false
            communityHotHelper.initData()
        }
    }

    private fun addLiveDataBus() {
        //登录回调
        LiveDataBus.get()
            .with(LiveDataBusKey.USER_LOGIN_STATUS, UserManger.UserLoginStatus::class.java)
            .observe(this) {
                when (it) {
                    UserManger.UserLoginStatus.USER_LOGIN_SUCCESS -> {
                        isLoginChange = true
                    }

                    UserManger.UserLoginStatus.USER_LOGIN_OUT -> {
                        isLoginChange = true
                    }

                    else -> {}
                }
            }
    }
}