package com.changanford.home.acts.fragment

import android.os.Bundle
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.changanford.common.basic.BaseFragment
import com.changanford.common.basic.EmptyViewModel
import com.changanford.home.R
import com.changanford.home.acts.adapter.ActsMainAdapter
import com.changanford.home.acts.adapter.SimpleAdapter
import com.changanford.home.databinding.FragmentActsListBinding
import com.changanford.home.databinding.IncludeActsViewPagerBinding
import com.changanford.home.search.adapter.SearchActsResultAdapter
import com.changanford.home.search.data.SearchData
import com.zhpan.bannerview.BannerViewPager
import com.zhpan.indicator.enums.IndicatorSlideMode
import java.util.*

/**
 *  活动列表
 * */
class ActsListFragment : BaseFragment<FragmentActsListBinding, EmptyViewModel>() {

    var  shopLists = mutableListOf<SearchData>()
    val searchActsResultAdapter : SearchActsResultAdapter by lazy {
        SearchActsResultAdapter(mutableListOf())
    }
    var mPictureList: MutableList<String> = ArrayList() // 图片存储位置
    companion object {
        fun newInstance(): ActsListFragment {
            val fg = ActsListFragment()
            val bundle = Bundle()
            fg.arguments = bundle
            return fg
        }
    }
    override fun initView() {
        binding.homeCrv.layoutManager=
            LinearLayoutManager(context, LinearLayoutManager.VERTICAL,false)
        shopLists.add(SearchData())
        shopLists.add(SearchData())
        shopLists.add(SearchData())
        shopLists.add(SearchData())
        shopLists.add(SearchData())
        binding.homeCrv.adapter= SearchActsResultAdapter(shopLists)
        initViewPager()
        setIndicator()
    }
    override fun initData() {

    }


    private fun initViewPager() {
        binding.layoutViewpager.bViewpager.apply {
            setAutoPlay(true)
            setScrollDuration(500)
            setCanLoop(true)
            setAdapter(SimpleAdapter())
            setIndicatorView(binding.layoutViewpager.drIndicator)
            setRoundCorner(20)
            setOnPageClickListener(object : BannerViewPager.OnPageClickListener {
                override fun onPageClick(position: Int) {
                }
            })
            setIndicatorSliderColor(
                ContextCompat.getColor(context, R.color.blue_tab),
                ContextCompat.getColor(context, R.color.colorPrimary)
            )
            setIndicatorView(binding.layoutViewpager.drIndicator)
        }.create(getPicList(4))
    }

    /**
     * 设置指示器
     * */
    private fun setIndicator() {
        val dp6 = resources.getDimensionPixelOffset(R.dimen.dp_6)
        binding.layoutViewpager.drIndicator.setIndicatorDrawable(
            R.drawable.shape_home_banner_normal,
            R.drawable.shape_home_banner_focus
        )
            .setIndicatorSize(
                dp6,
                dp6,
                resources.getDimensionPixelOffset(R.dimen.dp_20),
                dp6
            )
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