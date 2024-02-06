package com.changanford.home

import android.annotation.SuppressLint
import android.os.Looper
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.Constraints
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.changanford.common.MyApp
import com.changanford.common.basic.BaseFragment
import com.changanford.common.buried.BuriedUtil
import com.changanford.common.constant.SearchTypeConstant
import com.changanford.common.manger.UserManger
import com.changanford.common.ui.GetCoupopBindingPop
import com.changanford.common.ui.UpdateAgreePop
import com.changanford.common.ui.WaitReceiveBindingPop
import com.changanford.common.util.DisplayUtil
import com.changanford.common.util.JumpUtils
import com.changanford.common.util.MConstant
import com.changanford.common.util.bus.LiveDataBus
import com.changanford.common.util.bus.LiveDataBusKey
import com.changanford.common.util.gio.GIOUtils
import com.changanford.common.util.gio.GioPageConstant
import com.changanford.common.util.request.addRecord
import com.changanford.common.utilext.StatusBarUtil
import com.changanford.home.acts.fragment.ActsParentsFragment
import com.changanford.home.adapter.TwoAdRvListAdapter
import com.changanford.home.callback.ICallback
import com.changanford.home.data.AdBean
import com.changanford.home.data.PublishData
import com.changanford.home.data.ResultData
import com.changanford.home.databinding.FragmentSecondFloorBinding
import com.changanford.home.news.fragment.NewsListFragment
import com.changanford.home.recommend.fragment.RecommendFragment
import com.changanford.home.request.HomeV2ViewModel
import com.changanford.home.shot.fragment.BigShotFragment
import com.changanford.home.widget.pop.GetFbPop
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.gyf.immersionbar.ImmersionBar
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import razerdp.basepopup.BasePopupWindow
import java.lang.reflect.Field
import java.util.*

/**
 * A simple [Fragment] subclass as the default destination in the navigation.
 */
class HomeV2Fragment : BaseFragment<FragmentSecondFloorBinding, HomeV2ViewModel>() {

    var pagerAdapter: HomeViewPagerAdapter? = null

    var fragmentList: ArrayList<Fragment> = arrayListOf()

    var titleList = mutableListOf<String>()


    val immersionBar: ImmersionBar by lazy {
        ImmersionBar.with(this)
    }
    private val recommendFragment: RecommendFragment by lazy {
        RecommendFragment.newInstance()
    }

    private val actsParentsFragment: ActsParentsFragment by lazy {
        ActsParentsFragment.newInstance()
    }

    private val newsListFragment: NewsListFragment by lazy {
        NewsListFragment.newInstance()
    }

    val bigShotFragment: BigShotFragment by lazy {
        BigShotFragment.newInstance()
    }

    private val twoAdRvListAdapter: TwoAdRvListAdapter by lazy {
        TwoAdRvListAdapter()
    }
    var currentPosition = 0

