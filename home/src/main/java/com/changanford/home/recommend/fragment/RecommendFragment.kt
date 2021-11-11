package com.changanford.home.recommend.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.viewpager2.widget.ViewPager2
import com.changanford.common.basic.BaseLoadSirFragment
import com.changanford.common.bean.RecommendData
import com.changanford.common.manger.UserManger
import com.changanford.common.util.CommonUtils
import com.changanford.common.util.JumpUtils
import com.changanford.common.util.bus.CircleLiveBusKey
import com.changanford.common.util.bus.LiveDataBus
import com.changanford.common.util.bus.LiveDataBusKey
import com.changanford.common.util.toast.ToastUtils
import com.changanford.common.utilext.toastShow
import com.changanford.home.HomeV2Fragment
import com.changanford.home.PageConstant
import com.changanford.home.R
import com.changanford.home.adapter.RecommendAdapter
import com.changanford.home.data.InfoDetailsChangeData
import com.changanford.home.databinding.FragmentRecommendListBinding
import com.changanford.home.databinding.RecommendHeaderBinding
import com.changanford.home.recommend.adapter.RecommendBannerAdapter
import com.changanford.home.recommend.request.RecommendViewModel
import com.scwang.smart.refresh.layout.api.RefreshLayout
import com.scwang.smart.refresh.layout.listener.OnLoadMoreListener
import com.scwang.smart.refresh.layout.listener.OnRefreshListener
import com.zhpan.bannerview.constants.PageStyle


/**
 *  推荐列表
 * */
