package com.changanford.home.acts.fragment

import android.os.Bundle
import androidx.core.content.ContextCompat
import com.changanford.common.basic.BaseFragment
import com.changanford.common.basic.EmptyViewModel
import com.changanford.home.R
import com.changanford.home.acts.adapter.SimpleAdapter
import com.changanford.home.databinding.FragmentActsListBinding
import com.zhpan.bannerview.BannerViewPager
import com.zhpan.indicator.enums.IndicatorSlideMode
import java.util.*

/**
 *  活动列表
 * */
class ActsListFragment : BaseFragment<FragmentActsListBinding, EmptyViewModel>() {


    private var mPictureList: MutableList<String> = ArrayList() // 图片存储位置

    companion object {
        fun newInstance(): ActsListFragment {
            val fg = ActsListFragment()
            val bundle = Bundle()
            fg.arguments = bundle
            return fg
        }
    }

    override fun initView() {
        setIndicator()
        binding.bViewpager.apply {
            setAutoPlay(true)
            setScrollDuration(500)
            setCanLoop(true)
            setAdapter(SimpleAdapter())
            setIndicatorView(binding.drIndicator)
            setRoundCorner(20)
            setOnPageClickListener(object : BannerViewPager.OnPageClickListener {
                override fun onPageClick(position: Int) {
                }
            })
            setIndicatorSliderColor(
                ContextCompat.getColor(requireContext(), R.color.blue_tab),
                ContextCompat.getColor(requireContext(), R.color.colorPrimary)
            )
            setIndicatorView(binding.drIndicator)
        }.create(getPicList(4))


    }

    override fun initData() {

    }

    /**
     * 设置指示器
     * */
    private fun setIndicator() {
        val dp6 = resources.getDimensionPixelOffset(R.dimen.dp_6)
        binding.drIndicator.setIndicatorDrawable(
            R.drawable.shape_home_banner_normal,
            R.drawable.shape_home_banner_focus
        )
            .setIndicatorSize(dp6, dp6, resources.getDimensionPixelOffset(R.dimen.dp_20), dp6)
            .setIndicatorGap(resources.getDimensionPixelOffset(R.dimen.dp_5))
    }

    private fun getPicList(count: Int): MutableList<String> {
        mPictureList.add("https://img.oushangstyle.com/images/article_img/2021/09/528614463ed76ffa.png")
        mPictureList.add("https://img.oushangstyle.com/images/article_img/2021/09/528614463ed76ffa.png")
        mPictureList.add("https://img.oushangstyle.com/images/article_img/2021/09/528614463ed76ffa.png")
        mPictureList.add("https://img.oushangstyle.com/images/article_img/2021/09/528614463ed76ffa.png")
        return mPictureList
    }


}