    override fun initView() {
        //Tab+Fragment
        addLiveDataBus()
        lifecycleScope.launch {
            delay(500)
            StatusBarUtil.setLightStatusBar(requireActivity(), false)
        }
        StatusBarUtil.setStatusBarPaddingTop(binding.layoutTopBar.root, requireActivity())
        StatusBarUtil.setStatusBarMarginTop(binding.recommendContent.ivMore, requireActivity())
        easyViewPager()
        binding.refreshLayout.setEnableLoadMore(false)
        fragmentList.add(recommendFragment)
        fragmentList.add(actsParentsFragment)
        fragmentList.add(newsListFragment)
        fragmentList.add(Fragment())
//        fragmentList.add(bigShotFragment)
        titleList.add(getString(R.string.home_recommend))
        titleList.add(getString(R.string.home_acts))
        titleList.add(getString(R.string.home_news))
        titleList.add(getString(R.string.home_mouth))
//        titleList.add(getString(R.string.home_big_shot))
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
        binding.homeViewpager.registerOnPageChangeCallback(object :
            ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) { // 不禁用刷新
            }
        })
        binding.homeTab.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab) {
                GioPageConstant.prePageType = GioPageConstant.mainTabName
                GioPageConstant.prePageTypeName = GioPageConstant.mainSecondPageName()
                GioPageConstant.mainTabName = "发现页"
                GioPageConstant.findSecondPageName = when (tab.position) {
                    0 -> "发现页-推荐"
                    1 -> "发现页-活动"
                    2 -> "发现页-资讯"
                    3 -> "发现页-口碑"
                    else -> "发现页-推荐"
                }
                if (tab.position == 3) {//口碑跳转h5
                    JumpUtils.instans?.jump(1, MConstant.mouthUrl)
                    binding.homeViewpager.post {
                        binding.homeViewpager.currentItem = currentPosition
                    }
                    return
                }
                currentPosition = tab.position
                GIOUtils.homePageView()
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
//        binding.refreshLayout.setOnMultiListener(object : SimpleMultiListener() {
//            override fun onHeaderMoving(
//                header: RefreshHeader?,
//                isDragging: Boolean,
//                percent: Float,
//                offset: Int,
//                headerHeight: Int,
//                maxDragHeight: Int
//            ) {
//                val alphaTest = 1 - percent.coerceAtMost(1f)
//                if (alphaTest > 0.8f) { // 提前显示下方导航
//                    LiveDataBus.get()
//                        .with(LiveDataBusKey.LIVE_OPEN_TWO_LEVEL, Boolean::class.java)
//                        .postValue(false)
//                }
//                when (alphaTest) {
//                    0f -> {
//                        StatusBarUtil.setStatusBarColor(requireActivity(), R.color.transparent)
//                        LiveDataBus.get()
//                            .with(LiveDataBusKey.LIVE_OPEN_TWO_LEVEL, Boolean::class.java)
//                            .postValue(true)
//                    }
//
//                    1f -> { // 关闭，
//                        StatusBarUtil.setStatusBarColor(requireActivity(), R.color.white)
//                    }
//                }
//            }
//
//            override fun onStateChanged(
//                refreshLayout: RefreshLayout,
//                oldState: RefreshState,
//                newState: RefreshState
//            ) {
//                super.onStateChanged(refreshLayout, oldState, newState)
//                if (oldState == RefreshState.TwoLevel) {
//                    binding.classics.animate().alpha(1f).duration = 2000L
//                }
//            }
//
//            override fun onRefresh(refreshLayout: RefreshLayout) {
//                refreshLayout.finishRefresh()
//            }
//
//            override fun onLoadMore(refreshLayout: RefreshLayout) {
//                refreshLayout.finishLoadMore()
//
//            }
//        })
        binding.layoutTopBar.searchContent.setOnClickListener {
            toSearch()
        }
        binding.header.openTwoLevel(true)

        binding.header.setOnTwoLevelListener { refreshLayout ->
            binding.classics.animate().alpha(0f).duration = 2000L
            true
        }
        binding.homeViewpager.offscreenPageLimit = 1

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
            // 埋点
            BuriedUtil.instant?.discoverTopMenu(tab.text.toString())
            mTabText?.isSelected = true
            mTabText?.setTextColor(ContextCompat.getColor(MyApp.mContext, R.color.color_app_color))
            mTabText?.paint?.isFakeBoldText = true
            mTabText?.textSize = 18f
        } else {
            mTabText?.setTextColor(ContextCompat.getColor(MyApp.mContext, R.color.color_33))
            mTabText?.textSize = 15f
            mTabText?.paint?.isFakeBoldText = false// 取消加粗
        }
    }

    var itemPunchWhat: Int = 0

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
                mTabText.setTextColor(
                    ContextCompat.getColor(
                        MyApp.mContext,
                        R.color.color_app_color
                    )
                )
                mTabText.paint.isFakeBoldText = true
                mTabText.textSize = 18f

            } else {
                mTabText.setTextColor(ContextCompat.getColor(MyApp.mContext, R.color.color_33))
                mTabText.textSize = 15f
                mTabText.paint.isFakeBoldText = false// 取消加粗
            }
