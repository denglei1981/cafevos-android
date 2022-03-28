package com.changanford.car.ui.fragment

import android.annotation.SuppressLint
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import androidx.core.view.isVisible
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.changanford.car.CarViewModel
import com.changanford.car.R
import com.changanford.car.adapter.CarNotAdapter
import com.changanford.car.adapter.NewCarTopBannerAdapter
import com.changanford.car.control.CarControl
import com.changanford.car.databinding.FragmentCarBinding
import com.changanford.car.databinding.HeaderCarBinding
import com.changanford.common.basic.BaseFragment
import com.changanford.common.bean.NewCarBannerBean
import com.changanford.common.bean.NewCarInfoBean
import com.changanford.common.buried.WBuriedUtil
import com.changanford.common.util.FastClickUtils
import com.changanford.common.util.JumpUtils
import com.changanford.common.wutil.wLogE
import com.dueeeke.videoplayer.player.VideoView


class NewCarFragmentNoCar : BaseFragment<FragmentCarBinding, CarViewModel>() {
    private val mAdapter by lazy { CarNotAdapter() }
    private var topBannerList = ArrayList<NewCarBannerBean>()
    private val carTopBanner by lazy {NewCarTopBannerAdapter(requireActivity(),getVideoListener())}
    private val headerBinding by lazy { DataBindingUtil.inflate<HeaderCarBinding>(LayoutInflater.from(requireContext()), R.layout.header_car, null, false) }
    private var oldScrollY=0
    private var maxSlideY=800//最大滚动距离
    private val carControl by lazy { CarControl(requireActivity(),this,viewModel,mAdapter,headerBinding) }
    private var carInfoBean:MutableList<NewCarInfoBean>?=null
    private var hidden:Boolean=false
    private var videoPlayState=0//视频播放状态
    @SuppressLint("NewApi")
    override fun initView() {
        binding.apply {
            srl.setOnRefreshListener {
                getData()
                it.finishRefresh()
            }
            recyclerView.adapter=mAdapter
            recyclerView.addOnScrollListener(onScrollListener)
            mAdapter.addHeaderView(headerBinding.root)
            headerBinding.apply {
                btnSubmit.setOnClickListener { //立即订购
                    WBuriedUtil.clickCarOrder(topBannerList[carTopViewPager.currentItem].carModelName)
                }
            }
        }
        initObserve()
        initBanner()
    }
    override fun initData() {}
    private fun getData(){
        viewModel.getTopBanner()
        viewModel.getMyCarModelList()
        viewModel.getMoreCar()
    }
    private fun initObserve(){
        viewModel.topBannerBean.observe(this) {
            binding.recyclerView.scrollToPosition(0)
            oldScrollY=0
            it?.apply {
                if (size == 0) {
                    headerBinding.carTopViewPager.isVisible = false
                    return@observe
                }
                headerBinding.carTopViewPager.isVisible = true
                topBannerList.clear()
                topBannerList.addAll(this)
                headerBinding.carTopViewPager.apply {
                    carTopBanner.videoHashMap.clear()
                    carTopBanner.currentPosition=0
                    create(topBannerList)
                    updateControl()
                    Handler(Looper.myLooper()!!).postDelayed({
                        "banner>>>>高度：${headerBinding.carTopViewPager.height}".wLogE()
                        val bannerHeight=headerBinding.carTopViewPager.height
                        maxSlideY=bannerHeight/2
                    },500)
                }
                get(0).apply {
                    carControl.carModelCode=carModelCode
                    if(topAni==null&&bottomAni==null)carControl.delayMillis=null
                    else{
                        carControl.delayMillis=1000
                        Handler(Looper.myLooper()!!).postDelayed({
                            carControl.delayMillis=null
                        },1000)
                    }
                }
            }
        }
        viewModel.carInfoBean.observe(this) {
            bindingCompose()
            viewModel.getAuthCarInfo()
        }
    }
    private fun initBanner(){
        headerBinding.carTopViewPager.apply {
            setAutoPlay(true)
            setScrollDuration(500)
            setCanLoop(true)
            setAdapter(carTopBanner)
            stopLoopWhenDetachedFromWindow(true)
            setIndicatorView(headerBinding.drIndicator)
            setOnPageClickListener { _, position ->
            if (!FastClickUtils.isFastClick()) {
                    JumpUtils.instans?.jump(topBannerList[position].mainJumpType, topBannerList[position].mainJumpVal)
                }
            }
            carTopBanner.videoHashMap.clear()
            registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
                override fun onPageSelected(position: Int) {
                    super.onPageSelected(position)
                    carTopBanner.notifyDataSetChanged()
                    "页面切换onPageSelected>>>$position".wLogE()
                    carTopBanner.currentPosition=position
                    carTopBanner.releaseVideoAll()
                    topBannerList[position].apply {
                        carControl.carModelCode=carModelCode
                        bindingCompose()
                    }
                    videoPlayState=-1
                    updateControl()
                }
            })
            setIndicatorView(headerBinding.drIndicator)
        }
        headerBinding.drIndicator.setIndicatorGap(20).setIndicatorDrawable(R.drawable.indicator_unchecked, R.drawable.indicator_checked)
        headerBinding.carTopViewPager.isSaveEnabled = false
    }
    /**
     * 更新控制-主要控制banner是否滚动-视频播放暂停等
     * */
    private fun updateControl(isHidden:Boolean=hidden){
        "更新控制>>>isHidden:$isHidden>>>oldScrollY:$oldScrollY>>>maxSlideY:$maxSlideY".wLogE()
        headerBinding.carTopViewPager.apply {
            val item=if(topBannerList.size>0)topBannerList[currentItem] else null
            //可见 并且 滚动距离小于最大控制距离
            if(!isHidden&&oldScrollY<maxSlideY){
                if(item?.mainIsVideo==1){//是视频
                    if(videoPlayState==VideoView.STATE_PLAYBACK_COMPLETED){//视频播放完成
                        "视频播放完成".wLogE()
                        carTopBanner.releaseVideo(item.mainImg)
//                        setAutoPlay(true)
//                        startLoopNow()
                        if(currentItem<topBannerList.size-1)currentItem += 1
                        else currentItem=0
                    }else if(videoPlayState!=VideoView.STATE_PLAYING&&videoPlayState!=VideoView.STATE_PREPARING&&videoPlayState!=VideoView.STATE_PREPARED){
                        "是视频需要立即stopLoop".wLogE()
                        setAutoPlay(false)
                        stopLoop()
                        carTopBanner.resumeVideo(item.mainImg)
                        carTopBanner.addVideoListener(item.mainImg,getVideoListener())
                    }

                }else {//不是视频
                    carTopBanner.releaseVideoAll()
                    "不是视频则startLoop".wLogE()
                    setAutoPlay(true)
                    startLoop()
                }
            }else{
                "停止切换和播放pauseVideo>>>stopLoop".wLogE()
                setAutoPlay(false)
                stopLoop()
                carTopBanner.pauseVideo(item?.mainImg)
                carTopBanner.pauseVideoAll()
            }
        }
    }
    private fun bindingCompose(){
        viewModel.carInfoBean.value?.apply {
            for ((sort,item) in withIndex()){
                val modelCode=item.modelCode
                var isUpdateSort=true
                carInfoBean?.find { it.modelCode==modelCode }?.let {
                    //模块的排序是否改变
                    isUpdateSort=it.modelSort!=sort
                }
                bindView(sort,isUpdateSort,modelCode,item)
                item.modelSort=sort
            }
            carInfoBean=this@apply
        }
    }
    /**
     * [isUpdateSort]是否更改排序
    * */
    private fun bindView(sort:Int,isUpdateSort:Boolean,modelCode:String,dataBean: NewCarInfoBean?){
        when(modelCode){
            //推荐
            "cars"->carControl.setFooterRecommended(dataBean,sort,isUpdateSort)
            //购车
            "buy_service"->carControl.setFooterBuy(dataBean,sort,isUpdateSort)
            //车主认证
            "car_auth"->carControl.setFooterCertification(dataBean,sort,isUpdateSort)
            //售后
            "after_sales"->carControl.setFooterOwner(dataBean,sort,isUpdateSort)
            //经销商
            "dealers"->carControl.setFooterDealers(dataBean,sort,isUpdateSort)
        }
    }

    /**
     * RecyclerView 滚动监听 主要用于控制banner是否自动播放
    * */
    private val onScrollListener=object:RecyclerView.OnScrollListener() {
        override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
            super.onScrollStateChanged(recyclerView, newState)
            if(newState== RecyclerView.SCROLL_STATE_IDLE){
                updateControl()
            }
        }
        override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
            super.onScrolled(recyclerView, dx, dy)
            oldScrollY+=dy
        }
    }

    override fun onHiddenChanged(hidden: Boolean) {
        super.onHiddenChanged(hidden)
        this.hidden=hidden
        reset()
    }
    override fun onStart() {
        super.onStart()
        val locationType=carControl.locationType.value
        if(locationType==1||locationType==3)carControl.initLocation()
    }
    override fun onResume() {
        super.onResume()
        reset()
    }
    private fun reset(isHidden:Boolean=hidden){
        if(!isHidden) {
            carControl.mMapView?.onResume()
            getData()
        }else{
            carControl.mMapView?.onPause()
            updateControl(isHidden)
        }
    }
    private fun getVideoListener():VideoView.OnStateChangeListener{
        return object :VideoView.OnStateChangeListener{
            override fun onPlayerStateChanged(playerState: Int) {}
            override fun onPlayStateChanged(playState: Int) {
                videoPlayState=playState
                "播放监听》》onPlayStateChanged:>>>$playState".wLogE()
                if(VideoView.STATE_PLAYBACK_COMPLETED==playState||VideoView.STATE_PLAYING==playState){
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
        carControl.mLocationClient=null
        super.onDestroy()
    }
}