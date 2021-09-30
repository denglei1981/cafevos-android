package com.changanford.common.ui.viewpager

import androidx.viewpager2.widget.ViewPager2
import com.changanford.common.R
import com.changanford.common.basic.BaseActivity
import com.changanford.common.basic.EmptyViewModel
import com.changanford.common.databinding.TestviewpagerBinding

/**********************************************************************************
 * @Copyright (C), 2020-2021.
 * @FileName: com.changanford.common.ui.viewpager.Banner
 * @Author:　 　
 * @Version : V1.0
 * @Date: 2021/7/6 13:42
 * @Description: 　
 * *********************************************************************************
 */
class Banner : BaseActivity<TestviewpagerBinding, EmptyViewModel>() {

    override fun initView() {
        setContentView(R.layout.testviewpager)
        findViewById<ViewPager2>(R.id.banner_viewpager).adapter =
            MyViewPagerAdapterForActivity(this, Int.MAX_VALUE) {
                BannerFragment()
            }
    }

    override fun initData() {
    }
}