//            if (i == binding.homeTab.tabCount - 1) {
//                mTabText.setDrawableLeft(R.mipmap.icon_home_mouth, R.dimen.dp_20)
//            }
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

    var publishPopup: PublishPopup? = null
    private fun showPublish(publishLocationView: ImageView) {
        val location = IntArray(2)
        var height = DisplayUtil.getDpi(requireContext())
        publishLocationView.getLocationOnScreen(location)
        height -= location[1]
        val publishView = layoutInflater.inflate(R.layout.popup_home_publish, null)
        publishPopup = PublishPopup(
            requireContext(),
            this,
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
        publishPopup?.contentView?.measure(
            View.MeasureSpec.UNSPECIFIED,
            View.MeasureSpec.UNSPECIFIED
        )

        publishPopup?.showAsDropDown(publishLocationView)
    }

    override fun initData() {
//        viewModel.getTwoBanner()
        binding.recommendContent.rvBanner.adapter = twoAdRvListAdapter
        twoAdRvListAdapter.setOnItemClickListener { _, _, position ->
            val item = twoAdRvListAdapter.getItem(position)
            JumpUtils.instans!!.jump(item.jumpDataType, item.jumpDataValue)
        }
        binding.recommendContent.tvBigTopic.setOnClickListener {
        }
        viewModel.fBBeanLiveData.observe(this) {
            it?.apply {
                if (isPop == 1) {
                    android.os.Handler(Looper.myLooper()!!).postDelayed({
                        GetFbPop(requireContext(), viewModel, this, this@HomeV2Fragment).apply {
                            setOutSideDismiss(false)
                            showPopupWindow()
                        }
                    }, 500)
                } else {
                    viewModel.receiveList()
                }
            }
        }
//        viewModel.getUpdateAgree(this)
//        viewModel.getNewEstOne()
//        //是否领取福币
//        viewModel.isGetIntegral()
    }

    @SuppressLint("ClickableViewAccessibility")
    fun backImageViewTouch(adBean: AdBean) {
        binding.recommendContent.ivHome.setOnTouchListener { v, event ->
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                }

                MotionEvent.ACTION_UP -> {
                    JumpUtils.instans!!.jump(adBean.jumpDataType, adBean.jumpDataValue)
                }

                MotionEvent.ACTION_MOVE -> {
                    binding.header.finishTwoLevel()
                }
            }
            true
        }
    }

    var appIndexBackground: MutableList<AdBean>? = null


    override fun observe() {
        super.observe()
        viewModel.receiveListLiveData.observe(this, Observer { data ->
            if (data != null && data.isNotEmpty()) {
                // 弹窗
                android.os.Handler(Looper.myLooper()!!).postDelayed({
                    GetCoupopBindingPop(requireActivity(), this, data).apply {
                        showPopupWindow()
                        onDismissListener = object : BasePopupWindow.OnDismissListener() {
                            override fun onDismiss() {
                                viewModel.waitReceiveList()
                            }

                        }
                    }
                }, 500)
            } else {
                viewModel.waitReceiveList()
            }
        })
        viewModel.waitReceiveListLiveData.observe(this) {
            if (!it.isNullOrEmpty()) {
                android.os.Handler(Looper.myLooper()!!).postDelayed({
                    WaitReceiveBindingPop(
                        requireActivity(),
                        this,
                        it[0],
                        object : WaitReceiveBindingPop.ReceiveSuccessInterface {
                            override fun receiveSuccess() {
                                viewModel.waitReceiveList()
                            }
                        }
                    ).apply {
                        showPopupWindow()
                    }
                }, 500)
            }
        }

        viewModel.updateAgreeBean.observe(this) { bizCodeBean ->
            bizCodeBean?.let {
                it.windowMsg?.let {
                    android.os.Handler(Looper.myLooper()!!).postDelayed({
                        UpdateAgreePop(
                            requireContext(),
                            it,
                            object : UpdateAgreePop.UpdateAgreePopListener {
                                override fun clickCancel() {
                                    requireActivity().finish()
                                }

                                override fun clickSure() {
                                    bizCodeBean.ids?.let { it1 -> addRecord(it1) }
                                }

                            }).apply {
                            showPopupWindow()
                        }
                    }, 500)
                }
            }
        }

        LiveDataBus.get()
            .with(LiveDataBusKey.USER_LOGIN_STATUS, UserManger.UserLoginStatus::class.java)
            .observe(this) {
                when (it) {
                    UserManger.UserLoginStatus.USER_LOGIN_SUCCESS -> {
//                        viewModel.getNewEstOne()
                    }

                    else -> {}
                }
            }

    }


    fun stopRefresh() {
        binding.refreshLayout.finishRefresh()
    }

    open fun setCurrentItem(valueItem: String?) {
        try {
            if (!TextUtils.isEmpty(valueItem)) {
                binding.homeViewpager.currentItem = valueItem!!.toInt()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    private fun addLiveDataBus() {
        LiveDataBus.get().with(LiveDataBusKey.MAIN_TAB_CHANGE, String::class.java).observe(this) {
            if (it == "发现页") {

            }
        }
    }
}