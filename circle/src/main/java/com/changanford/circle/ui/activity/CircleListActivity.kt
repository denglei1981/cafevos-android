package com.changanford.circle.ui.activity

import android.content.Context
import android.graphics.Color
import android.view.animation.AccelerateInterpolator
import android.view.animation.DecelerateInterpolator
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentPagerAdapter
import com.alibaba.android.arouter.facade.annotation.Route
import com.changanford.circle.R
import com.changanford.circle.databinding.ActivityCircleListBinding
import com.changanford.circle.ext.toIntPx
import com.changanford.circle.ui.fragment.CircleListFragment
import com.changanford.circle.viewmodel.CircleListViewModel
import com.changanford.circle.widget.titles.ScaleTransitionPagerTitleView
import com.changanford.common.basic.BaseActivity
import com.changanford.common.router.path.ARouterCirclePath
import com.changanford.common.util.AppUtils
import com.xiaomi.push.iv
import net.lucode.hackware.magicindicator.ViewPagerHelper
import net.lucode.hackware.magicindicator.buildins.UIUtil
import net.lucode.hackware.magicindicator.buildins.commonnavigator.CommonNavigator
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.CommonNavigatorAdapter
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.IPagerIndicator
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.IPagerTitleView
import net.lucode.hackware.magicindicator.buildins.commonnavigator.indicators.LinePagerIndicator

/**
 *Author lcw
 *Time on 2021/9/18
 *Purpose 圈子详情
 */
@Route(path = ARouterCirclePath.CircleListActivity)
class CircleListActivity : BaseActivity<ActivityCircleListBinding, CircleListViewModel>() {


    override fun initView() {
        binding.run {
            AppUtils.setStatusBarMarginTop(rlTitle, this@CircleListActivity)
            ivBack.setOnClickListener { finish() }
        }
        initMagicIndicator()
        initTabAndViewPager()
    }

    private fun initTabAndViewPager() {
        binding.viewPager.apply {

            adapter = object : FragmentPagerAdapter(
                supportFragmentManager,
                BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT
            ) {
                override fun getCount(): Int {
                    return viewModel.tabList.size
                }

                override fun getItem(position: Int): Fragment {
                    return CircleListFragment.newInstance(position)
                }

            }

            offscreenPageLimit = 3
        }

    }

    override fun initData() {

    }

    private fun initMagicIndicator() {
        val magicIndicator = binding.magicTab
        magicIndicator.setBackgroundColor(Color.WHITE)
        val commonNavigator = CommonNavigator(this)
        commonNavigator.scrollPivotX = 0.8f
        commonNavigator.adapter = object : CommonNavigatorAdapter() {
            override fun getCount(): Int {
                return viewModel.tabList.size
            }

            override fun getTitleView(context: Context, index: Int): IPagerTitleView {
                val simplePagerTitleView =
                    ScaleTransitionPagerTitleView(context)
                simplePagerTitleView.minScale=1f
                simplePagerTitleView.text = viewModel.tabList[index]
                simplePagerTitleView.textSize = 15f
                simplePagerTitleView.setPadding(30.toIntPx(), 0, 30.toIntPx(), 0)
                simplePagerTitleView.normalColor =
                    ContextCompat.getColor(this@CircleListActivity, R.color.color_33)
                simplePagerTitleView.selectedColor =
                    ContextCompat.getColor(this@CircleListActivity, R.color.circle_app_color)
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
                        this@CircleListActivity,
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