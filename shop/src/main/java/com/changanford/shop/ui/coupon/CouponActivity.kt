package com.changanford.shop.ui.coupon

import android.view.LayoutInflater
import android.view.View
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.alibaba.android.arouter.facade.annotation.Route
import com.changanford.common.MyApp
import com.changanford.common.basic.BaseActivity
import com.changanford.common.basic.BaseViewModel
import com.changanford.common.router.path.ARouterShopPath
import com.changanford.common.wutil.ViewPage2AdapterAct
import com.changanford.shop.R
import com.changanford.shop.databinding.ActivityMyCouponBinding
import com.changanford.shop.ui.coupon.fragment.CouponCanUseFragment
import com.changanford.shop.ui.coupon.fragment.CouponUseInvalidFragment
import com.changanford.shop.ui.coupon.fragment.CouponUseOverFragment
import com.changanford.shop.view.TopBar
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator

@Route(path = ARouterShopPath.CouponActivity)
class CouponActivity : BaseActivity<ActivityMyCouponBinding, BaseViewModel>() {
    var fragmentList: ArrayList<Fragment> = arrayListOf()

    var titleList = mutableListOf<String>()

    val couponCanUseFragment: CouponCanUseFragment by lazy {
        CouponCanUseFragment.newInstance("1")
    }
    val couponUseOverFragment: CouponUseOverFragment by lazy {
        CouponUseOverFragment.newInstance("2")
    }
    val couponUseInvalidFragment: CouponUseInvalidFragment by lazy {
        CouponUseInvalidFragment.newInstance("3")
    }
    override fun initView() {
        binding.layoutTop.setOnBackClickListener(object:TopBar.OnBackClickListener{
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
        if (isSelect) {
            mTabText?.isSelected = true
            mTabText?.setTextColor(ContextCompat.getColor(MyApp.mContext, R.color.black))
            mTabText?.paint?.isFakeBoldText = true

        } else {
            mTabText?.setTextColor(ContextCompat.getColor(MyApp.mContext, R.color.gray_999999))

            mTabText?.paint?.isFakeBoldText = false// 取消加粗

        }
    }
    private fun initTab() {
        for (i in 0 until binding.searchTab.tabCount) {
            //寻找到控件
            val view: View = LayoutInflater.from(MyApp.mContext).inflate(R.layout.tab_coupon, null)
            val mTabText = view.findViewById<TextView>(R.id.tv_title)

            mTabText.text = titleList[i]
            if (itemPunchWhat == i) {
                mTabText.isSelected = true
                mTabText.setTextColor(ContextCompat.getColor(MyApp.mContext, R.color.black))
            } else {
                mTabText.setTextColor(ContextCompat.getColor(MyApp.mContext, R.color.gray_999999))
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
                R.color.color_00095B
            )
        )
    }
}