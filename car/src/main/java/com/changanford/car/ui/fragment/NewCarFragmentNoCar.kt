package com.changanford.car.ui.fragment

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.animation.AccelerateInterpolator
import android.view.animation.DecelerateInterpolator
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.changanford.car.CarViewModel
import com.changanford.car.R
import com.changanford.car.adapter.CarNotAdapter
import com.changanford.car.adapter.NewCarTopBannerAdapter
import com.changanford.car.control.CarControl
import com.changanford.car.databinding.CarFragmentBottomBinding
import com.changanford.car.databinding.CarFragmentTopBinding
import com.changanford.car.databinding.FragmentCarBinding
import com.changanford.car.databinding.HeaderCarBinding
import com.changanford.common.basic.BaseFragment
import com.changanford.common.bean.NewCarBannerBean
import com.changanford.common.bean.NewCarInfoBean
import com.changanford.common.manger.UserManger
import com.changanford.common.util.FastClickUtils
import com.changanford.common.util.JumpUtils
import com.changanford.common.util.MConstant
import com.changanford.common.util.bus.LiveDataBus
import com.changanford.common.util.bus.LiveDataBusKey
import com.changanford.common.util.gio.GIOUtils
import com.changanford.common.util.gio.GioPageConstant
import com.changanford.common.utilext.StatusBarUtil
import com.changanford.common.utilext.toIntPx
import com.changanford.common.widget.title.CarScaleTransitionPagerTitleView
import com.changanford.common.wutil.wLogE
import com.dueeeke.videoplayer.player.VideoView
import com.gyf.immersionbar.ImmersionBar
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import net.lucode.hackware.magicindicator.buildins.UIUtil
import net.lucode.hackware.magicindicator.buildins.commonnavigator.CommonNavigator
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.CommonNavigatorAdapter
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.IPagerIndicator
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.IPagerTitleView
import net.lucode.hackware.magicindicator.buildins.commonnavigator.indicators.LinePagerIndicator


class NewCarFragmentNoCar : BaseFragment<FragmentCarBinding, CarViewModel>() {
    private val mAdapter by lazy { CarNotAdapter() }
    private var topBannerList = ArrayList<NewCarBannerBean>()
    private val carTopBanner by lazy {
        NewCarTopBannerAdapter(
            requireActivity(),
            getVideoListener()
        )
    }
    private val headerBinding by lazy {
        DataBindingUtil.inflate<HeaderCarBinding>(
            LayoutInflater.from(
                requireContext()
            ), R.layout.header_car, null, false
        )
    }
    private val carTopFragment by lazy { CarTopFragment() }
    private val carBottomFragment by lazy { CarBottomFragment() }
    private var fragments = ArrayList<Fragment>()
    private val carControl by lazy {
        CarControl(
            requireActivity(),
            this,
            viewModel,
            mAdapter,
            headerBinding,
            carTopFragment,
            carBottomFragment
        )
    }
    private var carInfoBean: MutableList<NewCarInfoBean>? = null
    private var hidden: Boolean = false
    private var videoPlayState = 0//视频播放状态
    private var isTop = true
    private var isFirstPageSelect = true
    private var selectPosition = -1

    @SuppressLint("NewApi")
    override fun initView() {
        val paddingTop = ImmersionBar.getStatusBarHeight(requireActivity())

        binding.rlTitle.setPadding(0, paddingTop + 10.toIntPx(), 0, 0)
        LiveDataBus.get().with(LiveDataBusKey.CLICK_CAR).observe(this) {
            StatusBarUtil.setLightStatusBar(requireActivity(), !isTop)
        }
        fragments.add(carTopFragment)
        fragments.add(carBottomFragment)
        initViewPager()
        LiveDataBus.get().withs<CarFragmentBottomBinding>("carBottom").observe(this) {
            getData()
//            carBottomFragment.setPadding(paddingTop + 60.toIntPx())
            carBottomFragment.carBottomBinding?.apply {
                mAdapter.setList(listOf(""))
                recyclerView.adapter = mAdapter
            }
        }
        LiveDataBus.get().withs<CarFragmentTopBinding>("carTop").observe(this) {
            viewModel.getTopBanner()
            initObserve()
            initBanner()
            addLiveDataBus()
        }
    }


