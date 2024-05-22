package com.changanford.shop.ui.coupon

import android.view.LayoutInflater
import android.view.View
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import com.alibaba.android.arouter.facade.annotation.Route
import com.changanford.common.MyApp
import com.changanford.common.basic.BaseActivity
import com.changanford.common.basic.BaseViewModel
import com.changanford.common.router.path.ARouterShopPath
import com.changanford.common.util.JumpUtils
import com.changanford.common.util.MConstant
import com.changanford.common.wutil.ViewPage2AdapterAct
import com.changanford.shop.R
import com.changanford.shop.databinding.ActivityMyCouponBinding
import com.changanford.shop.ui.coupon.fragment.CouponCanUseFragment
import com.changanford.shop.ui.coupon.fragment.CouponUseInvalidFragment
import com.changanford.shop.ui.coupon.fragment.CouponUseOverFragment
import com.changanford.shop.view.TopBar
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator


//优惠券
@Route(path = ARouterShopPath.CouponActivity)
class CouponActivity : BaseActivity<ActivityMyCouponBinding, BaseViewModel>() {
    private var fragmentList: ArrayList<Fragment> = arrayListOf()

    private var titleList = mutableListOf<String>()

    private val couponCanUseFragment: CouponCanUseFragment by lazy {
        CouponCanUseFragment.newInstance("1")
    }
    private val couponUseOverFragment: CouponUseOverFragment by lazy {
        CouponUseOverFragment.newInstance("2")
    }
    private val couponUseInvalidFragment: CouponUseInvalidFragment by lazy {
        CouponUseInvalidFragment.newInstance("3")
    }

    override fun initView() {
        binding.layoutTop.setOnRightTvClickListener(object : TopBar.OnRightTvClickListener {
            override fun onRightTvClick() {
                JumpUtils.instans?.jump(1, MConstant.COUPON_TASK_RULE)
            }

        })
        binding.layoutTop.setOnBackClickListener(object : TopBar.OnBackClickListener {
            override fun onBackClick() {
                onBackPressed()
            }
        })
        binding.searchTab.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab) {
                selectTab(tab, true)
            }

            override fun onTabUnselected(tab: TabLayout.Tab) {
                selectTab(tab, false)
            }

            override fun onTabReselected(tab: TabLayout.Tab?) {
            }
        })
    }

    private fun selectTab(tab: TabLayout.Tab, isSelect: Boolean) {
        val mTabText = tab.customView?.findViewById<TextView>(R.id.tv_title)
        val mView = tab.customView?.findViewById<View>(R.id.m_view)
        if (isSelect) {
            mView?.isVisible = true
            mTabText?.isSelected = true
            mTabText?.setTextColor(ContextCompat.getColor(MyApp.mContext, R.color.color_1700f4))
            mTabText?.textSize = 18f
//            mTabText?.paint?.isFakeBoldText = true
        } else {
            mView?.isVisible = false
            mTabText?.setTextColor(ContextCompat.getColor(MyApp.mContext, R.color.color_9916))
            mTabText?.textSize = 16f
//            mTabText?.paint?.isFakeBoldText = false// 取消加粗
        }
    }

    private fun initTab() {
        for (i in 0 until binding.searchTab.tabCount) {
            //寻找到控件
            val view: View = LayoutInflater.from(MyApp.mContext).inflate(R.layout.tab_coupon, null)
            val mTabText = view.findViewById<TextView>(R.id.tv_title)
            val mView = view.findViewById<View>(R.id.m_view)
            when (i) {
                0 -> {
                    val params = mTabText.layoutParams as RelativeLayout.LayoutParams
                    params.addRule(RelativeLayout.ALIGN_PARENT_START, RelativeLayout.TRUE)
                    params.addRule(RelativeLayout.CENTER_VERTICAL, RelativeLayout.TRUE)
                    mTabText.setLayoutParams(params)
                }

                1 -> {
                    val params = mTabText.layoutParams as RelativeLayout.LayoutParams
                    params.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE)
                    mTabText.setLayoutParams(params)
                }

                2 -> {
                    val params = mTabText.layoutParams as RelativeLayout.LayoutParams
                    params.addRule(RelativeLayout.ALIGN_PARENT_END, RelativeLayout.TRUE)
                    params.addRule(RelativeLayout.CENTER_VERTICAL, RelativeLayout.TRUE)
                    mTabText.setLayoutParams(params)
                }
            }
            mTabText.text = titleList[i]
            if (itemPunchWhat == i) {
                mView.isVisible = true
                mTabText.isSelected = true
                mTabText.setTextColor(ContextCompat.getColor(MyApp.mContext, R.color.color_1700f4))
                mTabText.textSize = 18f
            } else {
                mView.isVisible = false
                mTabText.setTextColor(ContextCompat.getColor(MyApp.mContext, R.color.color_9916))
                mTabText.textSize = 16f
            }
            //更改选中项样式
            //设置样式
            binding.searchTab.getTabAt(i)?.customView = view
        }
    }

    var itemPunchWhat: Int = 0
    override fun initData() {
        fragmentList.add(couponCanUseFragment)
        fragmentList.add(couponUseOverFragment)
        fragmentList.add(couponUseInvalidFragment)
        titleList.add("未使用")
        titleList.add("已使用")
        titleList.add("已失效")
        binding.viewpager.adapter = ViewPage2AdapterAct(this, fragmentList)
        TabLayoutMediator(binding.searchTab, binding.viewpager) { tab: TabLayout.Tab, i: Int ->
            tab.text = titleList[i]

        }.attach().apply {
            initTab()
        }
        binding.searchTab.setSelectedTabIndicatorColor(
            ContextCompat.getColor(
                MyApp.mContext,
                R.color.color_1700f4
            )
        )
    }
}