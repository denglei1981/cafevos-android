package com.changanford.evos.ui

import android.view.View
import androidx.viewpager2.widget.ViewPager2
import com.alibaba.android.arouter.facade.annotation.Route
import com.changanford.common.basic.BaseActivity
import com.changanford.common.basic.EmptyViewModel
import com.changanford.common.router.path.ARouterHomePath
import com.changanford.evos.R
import com.changanford.evos.adapter.LandingAdapter
import com.changanford.evos.databinding.ActivityLandingBinding

/**********************************************************************************
 * @Copyright (C), 2018-2020.
 * @FileName: com.changanford.evos.ui.fragment.LandingActivity
 * @Author:　 　
 * @Version : V1.0
 * @Date: 2020/6/5 17:48
 * @Description: 　引导页
 * *********************************************************************************
 */
@Route(path = ARouterHomePath.LandingActivity)
class LandingActivity : BaseActivity<ActivityLandingBinding, EmptyViewModel>() {


    var datas: ArrayList<Int> = ArrayList()

    override fun initView() {
       val bannerViewPager = binding.landingviewpager
        bannerViewPager.setAutoPlay(false)
            .setScrollDuration(500)
            .setCanLoop(false)
            .setIndicatorVisibility(View.GONE)
            .setOrientation(ViewPager2.ORIENTATION_HORIZONTAL)
            .setAdapter(LandingAdapter(this))
            .registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
                override fun onPageSelected(position: Int) {
                    super.onPageSelected(position)

                }
            }).create();
    }

    override fun initData() {
        datas.add(R.drawable.landing1)
        datas.add(R.drawable.landing2)
        datas.add(R.drawable.landing3)
//        datas.add(R.drawable.landing4)
//        datas.add(R.drawable.landing5)
        binding.landingviewpager.refreshData(datas)
    }
}