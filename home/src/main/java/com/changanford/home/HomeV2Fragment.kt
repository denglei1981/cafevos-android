package com.changanford.home

import android.animation.ObjectAnimator
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.animation.BounceInterpolator
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.Constraints
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.listener.OnItemClickListener
import com.changanford.common.MyApp
import com.changanford.common.basic.BaseFragment
import com.changanford.common.constant.SearchTypeConstant
import com.changanford.common.util.DisplayUtil
import com.changanford.common.util.JumpUtils
import com.changanford.common.util.MConstant
import com.changanford.common.util.bus.LiveDataBus
import com.changanford.common.util.bus.LiveDataBusKey
import com.changanford.common.utilext.GlideUtils
import com.changanford.common.utilext.StatusBarUtil
import com.changanford.home.acts.fragment.ActsParentsFragment
import com.changanford.home.adapter.TwoAdRvListAdapter
import com.changanford.home.callback.ICallback
import com.changanford.home.data.PublishData
import com.changanford.home.data.ResultData
import com.changanford.home.databinding.FragmentSecondFloorBinding
import com.changanford.home.news.fragment.NewsListFragment
import com.changanford.home.recommend.fragment.RecommendFragment
import com.changanford.home.request.HomeV2ViewModel
import com.changanford.home.shot.fragment.BigShotFragment
import com.changanford.home.util.AnimScaleInUtil
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.gyf.immersionbar.ImmersionBar
import com.scwang.smart.refresh.header.listener.OnTwoLevelListener
import com.scwang.smart.refresh.layout.api.RefreshHeader
import com.scwang.smart.refresh.layout.api.RefreshLayout
import com.scwang.smart.refresh.layout.constant.RefreshState
import com.scwang.smart.refresh.layout.listener.OnRefreshListener
import com.scwang.smart.refresh.layout.simple.SimpleMultiListener
import java.lang.Exception
import java.lang.reflect.Field

/**
 * A simple [Fragment] subclass as the default destination in the navigation.
 */