    private fun setTabState(isWhite: Boolean) {
        val nav = binding.magicTab.navigator as CommonNavigator
        if (isWhite) {
            nav.adapter = blackAdapter
            binding.rlTitle.setBackgroundColor(
                ContextCompat.getColor(
                    requireContext(),
                    R.color.white
                )
            )
        } else {
            nav.adapter = whiteAdapter
            binding.rlTitle.setBackgroundColor(
                ContextCompat.getColor(
                    requireContext(),
                    R.color.transparent
                )
            )
        }

        binding.magicTab.navigator = nav

    }

    private fun initViewPager() {
        val viewPagerAdapter = object : FragmentStateAdapter(this) {
            override fun getItemCount(): Int {
                return fragments.size
            }

            override fun createFragment(position: Int): Fragment {
                return fragments[position]
            }
        }
        binding.viewPager.adapter = viewPagerAdapter
        binding.viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                isTop = position == 0

                StatusBarUtil.setLightStatusBar(requireActivity(), !isTop)
                if (!isFirstPageSelect) {
                    hidden = position == 1
                    setTabState(position == 1)
                    updateControl()

                    if (!isTop) {
                        lifecycleScope.launch {
                            if (selectPosition != position) {
                                delay(100)
                                refreshBottomData()
                            }

                        }
                    }

                }
                isFirstPageSelect = false
                selectPosition = position
            }
        })
        binding.viewPager.offscreenPageLimit = 2
    }

    private fun refreshBottomData() {
        getBottomData(MConstant.carBannerCarModelId)
        viewModel.getRecentlyDealers(
            carControl.latLng?.longitude,
            carControl.latLng?.latitude,
            MConstant.carBannerCarModelId
        )
    }

    override fun initData() {

    }

    private fun getData() {
        viewModel.getMyCarModelList()
        viewModel.getMoreCar()
//        viewModel.getLoveCarRecommendList {
//            headerBinding.caractivity.setContent {
//                loveCarActivityList(it)
//            }
//        }
    }

    private fun initObserve() {
        viewModel.topBannerBean.observe(this) {
            it?.apply {
                if (size == 0) {
                    carTopFragment.carTopBinding?.carTopViewPager?.isVisible = false
                    return@observe
                }
                MConstant.carBannerCarModelId = it[0].carModelId.toString()
                carTopFragment.carTopBinding?.carTopViewPager?.isVisible = true
                topBannerList.clear()
                topBannerList.addAll(this)
                carTopFragment.carTopBinding?.carTopViewPager?.apply {
                    post {
                        val params = layoutParams
                        params.height = binding.srl.height - (50.toIntPx())
                        layoutParams = params
                    }
                    carTopBanner.playerHelper = null
                    carTopBanner.currentPosition = 0
                    create(topBannerList)
                    updateControl()
                    Handler(Looper.myLooper()!!).postDelayed({
                        "banner>>>>高度：${carTopFragment.carTopBinding?.carTopViewPager?.height}".wLogE()
                        val bannerHeight = carTopFragment.carTopBinding?.carTopViewPager?.height
                    }, 500)
                }
                get(0).apply {
                    carControl.carModelCode = carModelCode
                    if (topAni == null && bottomAni == null) carControl.delayMillis = null
                    else {
                        carControl.delayMillis = 1000
                        Handler(Looper.myLooper()!!).postDelayed({
                            carControl.delayMillis = null
                        }, 1000)
                    }
                }
            }
            initMagicIndicator()
        }
        viewModel.carInfoBean.observe(this) {
            bindingCompose()
            getBottomData(MConstant.carBannerCarModelId)
            viewModel.getAuthCarInfo()
            viewModel.getBottomAds()
        }
    }

    private fun getBottomData(carModelId: String) {
        viewModel.getCarHistory(carModelId)
        viewModel.getBuyCarTips(carModelId)
    }

    private var mMagicTabHasInit = false

    private fun initMagicIndicator() {
        val magicIndicator = binding.magicTab
        magicIndicator.setBackgroundColor(Color.TRANSPARENT)
        val commonNavigator = CommonNavigator(context)
//        commonNavigator.isAdjustMode = true
        commonNavigator.adapter = whiteAdapter
        magicIndicator.navigator = commonNavigator
        mMagicTabHasInit = true
    }

    private val whiteAdapter = object : CommonNavigatorAdapter() {
        override fun getCount(): Int {
            return topBannerList.size
        }

        override fun getTitleView(context: Context, index: Int): IPagerTitleView {
            val simplePagerTitleView =
                CarScaleTransitionPagerTitleView(context)
            simplePagerTitleView.text = topBannerList[index].name
            simplePagerTitleView.textSize = 18f
            simplePagerTitleView.setPadding(14.toIntPx(), 0, 14.toIntPx(), 0)
            simplePagerTitleView.normalColor =
                ContextCompat.getColor(context, R.color.white_b2)
            simplePagerTitleView.selectedColor =
                ContextCompat.getColor(context, R.color.white)
            simplePagerTitleView.setOnClickListener {
                carTopFragment.carTopBinding?.carTopViewPager?.currentItem = index
            }
            return simplePagerTitleView
        }

        override fun getIndicator(context: Context): IPagerIndicator {
            val indicator = LinePagerIndicator(context)
            indicator.mode = LinePagerIndicator.MODE_EXACTLY
            indicator.lineHeight =
                UIUtil.dip2px(context, 3.0).toFloat()
            indicator.lineWidth =
                UIUtil.dip2px(context, 22.0).toFloat()
            indicator.roundRadius =
                UIUtil.dip2px(context, 1.5).toFloat()
            indicator.startInterpolator = AccelerateInterpolator()
            indicator.endInterpolator = DecelerateInterpolator(2.0f)
            indicator.setColors(
                ContextCompat.getColor(
                    context,
                    R.color.white
                )
            )
            return indicator
        }
    }

    private val blackAdapter = object : CommonNavigatorAdapter() {
        override fun getCount(): Int {
            return topBannerList.size
        }

        override fun getTitleView(context: Context, index: Int): IPagerTitleView {
            val simplePagerTitleView =
                CarScaleTransitionPagerTitleView(context)
            simplePagerTitleView.text = topBannerList[index].name
            simplePagerTitleView.textSize = 18f
            simplePagerTitleView.setPadding(14.toIntPx(), 0, 14.toIntPx(), 0)
            simplePagerTitleView.normalColor =
                ContextCompat.getColor(context, R.color.color_9916)
            simplePagerTitleView.selectedColor =
                ContextCompat.getColor(context, R.color.color_1700f4)
            simplePagerTitleView.setOnClickListener {
                carTopFragment.carTopBinding?.carTopViewPager?.currentItem = index
            }
            return simplePagerTitleView
        }

        override fun getIndicator(context: Context): IPagerIndicator {
            val indicator = LinePagerIndicator(context)
            indicator.mode = LinePagerIndicator.MODE_EXACTLY
            indicator.lineHeight =
                UIUtil.dip2px(context, 3.0).toFloat()
            indicator.lineWidth =
                UIUtil.dip2px(context, 22.0).toFloat()
            indicator.roundRadius =
                UIUtil.dip2px(context, 1.5).toFloat()
            indicator.startInterpolator = AccelerateInterpolator()
            indicator.endInterpolator = DecelerateInterpolator(2.0f)
            indicator.setColors(
                ContextCompat.getColor(
                    context,
                    R.color.color_1700f4
                )
            )
            return indicator
        }
    }

    private fun initBanner() {
        carTopFragment.carTopBinding?.carTopViewPager?.apply {
            setAutoPlay(false)
//            setScrollDuration(500)
            setCanLoop(true)
            setAdapter(carTopBanner)
//            stopLoopWhenDetachedFromWindow(true)
//            setIndicatorView(headerBinding.drIndicator)
            setOnPageClickListener { _, position ->
                if (!FastClickUtils.isFastClick()) {
                    val item = topBannerList[position]
                    topBannerList[position].apply {
                        JumpUtils.instans?.jump(mainJumpType, mainJumpVal)
                        GioPageConstant.maJourneyId = item.maJourneyId
                        GioPageConstant.maPlanId = item.maPlanId
                        GioPageConstant.maJourneyActCtrlId = item.maJourneyActCtrlId
                        GIOUtils.homePageClick("广告banner", (position + 1).toString(), name)
                    }
                }
            }
            carTopBanner.playerHelper = null
            val magicIndicator = binding.magicTab
            registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {

                override fun onPageScrolled(
                    position: Int,
                    positionOffset: Float,
                    positionOffsetPixels: Int
                ) {
                    super.onPageScrolled(position, positionOffset, positionOffsetPixels)
                    magicIndicator.onPageScrolled(position, positionOffset, positionOffsetPixels)

                }

                override fun onPageScrollStateChanged(state: Int) {
                    super.onPageScrollStateChanged(state)
                    magicIndicator.onPageScrollStateChanged(state)
                }

                override fun onPageSelected(position: Int) {
                    super.onPageSelected(position)
                    magicIndicator.onPageSelected(position)
                    "页面切换onPageSelected>>>$position".wLogE()
                    carTopBanner.releaseVideo()
                    carTopBanner.currentPosition = position
                    carTopBanner.notifyDataSetChanged()
                    topBannerList[position].apply {
                        MConstant.carBannerCarModelId = carModelId.toString()
                        carControl.carModelCode = carModelCode
                        MConstant.carBannerCarModelId = carModelId.toString()
                        if (binding.viewPager.currentItem == 1) {
                            binding.viewPager.currentItem = 0
                        }
                        if (!isTop) {//在底部点击tab才触发
                            lifecycleScope.launch {
                                delay(100)
                                refreshBottomData()
                            }
                        }
                    }
                    videoPlayState = -1
                    updateControl()
                }
            })
            setIndicatorView(carTopFragment.carTopBinding?.drIndicator)
        }
