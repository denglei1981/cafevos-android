package com.changanford.circle.adapter

import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.animation.AccelerateInterpolator
import android.view.animation.DecelerateInterpolator
import androidx.core.content.ContextCompat
import androidx.core.view.marginStart
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import com.changanford.circle.R
import com.changanford.circle.databinding.ItemCircleMainTopBinding
import com.changanford.circle.databinding.ItemCircleMianBottomBinding
import com.changanford.circle.databinding.LayoutCircleHeaderHotTopicBinding
import com.changanford.circle.ext.toIntPx
import com.changanford.circle.ui.fragment.CircleDetailsMainFragment
import com.changanford.circle.ui.fragment.CircleRecommendV2Fragment
import com.changanford.circle.widget.titles.ScaleTransitionPagerTitleView
import com.changanford.common.basic.adapter.BaseAdapter
import com.changanford.common.basic.adapter.OnRecyclerViewItemClickListener
import com.changanford.common.router.path.ARouterCirclePath
import com.changanford.common.router.startARouter
import com.zhpan.bannerview.constants.PageStyle
import net.lucode.hackware.magicindicator.ViewPagerHelper
import net.lucode.hackware.magicindicator.buildins.UIUtil
import net.lucode.hackware.magicindicator.buildins.commonnavigator.CommonNavigator
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.CommonNavigatorAdapter
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.IPagerIndicator
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.IPagerTitleView
import net.lucode.hackware.magicindicator.buildins.commonnavigator.indicators.LinePagerIndicator
import net.lucode.hackware.magicindicator.buildins.commonnavigator.titles.ClipPagerTitleView
import net.lucode.hackware.magicindicator.buildins.commonnavigator.titles.SimplePagerTitleView

/**
 *Author lcw
 *Time on 2021/9/23
 *Purpose
 */
class CircleSquareAdapter(
    private val context: Context,
    private val supportFragmentManager: FragmentManager
) : BaseAdapter<String>(
    context,
    Pair(R.layout.layout_circle_header_hot_topic, 0),
    Pair(R.layout.item_circle_mian_bottom, 1)
) {

    private val tabList = listOf("推荐", "最新")


//    val topFragments = arrayListOf(
//        CircleMainFragment.newInstance("0"),
//        CircleMainFragment.newInstance("1")
//    )

    lateinit var topBinding: LayoutCircleHeaderHotTopicBinding


    val topicAdapter by lazy {
        CircleRecommendHotTopicAdapter()
    }

    override fun fillData(vdBinding: ViewDataBinding?, item: String, position: Int, viewType: Int) {
        when (viewType) {
            0 -> {
                val binding = vdBinding as LayoutCircleHeaderHotTopicBinding
                topBinding = binding



                initVpAd(binding)
//                initTopTabAndViewPager(binding)

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

            tvTopicMore.setOnClickListener {
                startARouter(ARouterCirclePath.HotTopicActivity)
            }

            topicAdapter.setOnItemClickListener { adapter, view, position ->
                val bundle = Bundle()
                bundle.putString("topicId", topicAdapter.getItem(position).topicId.toString())
                startARouter(ARouterCirclePath.TopicDetailsActivity, bundle)
            }

        }
    }

    private fun initVpAd(binding: LayoutCircleHeaderHotTopicBinding) {

        binding.let {

                it.tvTopicMore.setOnClickListener {
                    startARouter(ARouterCirclePath.HotTopicActivity)
                }
                it.bViewpager.visibility = View.GONE
                val recommendAdAdapter = CircleAdBannerAdapter()
                it.bViewpager.setAdapter(recommendAdAdapter)
                it.bViewpager.setCanLoop(true)
                it.bViewpager.setIndicatorView(it.drIndicator)
                it.bViewpager.setAutoPlay(true)
                it.bViewpager.setScrollDuration(500)
                it.bViewpager.setPageStyle(PageStyle.MULTI_PAGE_SCALE)
                it.bViewpager.create()
            }
            setIndicator(binding)

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

        magicIndicator.setBackgroundResource(R.drawable.circle_square_indicator)
        val commonNavigator = CommonNavigator(context)
        commonNavigator.isAdjustMode=true

        commonNavigator.adapter = object : CommonNavigatorAdapter() {
            override fun getCount(): Int {
                return tabList.size
            }

            override fun getTitleView(context: Context, index: Int): IPagerTitleView {

                val clipPagerTitleView = ClipPagerTitleView(context)
                clipPagerTitleView.text = tabList[index]
                clipPagerTitleView.textColor = Color.parseColor("#999999")
                clipPagerTitleView.clipColor = Color.BLACK

                clipPagerTitleView.setOnClickListener { binding.viewPager.currentItem = index }
                return clipPagerTitleView
            }

            override fun getIndicator(context: Context): IPagerIndicator {
                val indicator = LinePagerIndicator(context)
                val navigatorHeight =
                    context.resources.getDimension(R.dimen.common_navigator_height)
                val borderWidth = UIUtil.dip2px(context, 1.0).toFloat()
                val lineHeight = navigatorHeight - 2 * borderWidth
                indicator.lineHeight = lineHeight
                indicator.roundRadius = lineHeight / 2
//                indicator.yOffset = borderWidth

                indicator.setColors(Color.parseColor("#ffffff"))
                return indicator
            }
        }
        magicIndicator.navigator = commonNavigator
        ViewPagerHelper.bind(magicIndicator, binding.viewPager)
    }

    private fun initTabAndViewPager(binding: ItemCircleMianBottomBinding) {
        binding.viewPager.apply {

            adapter = object : FragmentPagerAdapter(
                supportFragmentManager,
                BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT
            ) {
                override fun getCount(): Int {
                    return tabList.size
                }

                override fun getItem(position: Int): Fragment {
                    return CircleRecommendV2Fragment.newInstance(if (position == 0) 1 else 2)

//                    return CircleDetailsMainFragment.newInstance(if (position == 0) 4 else 2)
                }

            }

            offscreenPageLimit = 1
        }

    }

    override fun getItemViewType(position: Int): Int {
        return position
    }
}