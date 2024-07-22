package com.changanford.circle.adapter

import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.view.View
import androidx.core.content.ContextCompat
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import androidx.lifecycle.Lifecycle
import androidx.viewpager.widget.ViewPager
import androidx.viewpager2.widget.ViewPager2
import com.changanford.circle.R
import com.changanford.circle.databinding.ItemCircleMianBottomBinding
import com.changanford.circle.databinding.LayoutCircleHeaderHotTopicBinding
import com.changanford.circle.ui.fragment.CircleRecommendV2Fragment
import com.changanford.circle.widget.titles.ScaleTransitionPagerTitleView
import com.changanford.common.adapter.PolySearchTopicAdapter
import com.changanford.common.basic.adapter.BaseAdapter
import com.changanford.common.bean.AdBean
import com.changanford.common.buried.BuriedUtil
import com.changanford.common.router.path.ARouterCirclePath
import com.changanford.common.router.startARouter
import com.changanford.common.util.gio.GIOUtils
import com.changanford.common.util.gio.GioPageConstant
import com.changanford.common.utilext.toIntPx

import com.youth.banner.util.BannerUtils
import com.zhpan.bannerview.constants.PageStyle
import net.lucode.hackware.magicindicator.ViewPagerHelper
import net.lucode.hackware.magicindicator.buildins.UIUtil
import net.lucode.hackware.magicindicator.buildins.commonnavigator.CommonNavigator
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.CommonNavigatorAdapter
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.IPagerIndicator
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.IPagerTitleView
import net.lucode.hackware.magicindicator.buildins.commonnavigator.indicators.LinePagerIndicator

/**
 *Author lcw
 *Time on 2021/9/23
 *Purpose
 */
