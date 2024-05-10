package com.changanford.my

import android.content.Context
import android.view.View
import android.view.animation.AccelerateInterpolator
import android.view.animation.DecelerateInterpolator
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentPagerAdapter
import com.alibaba.android.arouter.facade.annotation.Route
import com.changanford.circle.R
import com.changanford.circle.widget.titles.TopicTransitionPagerTitleView
import com.changanford.common.manger.RouterManger
import com.changanford.common.router.path.ARouterMyPath
import com.changanford.common.util.gio.updateMainGio
import com.changanford.common.utilext.toIntPx
import com.changanford.my.databinding.UiMyPostBinding
import com.changanford.my.fragment.MyPostFragment
import com.changanford.my.viewmodel.ActViewModel
import net.lucode.hackware.magicindicator.ViewPagerHelper
import net.lucode.hackware.magicindicator.buildins.UIUtil
import net.lucode.hackware.magicindicator.buildins.commonnavigator.CommonNavigator
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.CommonNavigatorAdapter
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.IPagerIndicator
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.IPagerTitleView
import net.lucode.hackware.magicindicator.buildins.commonnavigator.indicators.LinePagerIndicator
import net.lucode.hackware.magicindicator.buildins.commonnavigator.titles.SimplePagerTitleView

/**
 *  文件名：MyPostUI
 *  创建者: zcy
 *  创建日期：2021/10/8 11:19
 *  描述: TODO
 *  修改描述：TODO
 */
@Route(path = ARouterMyPath.MineFollowUI)
class MyPostUI : BaseMineUI<UiMyPostBinding, ActViewModel>() {

    override fun initView() {
        title = "我的帖子页"
//        "${MConstant.imgcdn}".logE()

        binding.postToolbar.toolbarTitle.text = "我的帖子"
        binding.postToolbar.toolbarSave.text = "草稿"
        binding.postToolbar.toolbarSave.visibility = View.VISIBLE
        binding.postToolbar.toolbar.setNavigationOnClickListener { finish() }
        binding.postToolbar.toolbarSave.setOnClickListener {
            RouterManger.startARouter(ARouterMyPath.MyPostDraftUI)
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

                    return MyPostFragment.newInstance(position)
                }

            }
            offscreenPageLimit = 1
        }

    }

    private fun initMagicIndicator() {
        val magicIndicator = binding.magicTab
//        magicIndicator.setBackgroundColor(Color.WHITE)
        val commonNavigator = CommonNavigator(this)
        commonNavigator.scrollPivotX = 0.8f
        commonNavigator.adapter = object : CommonNavigatorAdapter() {
            override fun getCount(): Int {
                return viewModel.tabList.size
            }

            override fun getTitleView(context: Context, index: Int): IPagerTitleView {
                val simplePagerTitleView: SimplePagerTitleView =
                    TopicTransitionPagerTitleView(context)
                simplePagerTitleView.text = viewModel.tabList[index]
//                simplePagerTitleView.textSize = 18f
                simplePagerTitleView.setPadding(15.toIntPx(), 0, 15.toIntPx(), 0)
                simplePagerTitleView.normalColor =
                    ContextCompat.getColor(this@MyPostUI, R.color.color_9916)
                simplePagerTitleView.selectedColor =
                    ContextCompat.getColor(this@MyPostUI, R.color.circle_app_color)
                simplePagerTitleView.setOnClickListener { binding.viewPager.currentItem = index }
                return simplePagerTitleView
            }

            override fun getIndicator(context: Context): IPagerIndicator {
                val indicator = LinePagerIndicator(context)
                indicator.mode = LinePagerIndicator.MODE_EXACTLY
                indicator.lineHeight =
                    UIUtil.dip2px(context, 2.0).toFloat()
                indicator.lineWidth =
                    UIUtil.dip2px(context, 30.0).toFloat()
                indicator.roundRadius =
                    UIUtil.dip2px(context, 0.0).toFloat()
                indicator.top = 8
                indicator.startInterpolator = AccelerateInterpolator()
                indicator.endInterpolator = DecelerateInterpolator(2.0f)
                indicator.setColors(
                    ContextCompat.getColor(
                        this@MyPostUI,
                        R.color.circle_app_color
                    )
                )
                return indicator
            }
        }
        magicIndicator.navigator = commonNavigator
        ViewPagerHelper.bind(magicIndicator, binding.viewPager)
    }

    override fun onResume() {
        super.onResume()
        updateMainGio("我的帖子页", "我的帖子页")
    }
}