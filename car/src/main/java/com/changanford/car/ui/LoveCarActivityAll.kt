package com.changanford.car.ui

import androidx.viewpager2.widget.ViewPager2
import com.alibaba.android.arouter.facade.annotation.Route
import com.changanford.car.CarViewModel
import com.changanford.car.R
import com.changanford.car.adapter.LoveCarAdapter
import com.changanford.car.databinding.ActivityLovecaractivityallBinding
import com.changanford.car.ui.compose.loveCarActivityAll
import com.changanford.common.basic.BaseActivity
import com.changanford.common.router.path.ARouterCarControlPath
import com.changanford.common.util.AppUtils
import com.changanford.common.util.FastClickUtils

@Route(path = ARouterCarControlPath.LoveCarActivityAll)
class LoveCarActivityAll: BaseActivity<ActivityLovecaractivityallBinding, CarViewModel>() {
    val carTopBanner by  lazy {
        LoveCarAdapter()
    }
    override fun initView() {
        binding.titleLayout.barTvTitle.text = "全部活动"
        AppUtils.setStatusBarPaddingTop(binding.titleLayout.commTitleBar, this)
        binding.titleLayout.barImgBack.setOnClickListener{
            finish()
        }
        binding.carTopViewPager.apply {
            setAutoPlay(true)
            setScrollDuration(500)
            setCanLoop(true)
            setAdapter(carTopBanner)
            stopLoopWhenDetachedFromWindow(true)
            setIndicatorView(binding.drIndicator)
            setOnPageClickListener { _, position ->
                if (!FastClickUtils.isFastClick()) {

                }
            }.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            }).create()
        }
        binding.drIndicator.setIndicatorGap(20).setIndicatorDrawable(R.drawable.indicator_c0, R.drawable.indicator_94)
        binding.carTopViewPager.isSaveEnabled = false
    }

    override fun initData() {
        viewModel.getLoveCarActivityList {
//            it.addAll(it)
//            it.addAll(it)
//            it[0].activityList.addAll(it[0].activityList)
            binding.composeLayout.setContent {
                loveCarActivityAll(it)
            }
        }
        viewModel.getLoveCarConfig{
            binding.carTopViewPager.refreshData(it)
        }
    }
}