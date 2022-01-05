package com.changanford.circle.ui.activity.circle

import android.content.Context
import android.graphics.Color
import android.view.Gravity
import android.view.animation.AccelerateInterpolator
import android.view.animation.DecelerateInterpolator
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentPagerAdapter
import com.alibaba.android.arouter.facade.annotation.Route
import com.changanford.circle.R
import com.changanford.circle.databinding.ActivityCircleHotlistBinding
import com.changanford.circle.ext.toIntPx
import com.changanford.circle.ui.fragment.circle.HotListFragment
import com.changanford.circle.viewmodel.circle.NewCircleViewModel
import com.changanford.circle.widget.titles.ScaleTransitionPagerTitleView
import com.changanford.common.basic.BaseActivity
import com.changanford.common.router.path.ARouterCirclePath
import com.changanford.common.util.AppUtils
import com.google.android.material.appbar.AppBarLayout
import net.lucode.hackware.magicindicator.ViewPagerHelper
import net.lucode.hackware.magicindicator.buildins.UIUtil
import net.lucode.hackware.magicindicator.buildins.commonnavigator.CommonNavigator
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.CommonNavigatorAdapter
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.IPagerIndicator
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.IPagerTitleView
import net.lucode.hackware.magicindicator.buildins.commonnavigator.indicators.LinePagerIndicator

/**
 * @Author : wenke
 * @Time : 2022/1/5 0005
 * @Description : 圈子热门榜单
 */
@Route(path = ARouterCirclePath.HotListActivity)
class HotListActivity:BaseActivity<ActivityCircleHotlistBinding, NewCircleViewModel>(){
    private val tabNames= arrayListOf("热门车型圈","热门车友圈","热门生活圈")
    override fun initView() {
        binding.run {
            AppUtils.setStatusBarMarginTop(topBar, this@HotListActivity)
            ivBack.setOnClickListener { finish() }
        }
        initTabAndViewPager()
        initMagicIndicator()
    }

    override fun initData() {

    }
    private fun initTabAndViewPager() {
        binding.viewPager.apply {
            adapter = object : FragmentPagerAdapter(supportFragmentManager,BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {
                override fun getCount(): Int {
                    return tabNames.size
                }
                override fun getItem(position: Int): Fragment {
                    return HotListFragment.newInstance(position)
                }

            }
            offscreenPageLimit = 3
        }
    }

    private fun initMagicIndicator() {
        if (tabNames.size <= 3) {
            val layoutParam = AppBarLayout.LayoutParams(
                AppBarLayout.LayoutParams.WRAP_CONTENT,
                AppBarLayout.LayoutParams.WRAP_CONTENT
            )
            layoutParam.gravity = Gravity.CENTER_HORIZONTAL
            binding.magicTab.layoutParams = layoutParam
        } else {
            val layoutParam = AppBarLayout.LayoutParams(
                AppBarLayout.LayoutParams.MATCH_PARENT,
                AppBarLayout.LayoutParams.WRAP_CONTENT
            )
            binding.magicTab.layoutParams = layoutParam
        }
        binding.magicTab.setPadding(0, 0, 0, 2.toIntPx())

        val magicIndicator = binding.magicTab
        magicIndicator.setBackgroundColor(Color.WHITE)
        val commonNavigator = CommonNavigator(this)
        commonNavigator.scrollPivotX = 0.8f
        commonNavigator.adapter = object : CommonNavigatorAdapter() {
            override fun getCount(): Int {
                return tabNames.size
            }

            override fun getTitleView(context: Context, index: Int): IPagerTitleView {
                val simplePagerTitleView =
                    ScaleTransitionPagerTitleView(context)
                simplePagerTitleView.minScale = 1f
                simplePagerTitleView.text = tabNames[index]
                simplePagerTitleView.textSize = 15f
                if (tabNames.size <= 3) {
                    simplePagerTitleView.setPadding(
                        30.toIntPx(),
                        10.toIntPx(),
                        30.toIntPx(),
                        3.toIntPx()
                    )
                } else {
                    simplePagerTitleView.setPadding(
                        20.toIntPx(),
                        10.toIntPx(),
                        20.toIntPx(),
                        3.toIntPx()
                    )
                }
                simplePagerTitleView.normalColor =
                    ContextCompat.getColor(this@HotListActivity, R.color.color_33)
                simplePagerTitleView.selectedColor =
                    ContextCompat.getColor(this@HotListActivity, R.color.circle_app_color)
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
                        this@HotListActivity,
                        R.color.circle_app_color
                    )
                )
                return indicator
            }
        }
        magicIndicator.navigator = commonNavigator
        ViewPagerHelper.bind(magicIndicator, binding.viewPager)
    }
}