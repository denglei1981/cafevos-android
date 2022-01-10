package com.changanford.circle.ui.activity.circle

import android.content.Context
import android.graphics.Color
import android.os.Bundle
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
import com.changanford.common.manger.RouterManger
import com.changanford.common.router.path.ARouterCirclePath
import com.changanford.common.util.AppUtils
import com.changanford.common.wutil.ScreenUtils
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
    companion object{
        fun start(type:Int=0){
            val bundle=Bundle()
            bundle.putInt("type", type)
            RouterManger.startARouter(ARouterCirclePath.HotListActivity,bundle)
        }
    }
    private val tabNames= arrayListOf("热门车型圈","热门车友圈","热门生活圈")
    private var defaultType=0
    override fun initView() {
        binding.run {
            AppUtils.setStatusBarMarginTop(topBar, this@HotListActivity)
            ivBack.setOnClickListener { finish() }
        }
        defaultType=intent.getIntExtra("type",0)
        if(defaultType>=tabNames.size||defaultType<0)defaultType=0
        initTabAndViewPager()
        initMagicIndicator()
//        binding.viewPager.currentItem = defaultType
    }

    override fun initData() {}
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
        val magicIndicator = binding.magicTab
        binding.magicTab.setPadding(0, 0, 0, 2.toIntPx())
        magicIndicator.setBackgroundColor(Color.WHITE)
        val commonNavigator = CommonNavigator(this)
        commonNavigator.scrollPivotX = 0.8f
        commonNavigator.adapter = object : CommonNavigatorAdapter() {
            override fun getCount(): Int {
                return tabNames.size
            }

            override fun getTitleView(context: Context, index: Int): IPagerTitleView {
                val simplePagerTitleView = ScaleTransitionPagerTitleView(context)
                simplePagerTitleView.gravity=Gravity.CENTER_HORIZONTAL
                simplePagerTitleView.minScale = 1f
                simplePagerTitleView.text = tabNames[index]
                simplePagerTitleView.textSize = 15f
                simplePagerTitleView.width=ScreenUtils.getScreenWidth(this@HotListActivity)/3
                simplePagerTitleView.setPadding(0, 0, 0, 3.toIntPx())
                simplePagerTitleView.normalColor = ContextCompat.getColor(this@HotListActivity, R.color.color_33)
                simplePagerTitleView.selectedColor = ContextCompat.getColor(this@HotListActivity, R.color.circle_app_color)
                simplePagerTitleView.setOnClickListener { binding.viewPager.currentItem = index }
                return simplePagerTitleView
            }

            override fun getIndicator(context: Context): IPagerIndicator {
                val indicator = LinePagerIndicator(context)
                indicator.mode = LinePagerIndicator.MODE_EXACTLY
                indicator.lineHeight = UIUtil.dip2px(context, 3.0).toFloat()
                indicator.lineWidth = UIUtil.dip2px(context, 22.0).toFloat()
                indicator.roundRadius = UIUtil.dip2px(context, 1.5).toFloat()
                indicator.startInterpolator = AccelerateInterpolator()
                indicator.endInterpolator = DecelerateInterpolator(2.0f)
                indicator.setColors(
                    ContextCompat.getColor(this@HotListActivity, R.color.circle_app_color)
                )
                return indicator
            }
        }
        magicIndicator.navigator = commonNavigator
        ViewPagerHelper.bind(magicIndicator, binding.viewPager)
    }
}