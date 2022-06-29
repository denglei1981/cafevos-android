package com.changanford.circle.ui.activity

import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.text.TextUtils
import android.view.animation.AccelerateInterpolator
import android.view.animation.DecelerateInterpolator
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentPagerAdapter
import com.alibaba.android.arouter.facade.annotation.Route
import com.changanford.circle.R
import com.changanford.circle.bean.CircleTypesBean
import com.changanford.circle.databinding.ActivityCircleListBinding
import com.changanford.circle.ext.toIntPx
import com.changanford.circle.ui.fragment.CircleListFragment
import com.changanford.circle.viewmodel.CircleListViewModel
import com.changanford.circle.widget.titles.ScaleTransitionPagerTitleView
import com.changanford.common.basic.BaseActivity
import com.changanford.common.router.path.ARouterCirclePath
import com.changanford.common.router.path.ARouterMyPath
import com.changanford.common.router.startARouter
import com.changanford.common.util.AppUtils
import com.changanford.common.utilext.toast
import com.google.android.material.appbar.AppBarLayout
import net.lucode.hackware.magicindicator.ViewPagerHelper
import net.lucode.hackware.magicindicator.buildins.UIUtil
import net.lucode.hackware.magicindicator.buildins.commonnavigator.CommonNavigator
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.CommonNavigatorAdapter
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.IPagerIndicator
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.IPagerTitleView
import net.lucode.hackware.magicindicator.buildins.commonnavigator.indicators.LinePagerIndicator
import net.lucode.hackware.magicindicator.buildins.commonnavigator.titles.SimplePagerTitleView
import java.net.URLDecoder
import kotlin.math.abs

/**
 *Author lcw
 *Time on 2021/9/18
 *Purpose 全部圈子
 */
@Route(path = ARouterCirclePath.CircleListActivity)
class CircleListActivity : BaseActivity<ActivityCircleListBinding, CircleListViewModel>() {
    companion object{
        fun start(typeId:String?="0"){
            val bundle = Bundle()
            bundle.putString("typeId", typeId?:"0")
            startARouter(ARouterCirclePath.CircleListActivity,bundle)
        }
    }
    private var typeId:String="0"//圈子分类ID 默认全部
    private var typeName:String=""
    override fun initView() {
        binding.run {
            AppUtils.setStatusBarMarginTop(rlTitle, this@CircleListActivity)
            ivBack.setOnClickListener { finish() }
            tvMyCircle.setOnClickListener {
                startARouter(ARouterMyPath.MineCircleUI, true)
            }
        }
        typeId=intent.getStringExtra("typeId")?:"0"
        typeName=intent.getStringExtra("value")?:""
        initListener()
    }

    private fun initListener() {
        binding.run {
            tvSearch.setOnClickListener {
                startARouter(ARouterCirclePath.SearchCircleActivity)
            }
            tvMyCircle.setOnClickListener {
                startARouter(ARouterMyPath.MineCircleUI, true)
            }
        }
        //处理滑动顶部效果
        binding.appbarLayout.addOnOffsetChangedListener(AppBarLayout.OnOffsetChangedListener { appBarLayout, verticalOffset ->
            val absOffset = abs(verticalOffset).toFloat() * 2.5F
            //改变透明度
            if (absOffset <= appBarLayout.height) {
                binding.vLine.alpha = 0.5F
            } else {
                binding.vLine.alpha = 0F
            }
        })
    }

    private fun initTabAndViewPager(types: ArrayList<CircleTypesBean>) {
        binding.viewPager.apply {

            adapter = object : FragmentPagerAdapter(
                supportFragmentManager,
                BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT
            ) {
                override fun getCount(): Int {
                    return types.size
                }

                override fun getItem(position: Int): Fragment {
                    val bean = types[position]
                    return CircleListFragment.newInstance(bean.id, bean.isRegion)
                }

            }

            offscreenPageLimit = 3
        }

    }

    override fun initData() {
        viewModel.getTypes()
    }

    override fun observe() {
        super.observe()
        viewModel.typesBean.observe(this) {
            if (it.isNotEmpty()) {
                runOnUiThread {
                    initMagicIndicator(it)
                    initTabAndViewPager(it)
                    val index =if(TextUtils.isEmpty(typeName)) it.indexOfFirst { item -> item.id.toString() == typeId }
                    else it.indexOfFirst { item -> item.name == URLDecoder.decode(typeName, "UTF-8") }
                    if (index > 0) binding.viewPager.currentItem = index

                }
            } else {
                "没有圈子类型".toast()
                finish()
            }
        }
    }

    private fun initMagicIndicator(types: ArrayList<CircleTypesBean>) {

//        if (types.size <= 3) {
//            val layoutParam = AppBarLayout.LayoutParams(
//                AppBarLayout.LayoutParams.WRAP_CONTENT,
//                AppBarLayout.LayoutParams.WRAP_CONTENT
//            )
//            layoutParam.gravity = Gravity.CENTER_HORIZONTAL
//            binding.magicTab.layoutParams = layoutParam
//        } else {
            val layoutParam = AppBarLayout.LayoutParams(
                AppBarLayout.LayoutParams.MATCH_PARENT,
                AppBarLayout.LayoutParams.WRAP_CONTENT
            )
            binding.magicTab.layoutParams = layoutParam
//        }
        binding.magicTab.setPadding(0, 0, 0, 2.toIntPx())

        val magicIndicator = binding.magicTab
        magicIndicator.setBackgroundColor(Color.WHITE)
        val commonNavigator = CommonNavigator(this)
        commonNavigator.scrollPivotX = 0.8f
        commonNavigator.adapter = object : CommonNavigatorAdapter() {
            override fun getCount(): Int {
                return types.size
            }

            override fun getTitleView(context: Context, index: Int): IPagerTitleView {
                val simplePagerTitleView: SimplePagerTitleView = ScaleTransitionPagerTitleView(context)
                simplePagerTitleView.apply {
                    text = types[index].name
                    textSize = 18f
                    setPadding(20.toIntPx(), 10.toIntPx(), 20.toIntPx(), 9.toIntPx())
                    normalColor = ContextCompat.getColor(this@CircleListActivity, R.color.color_33)
                    selectedColor = ContextCompat.getColor(this@CircleListActivity, R.color.circle_app_color)
                    setOnClickListener { binding.viewPager.currentItem = index }
                    return this
                }
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