class CircleSquareAdapter(
    private val context: Context,
    private val supportFragmentManager: FragmentManager,
    private val lifecycleRegistry: Lifecycle
) : BaseAdapter<String>(
    context,
    Pair(R.layout.layout_circle_header_hot_topic, 0),
    Pair(R.layout.item_circle_mian_bottom, 1)
) {

    private val tabList = listOf("推荐", "最新")
    val circleRecommendV2Fragment =
        CircleRecommendV2Fragment.newInstance(1)
    val lastCircleRecommendV2Fragment =
        CircleRecommendV2Fragment.newInstance(2)

//    val topFragments = arrayListOf(
//        CircleMainFragment.newInstance("0"),
//        CircleMainFragment.newInstance("1")
//    )

    lateinit var topBinding: LayoutCircleHeaderHotTopicBinding


    val topicAdapter by lazy {
        PolySearchTopicAdapter()
    }

    override fun fillData(vdBinding: ViewDataBinding?, item: String, position: Int, viewType: Int) {
        when (viewType) {
            0 -> {
                val binding = vdBinding as LayoutCircleHeaderHotTopicBinding
                topBinding = binding
                initVpAd(binding)

                binding.ryTopic.adapter = topicAdapter

                setTopListener(binding)

            }

            1 -> {
                val binding = vdBinding as ItemCircleMianBottomBinding
                initMagicIndicator(binding)
                initTabAndViewPager(binding)
            }
        }
    }

    private fun setTopListener(binding: LayoutCircleHeaderHotTopicBinding) {
        binding.run {
            ivTopicRight.setOnClickListener {
                startARouter(ARouterCirclePath.HotTopicActivity)
                GIOUtils.homePageClick("热门话题", 0.toString(), "更多")
            }

            topicAdapter.setOnItemClickListener { adapter, view, position ->
                GioPageConstant.topicEntrance = "社区-广场-热门话题"
                val item = topicAdapter.getItem(position)
                // 埋点
                BuriedUtil.instant?.communityMainHotTopic(item.name)
                GIOUtils.homePageClick("热门话题", (position + 1).toString(), item.name)
                val bundle = Bundle()
                bundle.putString("topicId", item.topicId.toString())
                startARouter(ARouterCirclePath.TopicDetailsActivity, bundle)
            }

        }
    }

    private fun initVpAd(binding: LayoutCircleHeaderHotTopicBinding) {

        binding.let {

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
            it.bViewpager.registerLifecycleObserver(lifecycleRegistry)
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
        setIndicator(binding)
    }

    fun refreshViewPager(listBean: List<AdBean>) {
        topBinding.let {

//            it.ivTopicRight.setOnClickListener {
//                startARouter(ARouterCirclePath.HotTopicActivity)
//            }
//            it.bViewpager.visibility = View.GONE
            val recommendAdAdapter = CircleAdBannerAdapter()
            it.bViewpager.setAdapter(recommendAdAdapter)
            it.bViewpager.setCanLoop(true)
            it.bViewpager.setPageMargin(20)
            it.bViewpager.setRevealWidth(BannerUtils.dp2px(10f))
            it.bViewpager.setPageStyle(PageStyle.MULTI_PAGE)
            it.bViewpager.registerLifecycleObserver(lifecycleRegistry)
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
        topBinding.bViewpager.refreshData(listBean)
        setIndicator(topBinding)
    }

    /**
     * 设置指示器
     * */
    private fun setIndicator(binding: LayoutCircleHeaderHotTopicBinding?) {
        val dp6 = context.resources.getDimensionPixelOffset(R.dimen.dp_6)
        binding?.drIndicator?.setIndicatorDrawable(
            R.drawable.shape_circle_banner_normal,
            R.drawable.shape_circle_banner_focus
        )
            ?.setIndicatorSize(
                dp6,
                dp6,
                context.resources.getDimensionPixelOffset(R.dimen.dp_20),
                dp6
            )
            ?.setIndicatorGap(context.resources.getDimensionPixelOffset(R.dimen.dp_5))
    }


    private fun initMagicIndicator(binding: ItemCircleMianBottomBinding) {
        val magicIndicator = binding.magicTab
//        magicIndicator.setBackgroundResource(R.drawable.circle_square_indicator)
        val commonNavigator = CommonNavigator(context)
        commonNavigator.isAdjustMode = true

        commonNavigator.adapter = object : CommonNavigatorAdapter() {
            override fun getCount(): Int {
                return tabList.size
            }

            override fun getTitleView(context: Context, index: Int): IPagerTitleView {

                val clipPagerTitleView = ScaleTransitionPagerTitleView(context)
                clipPagerTitleView.text = tabList[index]
                clipPagerTitleView.normalColor =
                    ContextCompat.getColor(context, R.color.color_8016)
                clipPagerTitleView.setPadding(0, 0, 24.toIntPx(), 0)
                clipPagerTitleView.selectedColor =
                    ContextCompat.getColor(context, R.color.circle_app_color)

                clipPagerTitleView.setOnClickListener { binding.viewPager.currentItem = index }
                return clipPagerTitleView
            }

            override fun getIndicator(context: Context): IPagerIndicator {
                val indicator = LinePagerIndicator(context)
                val navigatorHeight =
                    context.resources.getDimension(R.dimen.common_navigator_height)
                val borderWidth = UIUtil.dip2px(context, 1.0).toFloat()
                val lineHeight = navigatorHeight - 2 * borderWidth
                indicator.lineHeight = 0f
                indicator.roundRadius = lineHeight / 2
//                indicator.yOffset = borderWidth

                indicator.setColors(Color.parseColor("#ffffff"))
                return indicator
            }
        }
        magicIndicator.navigator = commonNavigator
        ViewPagerHelper.bind(magicIndicator, binding.viewPager)

    }

    var viewpagerBinding: ItemCircleMianBottomBinding? = null
    private fun initTabAndViewPager(binding: ItemCircleMianBottomBinding) {
        viewpagerBinding = binding
        binding.viewPager.apply {

            adapter = object : FragmentPagerAdapter(
                supportFragmentManager,
                BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT
            ) {
                override fun getCount(): Int {
                    return tabList.size
                }

                override fun getItem(position: Int): Fragment {

                    return when (position) {
                        0 -> {
                            circleRecommendV2Fragment
                        }

                        1 -> {
                            lastCircleRecommendV2Fragment
                        }

                        else -> {
                            circleRecommendV2Fragment
                        }
                    }


                }
            }
            offscreenPageLimit = 1

            val pageChangeListener = object : ViewPager.OnPageChangeListener {
                override fun onPageScrolled(
                    position: Int,
                    positionOffset: Float,
                    positionOffsetPixels: Int
                ) {

                }

                override fun onPageSelected(position: Int) {
                    if (position == 0) {
                        GIOUtils.homePageClick("筛选区", 1.toString(), "推荐")
                    } else {
                        GIOUtils.homePageClick("筛选区", 2.toString(), "最新")
                    }
                }

                override fun onPageScrollStateChanged(state: Int) {

                }

            }
            removeOnPageChangeListener(pageChangeListener)
            addOnPageChangeListener(pageChangeListener)
        }
    }

    fun outRefresh() {
        viewpagerBinding?.let { vp ->
            when (vp.viewPager.currentItem) {
                0 -> {
                    circleRecommendV2Fragment.outRefresh()
                }

                1 -> {
                    lastCircleRecommendV2Fragment.outRefresh()
                }
            }


        }
    }


    override fun getItemViewType(position: Int): Int {
        return position
    }
}