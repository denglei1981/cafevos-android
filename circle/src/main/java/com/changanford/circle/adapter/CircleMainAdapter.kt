package com.changanford.circle.adapter

import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.view.animation.AccelerateInterpolator
import android.view.animation.DecelerateInterpolator
import androidx.core.content.ContextCompat
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import com.changanford.circle.R
import com.changanford.circle.databinding.ItemCircleMainTopBinding
import com.changanford.circle.databinding.ItemCircleMianBottomBinding
import com.changanford.circle.ext.toIntPx
import com.changanford.circle.ui.fragment.CircleDetailsMainFragment
import com.changanford.circle.ui.fragment.CircleMainFragment
import com.changanford.circle.widget.titles.ScaleTransitionPagerTitleView
import com.changanford.common.basic.adapter.BaseAdapter
import com.changanford.common.basic.adapter.OnRecyclerViewItemClickListener
import com.changanford.common.router.path.ARouterCirclePath
import com.changanford.common.router.startARouter
import net.lucode.hackware.magicindicator.ViewPagerHelper
import net.lucode.hackware.magicindicator.buildins.UIUtil
import net.lucode.hackware.magicindicator.buildins.commonnavigator.CommonNavigator
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.CommonNavigatorAdapter
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.IPagerIndicator
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.IPagerTitleView
import net.lucode.hackware.magicindicator.buildins.commonnavigator.indicators.LinePagerIndicator
import net.lucode.hackware.magicindicator.buildins.commonnavigator.titles.SimplePagerTitleView

/**
 *Author lcw
 *Time on 2021/9/23
 *Purpose
 */
class CircleMainAdapter(
    private val context: Context,
    private val supportFragmentManager: FragmentManager
) : BaseAdapter<String>(
    context,
    Pair(R.layout.item_circle_main_top, 0),
    Pair(R.layout.item_circle_mian_bottom, 1)
) {

    private val tabList = listOf("推荐", "最新")
    private val topTabList = listOf("地域", "兴趣")

    val topFragments = arrayListOf(
        CircleMainFragment.newInstance("0"),
        CircleMainFragment.newInstance("1")
    )

    lateinit var topBinding: ItemCircleMainTopBinding

    val allCircleAdapter by lazy {
        CircleMainCircleAdapter(context)
    }

    val topicAdapter by lazy {
        CircleMainTopicAdapter(context)
    }

    override fun fillData(vdBinding: ViewDataBinding?, item: String, position: Int, viewType: Int) {
        when (viewType) {
            0 -> {
                val binding = vdBinding as ItemCircleMainTopBinding
                topBinding = binding

                binding.ryCircle.adapter = allCircleAdapter

//                initTopMagicIndicator(binding)
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

    private fun setTopListener(binding: ItemCircleMainTopBinding) {
        binding.run {
            tvCircleMore.setOnClickListener {
                startARouter(ARouterCirclePath.CircleListActivity)
            }
            tvTopicMore.setOnClickListener {
                startARouter(ARouterCirclePath.HotTopicActivity)
            }
            topicAdapter.setOnItemClickListener(object : OnRecyclerViewItemClickListener {
                override fun onItemClick(view: View?, position: Int) {
                    val bundle = Bundle()
                    bundle.putString("topicId", topicAdapter.getItem(position)?.topicId.toString())
                    startARouter(ARouterCirclePath.TopicDetailsActivity, bundle)
                }
            })
            allCircleAdapter.setOnItemClickListener(object : OnRecyclerViewItemClickListener {
                override fun onItemClick(view: View?, position: Int) {
                    if (allCircleAdapter.getItem(position)?.circleId == 0) {
                        startARouter(ARouterCirclePath.CircleListActivity)
                    } else {
                        val bundle = Bundle()
                        bundle.putString(
                            "circleId",
                            allCircleAdapter.getItem(position)?.circleId.toString()
                        )
                        startARouter(ARouterCirclePath.CircleDetailsActivity, bundle)
                    }
                }
            })
        }
    }

    private fun initTopMagicIndicator(binding: ItemCircleMainTopBinding) {
        val magicIndicator = binding.magicTab
        magicIndicator.setBackgroundColor(Color.WHITE)
        val commonNavigator = CommonNavigator(context)
        commonNavigator.scrollPivotX = 0.8f
        commonNavigator.adapter = object : CommonNavigatorAdapter() {
            override fun getCount(): Int {
                return topTabList.size
            }

            override fun getTitleView(context: Context, index: Int): IPagerTitleView {
                val simplePagerTitleView: SimplePagerTitleView =
                    ScaleTransitionPagerTitleView(context)
                simplePagerTitleView.text = topTabList[index]
                simplePagerTitleView.textSize = 18f
                simplePagerTitleView.setPadding(10.toIntPx(), 0, 10.toIntPx(), 0)
                simplePagerTitleView.normalColor =
                    ContextCompat.getColor(context, R.color.color_33)
                simplePagerTitleView.selectedColor =
                    ContextCompat.getColor(context, R.color.circle_app_color)
                simplePagerTitleView.setOnClickListener { binding.viewPagerTop.currentItem = index }
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
                        R.color.circle_app_color
                    )
                )
                return indicator
            }
        }
        magicIndicator.navigator = commonNavigator
        ViewPagerHelper.bind(magicIndicator, binding.viewPagerTop)
    }

    private fun initTopTabAndViewPager(binding: ItemCircleMainTopBinding) {
        binding.viewPagerTop.apply {

            adapter = object : FragmentPagerAdapter(
                supportFragmentManager,
                BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT
            ) {
                override fun getCount(): Int {
                    return tabList.size
                }

                override fun getItem(position: Int): Fragment {
                    return topFragments[position]
                }

            }

            offscreenPageLimit = 1
        }

    }

    private fun initMagicIndicator(binding: ItemCircleMianBottomBinding) {
        val magicIndicator = binding.magicTab
        magicIndicator.setBackgroundColor(Color.WHITE)
        val commonNavigator = CommonNavigator(context)
        commonNavigator.scrollPivotX = 0.8f
        commonNavigator.adapter = object : CommonNavigatorAdapter() {
            override fun getCount(): Int {
                return tabList.size
            }

            override fun getTitleView(context: Context, index: Int): IPagerTitleView {
                val simplePagerTitleView: SimplePagerTitleView =
                    ScaleTransitionPagerTitleView(context)
                simplePagerTitleView.text = tabList[index]
                simplePagerTitleView.textSize = 18f
                simplePagerTitleView.setPadding(10.toIntPx(), 0, 10.toIntPx(), 0)
                simplePagerTitleView.normalColor =
                    ContextCompat.getColor(context, R.color.color_33)
                simplePagerTitleView.selectedColor =
                    ContextCompat.getColor(context, R.color.circle_app_color)
                simplePagerTitleView.setOnClickListener { binding.viewPager.currentItem = index }
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
                        R.color.circle_app_color
                    )
                )
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
                    return CircleDetailsMainFragment.newInstance(if (position == 0) 4 else 2)
                }

            }

            offscreenPageLimit = 1
        }

    }

    override fun getItemViewType(position: Int): Int {
        return position
    }
}