class HomeV2Fragment : BaseFragment<FragmentSecondFloorBinding, HomeV2ViewModel>(),
    OnRefreshListener {

    var pagerAdapter: HomeViewPagerAdapter? = null

    var fragmentList: ArrayList<Fragment> = arrayListOf()

    var titleList = mutableListOf<String>()


    val immersionBar: ImmersionBar by lazy {
        ImmersionBar.with(this)
    }
    val recommendFragment: RecommendFragment by lazy {
        RecommendFragment.newInstance()
    }

    val actsParentsFragment: ActsParentsFragment by lazy {
        ActsParentsFragment.newInstance()
    }

    val newsListFragment: NewsListFragment by lazy {
        NewsListFragment.newInstance()
    }

    val bigShotFragment: BigShotFragment by lazy {
        BigShotFragment.newInstance()
    }

    val twoAdRvListAdapter: TwoAdRvListAdapter by lazy {
        TwoAdRvListAdapter()
    }
    var currentPosition = 0


    override fun initView() {
        //Tab+Fragment

        StatusBarUtil.setStatusBarColor(requireActivity(), R.color.white)
        ImmersionBar.with(this).statusBarColor(R.color.white)
        StatusBarUtil.setStatusBarPaddingTop(binding.llTabContent, requireActivity())
        StatusBarUtil.setStatusBarMarginTop(binding.recommendContent.ivMore, requireActivity())
        StatusBarUtil.setStatusBarMarginTop(binding.homeTab, requireActivity())
        easyViewPager()
        binding.refreshLayout.setEnableLoadMore(false)
        fragmentList.add(recommendFragment)
        fragmentList.add(actsParentsFragment)
        fragmentList.add(newsListFragment)
        fragmentList.add(bigShotFragment)
        titleList.add(getString(R.string.home_recommend))
        titleList.add(getString(R.string.home_acts))
        titleList.add(getString(R.string.home_news))
        titleList.add(getString(R.string.home_big_shot))
        pagerAdapter = HomeViewPagerAdapter(this, fragmentList)
        binding.homeViewpager.adapter = pagerAdapter
        binding.homeViewpager.isSaveEnabled = false
        binding.recommendContent.llBack.setOnClickListener {
            binding.header.finishTwoLevel()
        }
        binding.homeTab.setSelectedTabIndicatorColor(
            ContextCompat.getColor(
                MyApp.mContext,
                R.color.blue_tab
            )
        )
        binding.homeTab.tabRippleColor = null

        TabLayoutMediator(binding.homeTab, binding.homeViewpager) { tab: TabLayout.Tab, i: Int ->
            tab.text = titleList[i]

        }.attach().apply {
            initTab()
        }
        binding.refreshLayout.setOnRefreshListener(this)
        binding.homeViewpager.registerOnPageChangeCallback(object :
            ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) { // 不禁用刷新
                currentPosition = position
//                when (position) {
//                    0 -> {
//                        binding.refreshLayout.setEnableRefresh(true)
//                    }
//                    else -> {
//                        binding.refreshLayout.setEnableRefresh(false)
//                    }
//                }
            }
        })
        binding.homeTab.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab) {
                selectTab(tab, true)
            }

            override fun onTabUnselected(tab: TabLayout.Tab) {
                selectTab(tab, false)
            }

            override fun onTabReselected(tab: TabLayout.Tab?) {
            }
        })

        binding.recommendContent.ivMore.setOnClickListener {
            showPublish(binding.recommendContent.ivMore)
        }
        binding.layoutTopBar.ivScan.setOnClickListener {
            showPublish(binding.layoutTopBar.ivScan)
        }
        binding.recommendContent.etSearchContent.setOnClickListener {
            toSearch()
        }
        binding.refreshLayout.setOnMultiListener(object : SimpleMultiListener() {
            override fun onHeaderMoving(
                header: RefreshHeader?,
                isDragging: Boolean,
                percent: Float,
                offset: Int,
                headerHeight: Int,
                maxDragHeight: Int
            ) {
                val alphaTest = 1 - percent.coerceAtMost(1f)
                when (alphaTest) {
                    0f -> {
                        // 打开二楼
//                        move()
                        StatusBarUtil.setStatusBarColor(requireActivity(), R.color.transparent)
                        LiveDataBus.get()
                            .with(LiveDataBusKey.LIVE_OPEN_TWO_LEVEL, Boolean::class.java)
                            .postValue(true)
                    }
                    1f -> { // 关闭，
//                        moveCancel()
                        StatusBarUtil.setStatusBarColor(requireActivity(), R.color.white)
                        LiveDataBus.get()
                            .with(LiveDataBusKey.LIVE_OPEN_TWO_LEVEL, Boolean::class.java)
                            .postValue(false)
                    }
                }
            }

            override fun onStateChanged(
                refreshLayout: RefreshLayout,
                oldState: RefreshState,
                newState: RefreshState
            ) {
                super.onStateChanged(refreshLayout, oldState, newState)
                if (oldState == RefreshState.TwoLevel) {
                    binding.classics.animate().alpha(1f).duration = 2000L
                }
            }

            override fun onRefresh(refreshLayout: RefreshLayout) {
                refreshLayout.finishRefresh()
            }

            override fun onLoadMore(refreshLayout: RefreshLayout) {
                refreshLayout.finishLoadMore()

            }
        })
        binding.layoutTopBar.ivSearch.setOnClickListener {
            toSearch()
        }
        binding.header.openTwoLevel(true)

        binding.header.setOnTwoLevelListener { refreshLayout ->
            binding.classics.animate().alpha(0f).duration = 2000L
            true
        }
    }

    fun toSearch() {
        when (binding.homeViewpager.currentItem) {
            0 -> {
                JumpUtils.instans!!.jump(108)
            }
            1 -> {
                JumpUtils.instans!!.jump(108, SearchTypeConstant.SEARCH_ACTS.toString())
            }
            2 -> {
                JumpUtils.instans!!.jump(108, SearchTypeConstant.SEARCH_NEWS.toString())
            }
            3 -> {
                JumpUtils.instans!!.jump(108, SearchTypeConstant.SEARCH_USER.toString())
            }
        }
    }

    private fun selectTab(tab: TabLayout.Tab, isSelect: Boolean) {
        val mTabText = tab.customView?.findViewById<TextView>(R.id.tv_title)
        if (isSelect) {
            mTabText?.isSelected = true
            mTabText?.setTextColor(ContextCompat.getColor(MyApp.mContext, R.color.black))
            mTabText?.paint?.isFakeBoldText = true
            mTabText?.textSize = 18f
        } else {
            mTabText?.setTextColor(ContextCompat.getColor(MyApp.mContext, R.color.black))
            mTabText?.textSize = 15f
            mTabText?.paint?.isFakeBoldText = false// 取消加粗
        }
    }

    var itemPunchWhat: Int = 0

    override fun onResume() {
        super.onResume()

    }

    fun isCurrentIndex(index: Int) = binding.homeViewpager.currentItem == index

    //初始化tab
    private fun initTab() {
        for (i in 0 until binding.homeTab.tabCount) {
            //寻找到控件
            val view: View = LayoutInflater.from(MyApp.mContext).inflate(R.layout.tab_home, null)
            val mTabText = view.findViewById<TextView>(R.id.tv_title)

            mTabText.text = titleList[i]
            if (itemPunchWhat == i) {
                mTabText.isSelected = true
                mTabText.setTextColor(ContextCompat.getColor(MyApp.mContext, R.color.black))
                mTabText.paint.isFakeBoldText = true
                mTabText.textSize = 18f

            } else {
                mTabText.setTextColor(ContextCompat.getColor(MyApp.mContext, R.color.black))
                mTabText.textSize = 15f
                mTabText.paint.isFakeBoldText = false// 取消加粗
            }
            //更改选中项样式
            //设置样式
            binding.homeTab.getTabAt(i)?.customView = view
        }
    }

    private fun easyViewPager() {
        try {
            val recyclerViewField: Field = ViewPager2::class.java.getDeclaredField("mRecyclerView")
            recyclerViewField.isAccessible = true
            val recyclerView: RecyclerView =
                recyclerViewField.get(binding.homeViewpager) as RecyclerView
            val touchSlopField: Field = RecyclerView::class.java.getDeclaredField("mTouchSlop")
            touchSlopField.isAccessible = true
            val touchSlop = touchSlopField.get(recyclerView) as Int
            touchSlopField.set(recyclerView, touchSlop * 4) //6 is empirical value
        } catch (ignore: Exception) {
        }
    }

    private fun showPublish(publishLocationView: ImageView) {
        val location = IntArray(2)
        var height = DisplayUtil.getDpi(requireContext())
        publishLocationView.getLocationOnScreen(location)
        height -= location[1]
        val publishView = layoutInflater.inflate(R.layout.popup_home_publish, null)
        val publishPopup = PublishPopup(
            requireContext(),
            publishView,
            Constraints.LayoutParams.WRAP_CONTENT,
            Constraints.LayoutParams.WRAP_CONTENT,
            object : ICallback {
                override fun onResult(result: ResultData) {
                    when ((result.data as PublishData).code) {
                        1 -> {//发布活动
                            JumpUtils.instans?.jump(13)
                        }
                        2 -> {//问卷调查
                            JumpUtils.instans?.jump(12)
                        }
                        3 -> {//扫一扫
                            JumpUtils.instans?.jump(61)
                        }
                    }
                }
            }
        )
        publishPopup.contentView.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED)
        publishPopup.showAsDropDown(publishLocationView)
    }

    override fun initData() {
        viewModel.getTwoBanner()
        binding.recommendContent.rvBanner.adapter = twoAdRvListAdapter
        twoAdRvListAdapter.setOnItemClickListener(object : OnItemClickListener {
            override fun onItemClick(adapter: BaseQuickAdapter<*, *>, view: View, position: Int) {
                val item = twoAdRvListAdapter.getItem(position)
                JumpUtils.instans!!.jump(item.jumpDataType, item.jumpDataValue)
            }
        })
        binding.recommendContent.tvBigTopic.setOnClickListener {
        }
    }


    override fun observe() {
        super.observe()
        viewModel.twoBannerLiveData.observe(this, Observer {
            if (it.isSuccess) {
                val appIndexBackground = it.data.app_index_background  // 背景广告
                appIndexBackground.forEach { b -> // 背景。
                    GlideUtils.loadBD(b.adImg, binding.recommendContent.ivHome)
                }

                val appIndexTopic = it.data.app_index_topic
                appIndexTopic.forEach { t -> // 话题
                    binding.recommendContent.tvTopicTitle.text = t.adSubName
                    binding.recommendContent.tvBigTopic.text = t.adName
                    binding.recommendContent.tvBigTopic.setOnClickListener {
                        JumpUtils.instans?.jump(t.jumpDataType, t.jumpDataValue)
                    }
                    binding.recommendContent.tvTopicTitle.setOnClickListener {
                        JumpUtils.instans?.jump(t.jumpDataType, t.jumpDataValue)
                    }
                }
                val appIndexBanner = it.data.app_index_banner
                appIndexBanner.forEach { b -> // banner
                    GlideUtils.loadBD(b.adImg, binding.recommendContent.ivBanner)
                    binding.recommendContent.ivBanner.setOnClickListener {
                        JumpUtils.instans?.jump(b.jumpDataType, b.jumpDataValue)
                    }
                }
                val appIndexAds = it.data.app_index_ads
                twoAdRvListAdapter.setNewInstance(appIndexAds)
            }

        })
        bus()
    }


    private fun bus() {
        LiveDataBus.get().withs<String>("Gone").observe(this, {
            binding.appbarLayout.setExpanded(false)
        })
        LiveDataBus.get().withs<String>("Visi").observe(this, {
            binding.appbarLayout.setExpanded(true)
        })
    }

    fun stopRefresh() {
        binding.refreshLayout.finishRefresh()
    }
    fun openTwoLevel() { // 主动打开二楼。。。
        if (MConstant.isFirstOpenTwoLevel) {
            binding.header.openTwoLevel(true)
            MConstant.isFirstOpenTwoLevel = false
        }
    }
    override fun onRefresh(refreshLayout: RefreshLayout) {
        when (currentPosition) {
            0 -> {
                recommendFragment.homeRefersh()
            }
            1 -> {
                actsParentsFragment.homeRefersh()
            }
            2 -> {
                newsListFragment.homeRefersh()
            }
            3 -> {
                bigShotFragment.homeRefersh()
            }
        }

    }

    private var animator: ObjectAnimator? = null // 手指移动动画。
    fun move() {
        val seek = binding.recommendContent.llBack.height.toFloat()
        animator = ObjectAnimator.ofFloat(
            binding.recommendContent.ivGoHome,
            "translationY",
            0.0f,
            -seek,
            30f,
            20f
        )
        animator?.duration = 5000 //动画时间
        animator?.interpolator = BounceInterpolator() //实现反复移动的效果
        animator?.repeatCount = -1 //设置动画重复次数
        animator?.startDelay = 1000 //设置动画延时执行
        animator?.start() //启动动画

    }

    fun moveCancel() {
        animator?.cancel()
    }


}