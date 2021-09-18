package com.changanford.circle.ui.activity

import android.graphics.Typeface
import android.os.Build
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.alibaba.android.arouter.facade.annotation.Route
import com.changanford.circle.CircleFragment
import com.changanford.circle.R
import com.changanford.circle.databinding.ActivityCircleListBinding
import com.changanford.circle.databinding.ItemCitcleTabBinding
import com.changanford.circle.ui.fragment.CircleListFragment
import com.changanford.circle.viewmodel.CircleListViewModel
import com.changanford.common.basic.BaseActivity
import com.changanford.common.router.path.ARouterCirclePath
import com.changanford.common.util.AppUtils
import com.google.android.material.tabs.TabLayoutMediator

/**
 *Author lcw
 *Time on 2021/9/18
 *Purpose 圈子详情
 */
@Route(path = ARouterCirclePath.CircleListActivity)
class CircleListActivity : BaseActivity<ActivityCircleListBinding, CircleListViewModel>() {

    private var oldPosition = 0

    override fun initView() {
        binding.run {
            AppUtils.setStatusBarMarginTop(rlTitle, this@CircleListActivity)
        }
        initTabAndViewPager()
    }

    private fun initTabAndViewPager() {
        binding.viewPager.apply {
            adapter = object : FragmentStateAdapter(this@CircleListActivity) {
                override fun getItemCount(): Int {
                    return viewModel.tabList.size
                }

                override fun createFragment(position: Int): Fragment {
                    return CircleListFragment.newInstance(position.toString())

                }

            }

//            registerOnPageChangeCallback(callback)

            offscreenPageLimit = 3
        }

        TabLayoutMediator(binding.tabs, binding.viewPager) { tab, position ->
            tab.text=viewModel.tabList[position]
        }.attach()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//            binding.tabs.setSelectedTabIndicator(
//                ContextCompat.getDrawable(
//                    this@CircleListActivity,
//                    R.drawable.circle_tab_circle_list
//                )
//            )
        }
    }

    override fun initData() {

    }

    private val callback = object :
        ViewPager2.OnPageChangeCallback() {
        override fun onPageSelected(position: Int) {
            super.onPageSelected(position)

            val oldTitle =
                binding.tabs.getTabAt(oldPosition)?.view?.findViewById<TextView>(R.id.tv_tab)
            if (oldTitle != null) {
                oldTitle.setTextColor(
                    ContextCompat.getColor(
                        this@CircleListActivity,
                        R.color.color_33
                    )
                )
                oldTitle.typeface = Typeface.defaultFromStyle(Typeface.NORMAL)
            }

            val title =
                binding.tabs.getTabAt(position)?.view?.findViewById<TextView>(R.id.tv_tab)
            if (title != null) {
                title.setTextColor(
                    ContextCompat.getColor(
                        this@CircleListActivity,
                        R.color.circle_00095b
                    )
                )
                //加粗
                title.typeface = Typeface.defaultFromStyle(Typeface.BOLD)
            }

            oldPosition = position
        }

    }
}