package com.changanford.circle.ui.activity

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.alibaba.android.arouter.facade.annotation.Route
import com.changanford.circle.R
import com.changanford.circle.databinding.ActivityCircleDetailsBinding
import com.changanford.circle.ui.fragment.CircleListFragment
import com.changanford.circle.viewmodel.CircleDetailsViewModel
import com.changanford.common.basic.BaseActivity
import com.changanford.common.router.path.ARouterCirclePath
import com.changanford.common.util.AppUtils
import com.google.android.material.appbar.AppBarLayout
import com.google.android.material.tabs.TabLayoutMediator
import kotlin.math.abs

/**
 *Author lcw
 *Time on 2021/9/18
 *Purpose
 */
@Route(path = ARouterCirclePath.CircleDetailsActivity)
class CircleDetailsActivity:BaseActivity<ActivityCircleDetailsBinding,CircleDetailsViewModel>() {

    private var isWhite = true//是否是白色状态

    override fun initView() {
        binding.run {
            AppUtils.setStatusBarPaddingTop(binding.topContent.vLine, this@CircleDetailsActivity)
            AppUtils.setStatusBarPaddingTop(binding.toolbar, this@CircleDetailsActivity)
        }
        //处理滑动顶部效果
        binding.appbarLayout.addOnOffsetChangedListener(AppBarLayout.OnOffsetChangedListener { appBarLayout, verticalOffset ->
            val absOffset = abs(verticalOffset).toFloat() * 2.5F
            //滑动到高度一半不是白色状态
            if (absOffset < appBarLayout.height * 0.6F && !isWhite) {
                binding.backImg.setImageResource(R.mipmap.whit_left)
//                binding.shareImg.setImageResource(R.mipmap.ic_w_share)
                isWhite = true
            }
            //超过高度一半是白色状态
            else if (absOffset > appBarLayout.height * 0.6F && isWhite) {
                binding.backImg.setImageResource(R.mipmap.back_xhdpi)
//                binding.shareImg.setImageResource(R.mipmap.ic_big_share)
                isWhite = false
            }
            //改变透明度
            if (absOffset <= appBarLayout.height) {
                val mAlpha = ((absOffset / appBarLayout.height) * 255).toInt()
                binding.toolbar.background.mutate().alpha = mAlpha
                binding.barTitleTv.alpha = mAlpha / 255.0F
            } else {
                binding.toolbar.background.mutate().alpha = 255
                binding.barTitleTv.alpha = 1.0F
            }
        })

        initTabAndViewPager()
    }

    private fun initTabAndViewPager() {
        binding.viewPager.apply {
            adapter = object : FragmentStateAdapter(this@CircleDetailsActivity) {
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


    }

    override fun initData() {
        
    }
}