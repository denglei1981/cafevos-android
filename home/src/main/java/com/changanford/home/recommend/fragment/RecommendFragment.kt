package com.changanford.home.recommend.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import androidx.core.view.isVisible
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.viewpager2.widget.ViewPager2
import com.changanford.common.basic.BaseLoadSirFragment
import com.changanford.common.bean.AdBean
import com.changanford.common.bean.RecommendData
import com.changanford.common.buried.BuriedUtil
import com.changanford.common.manger.UserManger
import com.changanford.common.ui.GridSpacingItemDecoration
import com.changanford.common.util.JumpUtils
import com.changanford.common.util.bus.CircleLiveBusKey
import com.changanford.common.util.bus.LiveDataBus
import com.changanford.common.util.bus.LiveDataBusKey
import com.changanford.common.util.ext.noAnima
import com.changanford.common.util.ext.setCircular
import com.changanford.common.util.gio.GIOUtils
import com.changanford.common.util.gio.GioPageConstant
import com.changanford.common.util.toast.ToastUtils
import com.changanford.common.utilext.GlideUtils
import com.changanford.common.utilext.toastShow
import com.changanford.common.wutil.ScreenUtils
import com.changanford.home.HomeV2Fragment
import com.changanford.home.R
import com.changanford.home.adapter.RecommendAdapter
import com.changanford.home.bean.HomeTopFastBean
import com.changanford.home.data.InfoDetailsChangeData
import com.changanford.home.databinding.FragmentRecommendListBinding
import com.changanford.home.databinding.LayoutKingkongInBinding
import com.changanford.home.databinding.LayoutRecommendFastInBinding
import com.changanford.home.databinding.RecommendHeaderBinding
import com.changanford.home.recommend.adapter.RecommendBannerAdapter
import com.changanford.home.recommend.adapter.RecommendFastInListAdapter
import com.changanford.home.recommend.request.RecommendViewModel
import com.changanford.home.widget.SpacesItemDecoration
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

    private var headNewBinding: RecommendHeaderBinding? = null

    private var fastInBinding: LayoutRecommendFastInBinding? = null
    private var kingKongBinding: LayoutKingkongInBinding? = null

    private var isSecondHeader: Boolean = false
    private var headIndex = 0
    private var isAddAdBean = false

    private var topFastViews = ArrayList<HomeTopFastBean>()

    private val recommendAdapter: RecommendAdapter by lazy {
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
        binding.smartLayout.setEnableLoadMore(false)
        binding.smartLayout.setOnRefreshListener(this)
        binding.smartLayout.setOnLoadMoreListener(this)
        recommendAdapter.loadMoreModule.setOnLoadMoreListener {
            viewModel.getRecommend(true)
        }
        binding.recyclerView.noAnima()
        binding.recyclerView.layoutManager =
            LinearLayoutManager(requireActivity(), LinearLayoutManager.VERTICAL, false)
        binding.recyclerView.adapter = recommendAdapter
        recommendAdapter.setOnItemClickListener { adapter, view, position ->
            selectPosition = position
            val itemViewType = recommendAdapter.getItemViewType(position + 1)
            val item = recommendAdapter.getItem(position)
            GIOUtils.homePageClick("推荐信息流", (position + 1).toString(), item.title)
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
        viewModel.getFastEnter()
        viewModel.getKingKong()
        viewModel.getRecommend(false)

    }

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
                isSecondHeader = true
                recommendAdapter.addHeaderView(it.root, 0)
                headIndex++
                it.bViewpager.setAdapter(recommendBannerAdapter)
                it.bViewpager.setCanLoop(true)
                it.bViewpager.registerLifecycleObserver(lifecycle)
                it.bViewpager.setIndicatorView(it.drIndicator)
                it.bViewpager.setAutoPlay(true)
                it.bViewpager.setScrollDuration(500)
                it.bViewpager.setPageStyle(PageStyle.MULTI_PAGE_SCALE)
                it.bViewpager.registerOnPageChangeCallback(object :
                    ViewPager2.OnPageChangeCallback() {
                    override fun onPageSelected(position: Int) {
                        super.onPageSelected(position)
                        if (GioPageConstant.mainSecondPageName() == "发现页-推荐") {
                            val bean = it.bViewpager.data as List<AdBean>
                            val item = bean[position]
                            bean[position].adName?.let { it1 ->
                                GIOUtils.homePageExposure(
                                    "广告位banner", (position + 1).toString(),
                                    it1, item.maPlanId, item.maJourneyId, item.maJourneyActCtrlId
                                )
                            }
                        }
                    }
                })
                it.bViewpager.create()
            }
            setIndicator()
        }
    }

    private var isAddFaster: Boolean = false
    private var isAddKingKong: Boolean = false
    private var isAddGridSpace: Boolean = false
    private var isAddLinearSpace: Boolean = false

    private fun addHeadFaster(isGrid: Boolean, dataList: List<AdBean>) {
        if (fastInBinding == null) {
            fastInBinding = DataBindingUtil.inflate(
                LayoutInflater.from(requireContext()),
                R.layout.layout_recommend_fast_in,
                binding.recyclerView,
                false
            )
        }
        val fastInAdapter = RecommendFastInListAdapter()
        fastInBinding?.let { fi ->
            fi.rvFastIn.adapter = fastInAdapter
            var index = 1
            if (!isSecondHeader) {
                index = 0
            }
            if (dataList.isEmpty()) {
                fi.tvFastIn.visibility = View.GONE
            } else {
                fi.tvFastIn.visibility = View.VISIBLE
            }
            if (!isAddFaster) {
                recommendAdapter.addHeaderView(fi.root, headIndex)
                isAddFaster = true
            }
            fastInAdapter.setList(dataList)
            if (isGrid) {
                fastInAdapter.isWith = false
                fi.rvFastIn.layoutManager = GridLayoutManager(requireContext(), 3)
                if (!isAddGridSpace) {
                    fi.rvFastIn.addItemDecoration(
                        GridSpacingItemDecoration(
                            ScreenUtils.dp2px(
                                requireContext(),
                                10f
                            ), 3
                        )
                    )
                    isAddGridSpace = true
                }

            } else {
                fastInAdapter.isWith = true
                val linearLayoutManager =
                    LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
                fi.rvFastIn.layoutManager = linearLayoutManager
                if (!isAddLinearSpace) {
                    fi.rvFastIn.addItemDecoration(
                        SpacesItemDecoration(
                            ScreenUtils.dp2px(
                                requireContext(),
                                10f
                            )
                        )
                    )
                    isAddLinearSpace = true
                }
            }
        }

    }

    private fun addKingKong(dataList: List<AdBean>) {
        if (kingKongBinding == null) {
            kingKongBinding = DataBindingUtil.inflate(
                LayoutInflater.from(requireContext()),
                R.layout.layout_kingkong_in,
                binding.recyclerView,
                false
            )
        }
        kingKongBinding?.let { fi ->
            topFastViews.clear()
            topFastViews.add(HomeTopFastBean(fi.tvOne, fi.ivOne))
            topFastViews.add(HomeTopFastBean(fi.tvTwo, fi.ivTwo))
            topFastViews.add(HomeTopFastBean(fi.tvThree, fi.ivThree))
            topFastViews.add(HomeTopFastBean(fi.tvFour, fi.ivFour))
            topFastViews.add(HomeTopFastBean(fi.tvFive, fi.ivFive))
            var index = 1
            if (!isSecondHeader) {
                index = 0
            }
            if (dataList.isEmpty()) {
                fi.llFast.visibility = View.GONE
            } else {
                fi.llFast.visibility = View.VISIBLE
            }
            if (dataList.size > 5) {
                dataList.subList(0, 5)
            }
            topFastViews.forEach {
                it.shapeAbleImageView.isVisible = false
                it.textView.isVisible = false
            }
            dataList.forEachIndexed { index, adBean ->
                val bean = topFastViews[index]
                bean.shapeAbleImageView.isVisible = true
                bean.textView.isVisible = true
                bean.shapeAbleImageView.setCircular(8)
                bean.shapeAbleImageView.setOnClickListener {
                    JumpUtils.instans?.jump(adBean.jumpDataType, adBean.jumpDataValue)
                }
                GlideUtils.loadBD(adBean.adImg, bean.shapeAbleImageView)
                bean.textView.text = adBean.adSubName
            }
            fi.llTwo.isVisible = dataList.size > 2
            if (!isAddKingKong) {
                recommendAdapter.addHeaderView(fi.root, index)
                headIndex++
                isAddKingKong = true
            }
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
        viewModel.specialListLiveData.safeObserve(this) {
            if (it.extend.topicAreaConfig.indexListShow == 1) {
                val addBean = RecommendData(specialList = it)
                recommendAdapter.addData(1, addBean)
            } else {
                if (recommendAdapter.data.size > 1 && recommendAdapter.getItem(1).itemType == 5) {
                    recommendAdapter.removeAt(1)
                }
            }
        }
        viewModel.recommendAdBean.observe(this) {
//            if (!it.ads.isNullOrEmpty()){
//                val adBean = RecommendData(adBean = it.ads[0])
//                if (it.showPosition <= recommendAdapter.itemCount) {
//                    recommendAdapter.addData(it.showPosition, adBean)
//                    isAddAdBean = true
//                }
//            }
            recommendAddAds()
        }
        viewModel.recommendBannerLiveData.safeObserve(this, Observer {
            if (it.isSuccess) {
                if (it.data == null || it.data.isEmpty()) {
                    headNewBinding?.bViewpager?.visibility = View.GONE
                    headNewBinding?.drIndicator?.visibility = View.GONE
                } else {
                    headNewBinding?.bViewpager?.visibility = View.VISIBLE
                    headNewBinding?.drIndicator?.visibility = View.VISIBLE
                }
                headNewBinding?.bViewpager?.refreshData(it.data)
                if (!it.data.isNullOrEmpty()) {
                    val item = it.data[0]
                    it.data[0].adName?.let { it1 ->
                        GIOUtils.homePageExposure(
                            "广告位banner", 1.toString(),
                            it1, item.maPlanId, item.maJourneyId, item.maJourneyActCtrlId
                        )
                    }
                }

            } else {
                headNewBinding?.bViewpager?.visibility = View.GONE
                headNewBinding?.drIndicator?.visibility = View.GONE
            }
        })
        viewModel.fastEnterLiveData.safeObserve(this) {
            if (it.isSuccess) {
                when (it.data.showType) {
                    "SINGLE" -> {
                        addHeadFaster(false, it.data.ads)
                    }

                    "MULTI" -> {
                        addHeadFaster(true, it.data.ads)
                    }

                    else -> {
                        addHeadFaster(true, it.data.ads)
                    }
                }
            }
        }
        viewModel.kingKongLiveData.safeObserve(this) {
            if (it.isSuccess) {
                when (it.data.showType) {
                    "SINGLE" -> {
                        addKingKong(it.data.ads)
                    }

                    "MULTI" -> {
                        addKingKong(it.data.ads)
                    }

                    else -> {
                        addKingKong(it.data.ads)
                    }
                }
            }
        }
    }

    private fun toPostOrNews(item: RecommendData) { // 跳转到资讯，或者 帖子
        when (item.rtype) {//  val rtype: Int, // rtype 推荐业务类型 1 资讯 2 帖子 3 活动
            1 -> {
                if (item.authors != null) {
//                    val newsValueData = NewsValueData(item.artId, item.artType)
//                    val values = Gson().toJson(newsValueData)
                    GioPageConstant.infoEntrance = "发现-推荐-信息流"
                    JumpUtils.instans?.jump(2, item.artId)
                    // 埋点--- 资讯名称。
                    BuriedUtil.instant?.discoverNews(item.getTopic())
                } else {
                    toastShow("没有作者")
                }
            }

            2 -> {
                // todo 跳转到帖子
//                bundle.putString("postsId", value)
//                startARouter(ARouterCirclePath.PostDetailsActivity, bundle)
                GioPageConstant.postEntrance = "发现-推荐-信息流"
                JumpUtils.instans!!.jump(4, item.postsId)
                // 埋点--- 资讯名称。
                BuriedUtil.instant?.discoverPost(item.getTopic())
            }
        }
    }

    private fun toActs(data: RecommendData) {
        var item = data.wonderful
        GioPageConstant.infoEntrance = "发现-推荐-信息流"
        try {
//            CommonUtils.jumpActDetail(item.jumpType.toInt(), item.jumpValue)
//            if (item.jumpType.toIntOrNull() == 2 || item.jumpType.toIntOrNull() == 1) {
//                item.wonderfulType?.let { viewModel.AddACTbrid(it) }
//            }
            JumpUtils.instans?.jump(item.jumpDto.jumpCode, item.jumpDto.jumpVal)
//                if (item.jumpType == 2||item.jumpType==1) {
            if (item.outChain == "YES") {
                item.wonderfulType?.let { viewModel.AddACTbrid(it) }
            }
            BuriedUtil.instant?.discoverAct(item.title)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun bus() {
        LiveDataBus.get().withs<Int>(CircleLiveBusKey.REFRESH_POST_LIKE).observe(this) {
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
            // TODO 要取变量了。
            recommendAdapter.notifyItemChanged(selectPosition + 1)//有t
        }

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
                // TODO 要取变量了。
                recommendAdapter.notifyItemChanged(selectPosition + 1)// 有t
                if (item.authors?.isFollow != it.isFollow) {
                    // 关注不相同，以详情的为准。。
                    if (item.authors != null) {
                        recommendAdapter.notifyAtt(item.authors!!.authorId, it.isFollow)
                    }
                }
            })

        LiveDataBus.get().withs<Int>(CircleLiveBusKey.REFRESH_FOLLOW_USER).observe(this, Observer {
            if (selectPosition == -1) {
                return@Observer
            }
            val bean = recommendAdapter.getItem(selectPosition)
            if (bean.authors?.isFollow != it) { // 关注不相同，以详情的为准。。
                if (bean.authors != null) {
                    recommendAdapter.notifyAtt(bean.authors!!.authorId, it)
                }
            }
        })
        //登录回调
        LiveDataBus.get()
            .with(LiveDataBusKey.USER_LOGIN_STATUS, UserManger.UserLoginStatus::class.java)
            .observe(this, Observer {
                // 收到 登录状态改变回调都要刷新页面
                homeRefersh()
            })

        LiveDataBus.get().with(LiveDataBusKey.LIST_FOLLOW_CHANGE).observe(this, Observer {
            homeRefersh()
        })
    }

    override fun initData() {
        viewModel.recommendLiveData.safeObserve(this, Observer {
            if (it.isSuccess) {
                val dataList = it.data.dataList
                if (it.isLoadMore) {
                    recommendAdapter.addData(dataList)
                    binding.smartLayout.finishLoadMore()
                    //设置状态完成
                    recommendAdapter.loadMoreModule.loadMoreComplete()
                    recommendAddAds()
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
                if (viewModel.pageNo >= it.data.totalPage) {
                    binding.smartLayout.setEnableLoadMore(false)
                    recommendAdapter.loadMoreModule.loadMoreEnd()
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
                recommendAdapter.loadMoreModule.loadMoreComplete()
            }

        })
    }

    private fun recommendAddAds() {
        if (!isAddAdBean) {
            val adData = viewModel.recommendAdBean.value
            adData?.let {
                if (!adData.ads.isNullOrEmpty()) {
                    val adBean = RecommendData(adBean = adData.ads[0])
                    if (adData.showPosition <= recommendAdapter.itemCount) {
                        for (i in 0 until adData.showPosition) {
                            val itemType = recommendAdapter.getItem(i).getItemTypeLocal()
                            if (itemType == 4 || itemType == 5) {
                                adData.showPosition++
                            }
                        }
                        if (adData.showPosition < recommendAdapter.itemCount) {
                            val itemType =
                                recommendAdapter.getItem(adData.showPosition).getItemTypeLocal()
                            if (itemType == 4 || itemType == 5) {
                                adData.showPosition++
                            }
                        }
                        if (adData.showPosition <= recommendAdapter.itemCount) {
                            recommendAdapter.addData(adData.showPosition, adBean)
                            isAddAdBean = true
                        }
                    }
                }
            }
        }
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
        isAddAdBean = false
        headIndex = 0
        viewModel.getRecommendBanner()
        viewModel.getRecommend(false)
        viewModel.getFastEnter()
        viewModel.getKingKong()
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




