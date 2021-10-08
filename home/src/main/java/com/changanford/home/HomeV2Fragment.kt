package com.changanford.home

import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.Constraints
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.viewpager2.widget.ViewPager2
import com.changanford.common.MyApp
import com.changanford.common.basic.BaseFragment
import com.changanford.common.basic.EmptyViewModel
import com.changanford.common.router.path.ARouterHomePath
import com.changanford.common.router.startARouter
import com.changanford.common.util.DisplayUtil
import com.changanford.common.util.bus.LiveDataBus
import com.changanford.common.util.bus.LiveDataBusKey
import com.changanford.common.utilext.StatusBarUtil
import com.changanford.home.acts.fragment.ActsListFragment
import com.changanford.home.callback.ICallback
import com.changanford.home.data.ResultData
import com.changanford.home.databinding.FragmentSecondFloorBinding
import com.changanford.home.news.fragment.NewsListFragment
import com.changanford.home.recommend.fragment.RecommendFragment
import com.changanford.home.shot.fragment.BigShotFragment
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.gyf.immersionbar.ImmersionBar
import com.scwang.smart.refresh.layout.SmartRefreshLayout
import com.scwang.smart.refresh.layout.api.RefreshHeader
import com.scwang.smart.refresh.layout.api.RefreshLayout
import com.scwang.smart.refresh.layout.listener.OnRefreshListener
import com.scwang.smart.refresh.layout.simple.SimpleMultiListener

/**
 * A simple [Fragment] subclass as the default destination in the navigation.
 */
class HomeV2Fragment : BaseFragment<FragmentSecondFloorBinding, EmptyViewModel>(),
    OnRefreshListener {

    var pagerAdapter: HomeViewPagerAdapter? = null

    var fragmentList: ArrayList<Fragment> = arrayListOf()

    var titleList = mutableListOf<String>()


    val immersionBar: ImmersionBar by lazy {
        ImmersionBar.with(this)
    }
    val actsListFragment: ActsListFragment by lazy {
        ActsListFragment.newInstance()
    }
    val recommendFragment: RecommendFragment by lazy {
        RecommendFragment.newInstance()
    }

    override fun initView() {
        //Tab+Fragment
        StatusBarUtil.setStatusBarColor(requireActivity(), R.color.white)
        ImmersionBar.with(this).statusBarColor(R.color.white)
        StatusBarUtil.setStatusBarPaddingTop(binding.llTabContent, requireActivity())
        StatusBarUtil.setStatusBarMarginTop(binding.recommendContent.ivMore, requireActivity())
        StatusBarUtil.setStatusBarMarginTop(binding.homeTab, requireActivity())
        binding.refreshLayout.setEnableLoadMore(false)
        fragmentList.add(recommendFragment)
        fragmentList.add(ActsListFragment.newInstance())
        fragmentList.add(NewsListFragment.newInstance())
        fragmentList.add(BigShotFragment.newInstance())

        titleList.add(getString(R.string.home_recommend))
        titleList.add(getString(R.string.home_acts))
        titleList.add(getString(R.string.home_news))
        titleList.add(getString(R.string.home_big_shot))
        pagerAdapter = HomeViewPagerAdapter(this, fragmentList)
        binding.homeViewpager.adapter = pagerAdapter

        binding.homeViewpager.isSaveEnabled = false
        binding.recommendContent.tvGoBack.setOnClickListener {
            binding.header.finishTwoLevel()
        }

        binding.homeTab.setSelectedTabIndicatorColor(
            ContextCompat.getColor(
                MyApp.mContext,
                R.color.blue_tab
            )
        )
        binding.homeTab.tabRippleColor = null
//        setAppbarPercent()

        TabLayoutMediator(binding.homeTab, binding.homeViewpager) { tab: TabLayout.Tab, i: Int ->
            tab.text = titleList[i]

        }.attach().apply {
            initTab()
        }


        binding.refreshLayout.setOnRefreshListener(this)

        binding.homeViewpager.registerOnPageChangeCallback(object :
            ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                when (position) {
                    0 -> {
                        binding.refreshLayout.setEnableRefresh(true)
                    }
                    else -> {
                        binding.refreshLayout.setEnableRefresh(false)
                    }
                }
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
//                    binding.llTabContent.alpha = alphaTest
//                    binding.layoutTopBar.conContent.alpha =alphaTest
//                    binding.homeTab.alpha = alphaTest
                when (alphaTest) {
                    0f -> {
                        StatusBarUtil.setStatusBarColor(requireActivity(), R.color.transparent)
                        LiveDataBus.get()
                            .with(LiveDataBusKey.LIVE_OPEN_TWO_LEVEL, Boolean::class.java)
                            .postValue(true)
                    }
                    1f -> {
                        StatusBarUtil.setStatusBarColor(requireActivity(), R.color.white)
                        LiveDataBus.get()
                            .with(LiveDataBusKey.LIVE_OPEN_TWO_LEVEL, Boolean::class.java)
                            .postValue(false)
                    }
                }

            }
        })
        binding.layoutTopBar.ivSearch.setOnClickListener {
            startARouter(ARouterHomePath.PolySearchActivity)
        }

    }

    private fun selectTab(tab: TabLayout.Tab, isSelect: Boolean) {
        var mTabText = tab.customView?.findViewById<TextView>(R.id.tv_title)
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
                }
            }
        )
        publishPopup.contentView.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED)
        publishPopup.showAsDropDown(publishLocationView)
    }


    override fun initData() {

    }

    open fun stopRefresh() {
        binding.refreshLayout.finishRefresh()
    }


    override fun onRefresh(refreshLayout: RefreshLayout) {
        recommendFragment.homeRefersh()
    }


}