//        headerBinding.drIndicator.setIndicatorGap(20)
//            .setIndicatorDrawable(R.drawable.indicator_unchecked, R.drawable.indicator_checked)
        carTopFragment.carTopBinding?.carTopViewPager?.isSaveEnabled = false
    }

    /**
     * 更新控制-主要控制banner是否滚动-视频播放暂停等
     * */
    @SuppressLint("NotifyDataSetChanged")
    private fun updateControl(isHidden: Boolean = hidden) {
        "更新控制>>>isHidden:$isHidden>>>oldScrollY:>>>maxSlideY:>>>videoPlayState:$videoPlayState".wLogE()
        carTopFragment.carTopBinding?.carTopViewPager?.apply {
            val item = if (topBannerList.size > 0) topBannerList[currentItem] else null
            //可见 并且 滚动距离小于最大控制距离
            if (!isHidden) {
                if (item?.mainIsVideo == 1) {//是视频
                    if (videoPlayState == VideoView.STATE_PLAYBACK_COMPLETED) {//视频播放完成
                        "视频播放完成".wLogE()
                        carTopBanner.releaseVideo()
//                        setAutoPlay(true)
//                        startLoopNow()
                        if (currentItem < topBannerList.size - 1) currentItem += 1
                        else currentItem = 0
                    } else if (videoPlayState != VideoView.STATE_PLAYING && videoPlayState != VideoView.STATE_PREPARING) {
                        "是视频需要立即stopLoop".wLogE()
//                        setAutoPlay(false)
//                        stopLoop()
                        if (videoPlayState <= VideoView.STATE_PREPARED) {
                            carTopBanner.notifyDataSetChanged()
//                            carTopBanner.replay()
                        } else carTopBanner.resumeVideo(item.mainImg)
//                        carTopBanner.addVideoListener(getVideoListener())
                    }
                } else {//不是视频
                    "不是视频则startLoop".wLogE()
                    carTopBanner.releaseVideo()
//                    setAutoPlay(true)
//                    startLoop()
                }
            } else {
                "停止切换和播放pauseVideo>>>stopLoop".wLogE()
//                setAutoPlay(false)
//                stopLoop()
                carTopBanner.pauseVideo()
            }
        }
    }

    private fun bindingCompose() {
        viewModel.carInfoBean.value?.apply {
            for ((sort, item) in withIndex()) {
                val modelCode = item.modelCode
                var isUpdateSort = true
                carInfoBean?.find { it.modelCode == modelCode }?.let {
                    //模块的排序是否改变
                    isUpdateSort = it.modelSort != sort
                }
                bindView(sort, isUpdateSort, modelCode, item)
                item.modelSort = sort
            }
            carInfoBean = this@apply
        }
    }

    /**
     * [isUpdateSort]是否更改排序
     * */
    private fun bindView(
        sort: Int,
        isUpdateSort: Boolean,
        modelCode: String,
        dataBean: NewCarInfoBean?
    ) {
        when (modelCode) {
            //推荐
            "cars" -> carControl.setFooterRecommended(dataBean, sort, isUpdateSort)
            //购车
            "buy_service" -> carControl.setFooterBuy(dataBean, sort, isUpdateSort)
            //车主认证
            "car_auth" -> carControl.setFooterCertification(dataBean, sort, isUpdateSort)
            //售后
            "after_sales" -> carControl.setFooterOwner(dataBean, sort, isUpdateSort)
            //经销商
            "dealers" -> carControl.setFooterDealers(dataBean, sort, isUpdateSort)
            //提车日记
            "buy_car_diary" -> {
                carControl.setFooterCarHistory(dataBean, sort, isUpdateSort)
            }
            //购车引导
            "buy_car_guide" -> {
                carControl.setFooterBuyCayTips(dataBean, sort, isUpdateSort)
            }
            //广告位
            "car_middle_ads" -> {
                carControl.setFooterAds(dataBean, sort, isUpdateSort)
            }
        }
    }


    override fun onHiddenChanged(hidden: Boolean) {
        super.onHiddenChanged(hidden)
        this.hidden = hidden
        reset()
    }

    override fun onStart() {
        super.onStart()
        val locationType = carControl.locationType.value
        if (locationType == 1 || locationType == 3) carControl.initLocation()
    }

    override fun onResume() {
        super.onResume()
        reset()
//        viewModel.getMyCarModelList()
    }

    private fun reset(isHidden: Boolean = hidden) {
        if (!isHidden) {
            carControl.mMapView?.onResume()
//            getData()
        } else {
            carControl.mMapView?.onPause()
//            updateControl(isHidden)
        }
        updateControl(isHidden)
    }

    private fun getVideoListener(): VideoView.OnStateChangeListener {
        return object : VideoView.OnStateChangeListener {
            override fun onPlayerStateChanged(playerState: Int) {}
            override fun onPlayStateChanged(playState: Int) {
                videoPlayState = playState
                "播放监听》》onPlayStateChanged:>>>$playState".wLogE()
                if (VideoView.STATE_PLAYBACK_COMPLETED == playState || VideoView.STATE_PLAYING == playState) {
                    updateControl()
                }
            }
        }
    }

    override fun onPause() {
        super.onPause()
        reset(true)
    }

    override fun onDestroy() {
        carControl.mLocationClient?.stop()
        carControl.mBaiduMap?.isMyLocationEnabled = false
        carControl.mMapView?.onDestroy()
        carControl.mLocationClient = null
        super.onDestroy()
    }

    private fun addLiveDataBus() {
        //登录、退出登录回调
        LiveDataBus.get()
            .with(LiveDataBusKey.USER_LOGIN_STATUS, UserManger.UserLoginStatus::class.java)
            .observe(this) {
                when (it) {
                    UserManger.UserLoginStatus.USER_LOGIN_SUCCESS -> {
                        getData()
                    }

                    UserManger.UserLoginStatus.USER_LOGIN_OUT -> {
                        getData()
                    }

                    else -> {}
                }
            }
    }
}