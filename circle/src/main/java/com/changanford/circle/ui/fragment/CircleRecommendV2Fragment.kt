package com.changanford.circle.ui.fragment

import android.os.Bundle
import android.view.View
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.viewpager2.widget.ViewPager2
import com.changanford.circle.R
import com.changanford.circle.adapter.CircleAdBannerAdapter
import com.changanford.circle.adapter.CircleRecommendAdapter
import com.changanford.circle.databinding.FragmentCircleRecommendV2Binding
import com.changanford.circle.databinding.LayoutCircleHeaderHotTopicBinding
import com.changanford.circle.viewmodel.CircleDetailsViewModel
import com.changanford.common.adapter.PolySearchTopicAdapter
import com.changanford.common.basic.BaseFragment
import com.changanford.common.bean.AdBean
import com.changanford.common.manger.UserManger
import com.changanford.common.router.path.ARouterCirclePath
import com.changanford.common.router.startARouter
import com.changanford.common.util.JumpUtils
import com.changanford.common.util.MConstant
import com.changanford.common.util.bus.CircleLiveBusKey
import com.changanford.common.util.bus.LiveDataBus
import com.changanford.common.util.bus.LiveDataBusKey
import com.changanford.common.util.gio.GIOUtils
import com.changanford.common.util.gio.GioPageConstant
import com.youth.banner.util.BannerUtils
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
    private val topicAdapter by lazy {
        PolySearchTopicAdapter(false)
    }
    private val tabList = listOf("推荐", "最新")
    private lateinit var headBinding: LayoutCircleHeaderHotTopicBinding
    private val tabMutable = MutableLiveData<Int>()
    private val headView by lazy {
        layoutInflater.inflate(R.layout.layout_circle_header_hot_topic, null)
    }

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
        headBinding = DataBindingUtil.bind(headView)!!
        initMagicIndicator()
        headBinding.ryTopic.adapter = topicAdapter
        adapter.addHeaderView(headView)
        tabMutable.value = arguments?.getInt("type", 1)!!
        binding.ryCircle.run {
            val layoutManager = binding.ryCircle.layoutManager as LinearLayoutManager
            layoutManager.initialPrefetchItemCount = 4
        }
        binding.ryCircle.adapter = adapter
        adapter.loadMoreModule.setOnLoadMoreListener {
            page++
            viewModel.getRecommendPostData(tabMutable.value!!, page)
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
        binding.refreshLayout.setOnRefreshListener {
            initData()
        }
        topicAdapter.setOnItemClickListener { _, _, position ->
            val bean = topicAdapter.data[position]
            JumpUtils.instans?.jump(9, bean.topicId.toString())
        }
        initVpAd()
        bus()
    }

    private fun initMagicIndicator() {
        headBinding.apply {
            tvRecommend.setOnClickListener {
                if (tabMutable.value == 1) return@setOnClickListener
                tvRecommend.textSize = 18f
                tvNew.textSize = 16f
                tvRecommend.setTextColor(
                    ContextCompat.getColor(
                        requireContext(),
                        R.color.color_1700F4
                    )
                )
                tvNew.setTextColor(ContextCompat.getColor(requireContext(), R.color.color_8016))
                tabMutable.value = 1
            }
            tvNew.setOnClickListener {
                if (tabMutable.value == 2) return@setOnClickListener
                tvRecommend.textSize = 16f
                tvNew.textSize = 18f
                tvRecommend.setTextColor(
                    ContextCompat.getColor(
                        requireContext(),
                        R.color.color_8016
                    )
                )
                tvNew.setTextColor(ContextCompat.getColor(requireContext(), R.color.color_1700F4))
                tabMutable.value = 2
            }
        }
        tabMutable.observe(this) {
            page = 1
            if (isFirstInitRecommend) {
                isFirstInitRecommend = false
                return@observe
            }
            viewModel.getRecommendPostData(it, page, true)
        }
    }

    private var isFirstInitRecommend = true

    override fun initData() {
        var useTabValue = 1
        if (tabMutable.value == null) {
            useTabValue = 1
        }
        tabMutable.value?.let {
            useTabValue = it
        }
        viewModel.getRecommendTopic()
        viewModel.communityTopic()
        viewModel.getRecommendPostData(useTabValue, page)
    }


    override fun observe() {
        super.observe()
//        viewModel.topSignBean.observe(this) {
//            headBinding.run {
//                it.ontinuous?.let {
//                    val days = it.toInt()
//                    tvDaysNum.isVisible = days > 0
//                }
//                tvDaysNum.text = "已连续签到${it.ontinuous}天"
//                it.curDate?.let { ss ->
//                    tvDays.text = TimeUtils.MillisToStrHM2(it.curDate)
//                }
//            }
//        }
        viewModel.topicBean.observe(this) {
            topicAdapter.setList(it.topics)
        }
        viewModel.circleAdBean.observe(this) {
            if (it.isNotEmpty()) {
                headBinding.bViewpager.visibility = View.VISIBLE
                if (headBinding.bViewpager.data.isNullOrEmpty()) {
                    headBinding.bViewpager.refreshData(it)
                } else {
                    refreshViewPager(it)
                }
                if (!it.isNullOrEmpty()) {
                    val item = it[0]
                    it[0].adName?.let { it1 ->
                        GIOUtils.homePageExposure(
                            "广告位banner", 1.toString(),
                            it1, item.maPlanId, item.maJourneyId, item.maJourneyActCtrlId
                        )
                    }
                }
            } else {
                headBinding.bViewpager.visibility = View.GONE
            }

        }
        LiveDataBus.get().withs<Boolean>(CircleLiveBusKey.REFRESH_CIRCLE_MAIN).observe(this) {

        }
        LiveDataBus.get().with(LiveDataBusKey.MINE_SIGN_SIGNED).observe(this) {
            onResume()
        }
        viewModel.recommondBean.observe(this) {
            if (page == 1) {
                binding.ryCircle.visibility = View.VISIBLE
                binding.topView.visibility = View.GONE
                adapter.setList(it.dataList)
                binding.refreshLayout.finishRefresh()
            } else {
                adapter.addData(it.dataList)
                adapter.loadMoreModule.loadMoreComplete()
            }
            if (it.dataList.size == 0) {
                adapter.loadMoreModule.loadMoreEnd()
            }
        }
        //登录回调
        LiveDataBus.get()
            .with(LiveDataBusKey.USER_LOGIN_STATUS, UserManger.UserLoginStatus::class.java)
            .observe(this) {
                page = 1
                tabMutable.value?.let { it1 -> viewModel.getRecommendPostData(it1, page) }
            }

    }

    private fun initVpAd() {

        headBinding.let {

            it.ivTopicRight.setOnClickListener {
                startARouter(ARouterCirclePath.HotTopicActivity)
            }
            it.bViewpager.visibility = View.GONE
            val recommendAdAdapter = CircleAdBannerAdapter()
            it.bViewpager.setAdapter(recommendAdAdapter)
            it.bViewpager.setCanLoop(true)
            it.bViewpager.setPageMargin(20)
            it.bViewpager.setRevealWidth(BannerUtils.dp2px(10f))
            it.bViewpager.setPageStyle(PageStyle.MULTI_PAGE)
            it.bViewpager.registerLifecycleObserver(lifecycle)
            it.bViewpager.setIndicatorView(it.drIndicator)
            it.bViewpager.setAutoPlay(true)
            it.bViewpager.setScrollDuration(500)
            it.bViewpager.setPageStyle(PageStyle.MULTI_PAGE_SCALE)
            it.bViewpager.create()

            it.bViewpager.registerOnPageChangeCallback(object :
                ViewPager2.OnPageChangeCallback() {
                override fun onPageSelected(position: Int) {
                    super.onPageSelected(position)
                    if (it.bViewpager.visibility == View.VISIBLE) {
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
        }
        setIndicator()
    }

    private fun refreshViewPager(listBean: List<AdBean>) {
        headBinding.let {
            val recommendAdAdapter = CircleAdBannerAdapter()
            it.bViewpager.setAdapter(recommendAdAdapter)
            it.bViewpager.setCanLoop(true)
            it.bViewpager.setPageMargin(20)
            it.bViewpager.setRevealWidth(BannerUtils.dp2px(10f))
            it.bViewpager.setPageStyle(PageStyle.MULTI_PAGE)
            it.bViewpager.registerLifecycleObserver(lifecycle)
            it.bViewpager.setIndicatorView(it.drIndicator)
            it.bViewpager.setAutoPlay(true)
            it.bViewpager.setScrollDuration(500)
            it.bViewpager.setPageStyle(PageStyle.MULTI_PAGE_SCALE)
            it.bViewpager.create()

            it.bViewpager.registerOnPageChangeCallback(object :
                ViewPager2.OnPageChangeCallback() {
                override fun onPageSelected(position: Int) {
                    super.onPageSelected(position)
                    if (it.bViewpager.visibility == View.VISIBLE) {
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
        }
        headBinding.bViewpager.refreshData(listBean)
        setIndicator()
    }

    private fun setIndicator() {
        val dp6 = requireContext().resources.getDimensionPixelOffset(R.dimen.dp_6)
        headBinding.drIndicator.setIndicatorDrawable(
            R.drawable.shape_circle_banner_normal,
            R.drawable.shape_circle_banner_focus
        )
            .setIndicatorSize(
                dp6,
                dp6,
                requireContext().resources.getDimensionPixelOffset(R.dimen.dp_20),
                dp6
            )
            .setIndicatorGap(requireContext().resources.getDimensionPixelOffset(R.dimen.dp_5))
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
    }

    override fun onResume() {
        super.onResume()
        GioPageConstant.topicEntrance = ""
//        viewModel.getSignContinuousDays()
        checkSign()
    }

    private fun checkSign() {
        viewModel.getDay7Sign { daySignBean ->
            var canSign = daySignBean == null || MConstant.token.isNullOrEmpty()
            daySignBean?.sevenDays?.forEach {
                if (it.signStatus == 2) {
                    canSign = true
                }
            }
//            if (!canSign) {
//                headBinding.tvSign.run {
//                    setBackgroundResource(R.drawable.shape_e9_15dp)
//                    text = "已签到"
//                    isEnabled = false
//                    setTextColor(ContextCompat.getColor(requireContext(), R.color.color_4d16))
//                }
//            } else {
//                headBinding.tvSign.run {
//                    setBackgroundResource(R.drawable.bg_sign_top_topic)
//                    text = "签到得福币"
//                    isEnabled = true
//                    setTextColor(ContextCompat.getColor(requireContext(), R.color.white))
//                    if (MConstant.userId.isNotEmpty()) {
//                        setOnClickListener {
//                            JumpUtils.instans?.jump(37)
//                        }
//                    } else {
//                        setOnClickListener { startARouter(ARouterMyPath.SignUI) }
//                    }
//                }
//            }
        }
    }

    fun outRefresh() {
        page = 1
        tabMutable.value?.let { viewModel.getRecommendPostData(it, page) }
    }
}