open class RecommendFragment :
    BaseLoadSirFragment<FragmentRecommendListBinding, RecommendViewModel>(),
    OnLoadMoreListener, OnRefreshListener {
    val recommendAdapter: RecommendAdapter by lazy {
        RecommendAdapter(this)
    }
    companion object {
        fun newInstance(): RecommendFragment {
            val fg = RecommendFragment()
            val bundle = Bundle()
            fg.arguments = bundle
            return fg
        }
    }
    var selectPosition = -1
    override fun initView() {
        binding.smartLayout.setEnableRefresh(true)
        binding.smartLayout.setOnRefreshListener(this)
        binding.recyclerView.layoutManager =
            LinearLayoutManager(requireActivity(), LinearLayoutManager.VERTICAL, false)
        binding.recyclerView.adapter = recommendAdapter
        recommendAdapter.setOnItemClickListener { adapter, view, position ->
            selectPosition = position
            val itemViewType = recommendAdapter.getItemViewType(position+1)
            val item = recommendAdapter.getItem(position)
            when (itemViewType) {
                3 -> { // 跳转到活动
                    toActs(item)
                }
                else -> {
                    toPostOrNews(item)
                }
            }
        }
        setLoadSir(binding.smartLayout)
        addHeadView()
        viewModel.getRecommendBanner()
        viewModel.getRecommend(false)
    }

    var headNewBinding: RecommendHeaderBinding? = null

    private fun addHeadView() {
        if (headNewBinding == null) {
            headNewBinding = DataBindingUtil.inflate(
                LayoutInflater.from(requireContext()),
                R.layout.recommend_header,
                binding.recyclerView,
                false
            )
            val recommendBannerAdapter = RecommendBannerAdapter()
            headNewBinding?.let {
                recommendAdapter.addHeaderView(it.root, 0)
                it.bViewpager.setAdapter(recommendBannerAdapter)
                it.bViewpager.setCanLoop(true)
                it.bViewpager.setIndicatorView(it.drIndicator)
                it.bViewpager.setAutoPlay(true)
                it.bViewpager.setScrollDuration(500)
                it.bViewpager.setPageStyle(PageStyle.MULTI_PAGE_SCALE)
                it.bViewpager.create()
            }
            setIndicator()
        }
    }

    /**
     * 设置指示器
     * */
    private fun setIndicator() {
        val dp6 = resources.getDimensionPixelOffset(R.dimen.dp_6)
        headNewBinding?.drIndicator?.setIndicatorDrawable(
            R.drawable.shape_home_banner_normal,
            R.drawable.shape_home_banner_focus
        )
            ?.setIndicatorSize(dp6, dp6, resources.getDimensionPixelOffset(R.dimen.dp_20), dp6)
            ?.setIndicatorGap(resources.getDimensionPixelOffset(R.dimen.dp_5))
    }

    override fun observe() {
        super.observe()
        bus()
        viewModel.recommendBannerLiveData.observe(this, Observer {
            if (it.isSuccess) {
                if (it.data == null || it.data.isEmpty()) {
                    headNewBinding?.bViewpager?.visibility = View.GONE
                    headNewBinding?.drIndicator?.visibility = View.GONE
                } else {
                    headNewBinding?.bViewpager?.visibility = View.VISIBLE
                    headNewBinding?.drIndicator?.visibility = View.VISIBLE
                }
                headNewBinding?.bViewpager?.refreshData(it.data)

            } else {
                //
                headNewBinding?.bViewpager?.visibility = View.GONE
                headNewBinding?.drIndicator?.visibility = View.GONE
            }

        })
    }

    private fun toPostOrNews(item: RecommendData) { // 跳转到资讯，或者 帖子
        when (item.rtype) {//  val rtype: Int, // rtype 推荐业务类型 1 资讯 2 帖子 3 活动
            1 -> {
                if (item.authors != null) {
//                    val newsValueData = NewsValueData(item.artId, item.artType)
//                    val values = Gson().toJson(newsValueData)
                    JumpUtils.instans?.jump(2, item.artId)
                } else {
                    toastShow("没有作者")
                }
            }
            2 -> {
                // todo 跳转到帖子
//                bundle.putString("postsId", value)
//                startARouter(ARouterCirclePath.PostDetailsActivity, bundle)
                JumpUtils.instans!!.jump(4, item.postsId)
            }
        }
    }

    private fun toActs(item: RecommendData) {
        try {
            CommonUtils.jumpActDetail(item.jumpType.toInt(), item.jumpVal)
            if (item.jumpType.toIntOrNull() == 2||item.jumpType.toIntOrNull()==1) {
                item.wonderfulType?.let { viewModel.AddACTbrid(it) }
            }
        }catch (e:Exception){
            e.printStackTrace()
        }
    }

    private fun bus() {
        LiveDataBus.get().withs<Int>(CircleLiveBusKey.REFRESH_POST_LIKE).observe(this, {
            if (selectPosition == -1) {
                return@observe
            }
            val bean = recommendAdapter.getItem(selectPosition)
            bean.isLike = it
            if (bean.isLike == 1) {
                bean.postsLikesCount++
            } else {
                bean.postsLikesCount--
            }
            recommendAdapter.notifyItemChanged(selectPosition)
        })

        LiveDataBus.get().withs<InfoDetailsChangeData>(LiveDataBusKey.NEWS_DETAIL_CHANGE)
            .observe(this, Observer {
                // 主要是改，点赞，评论， 浏览记录。。。
                if (selectPosition == -1) {
                    return@Observer
                }
                val item = recommendAdapter.getItem(selectPosition)
                item.artLikesCount = it.likeCount
                item.isLike = it.isLike
                item.commentCount = it.msgCount
                recommendAdapter.notifyItemChanged(selectPosition)// 有t
                if (item.authors?.isFollow != it.isFollow) {
                    // 关注不相同，以详情的为准。。
                    if (item.authors != null) {
                        recommendAdapter.notifyAtt(item.authors!!.authorId, it.isFollow)
                    }
                }
            })

        LiveDataBus.get().withs<Int>(CircleLiveBusKey.REFRESH_FOLLOW_USER).observe(this, {
            if (selectPosition == -1) {
                return@observe
            }
            val bean = recommendAdapter.getItem(selectPosition)
            if (bean.authors?.isFollow != it) { // 关注不相同，以详情的为准。。
                if (bean.authors != null) {
                    recommendAdapter.notifyAtt(bean.authors!!.authorId, it)
                }
            }
        })
        //登录回调
        LiveDataBus.get().with(LiveDataBusKey.USER_LOGIN_STATUS, UserManger.UserLoginStatus::class.java)
            .observe(this,{
                // 收到 登录状态改变回调都要刷新页面
                homeRefersh()
            })

        LiveDataBus.get().with(LiveDataBusKey.LIST_FOLLOW_CHANGE).observe(this, Observer {
            homeRefersh()
        })
    }
    override fun initData() {
        viewModel.recommendLiveData.observe(this, Observer {
            if (it.isSuccess) {
                val dataList = it.data.dataList
                if (it.isLoadMore) {
                    recommendAdapter.addData(dataList)
                    binding.smartLayout.finishLoadMore()
                } else {
                    if (it.data == null || dataList.size == 0) {
                        showEmpty()
                    }
                    showContent()
                    recommendAdapter.setNewInstance(dataList)
                    binding.smartLayout.finishRefresh()
//                    (parentFragment as HomeV2Fragment).stopRefresh()
//                    (parentFragment as HomeV2Fragment).openTwoLevel()
                }
                if (it.data.dataList.size < PageConstant.DEFAULT_PAGE_SIZE_THIRTY) {
                    binding.smartLayout.setEnableLoadMore(false)
                } else {
                    binding.smartLayout.setEnableLoadMore(true)
                }
            } else {
                when (it.message) {
                    getString(R.string.net_error) -> {
                        showTimeOut()
                    }
                    else -> {
                        showFailure(it.message)
                    }
                }
                // 刷新也得停
                (parentFragment as HomeV2Fragment).stopRefresh()
                ToastUtils.showShortToast(it.message, requireContext())
            }

        })
    }

    open fun homeRefersh() {
        viewModel.getRecommend(false)
    }

    override fun onLoadMore(refreshLayout: RefreshLayout) {
        viewModel.getRecommend(true)
    }

    override fun onRetryBtnClick() {
        viewModel.getRecommend(false)
    }

    override fun onRefresh(refreshLayout: RefreshLayout) {
        viewModel.getRecommend(false)
    }

    override fun onPause() {
        super.onPause()
        headNewBinding?.bViewpager?.stopLoop()
    }

    override fun onResume() {
        super.onResume()
        headNewBinding?.bViewpager?.startLoop()
    }

    override fun onDestroy() {
        super.onDestroy()
        headNewBinding?.bViewpager?.stopLoop()
    }
}