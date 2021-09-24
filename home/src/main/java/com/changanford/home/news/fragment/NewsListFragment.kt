package com.changanford.home.news.fragment

import android.os.Bundle
import android.view.LayoutInflater
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import com.changanford.common.basic.BaseFragment
import com.changanford.common.basic.EmptyViewModel
import com.changanford.home.R
import com.changanford.home.acts.adapter.SimpleAdapter
import com.changanford.home.adapter.RecommendAdapter
import com.changanford.home.data.RecommendData
import com.changanford.home.databinding.FragmentNewsListBinding
import com.changanford.home.databinding.HeaderNewsListBinding
import com.changanford.home.news.adapter.NewsBannerAdapter
import com.changanford.home.news.adapter.NewsListAdapter
import com.changanford.home.news.data.NewsData
import com.changanford.home.shot.fragment.BigShotFragment
import com.zhpan.bannerview.BannerViewPager
import java.util.ArrayList

/**
 *  新闻列表
 * */
class NewsListFragment : BaseFragment<FragmentNewsListBinding, EmptyViewModel>() {

    private var mPictureList: MutableList<String> = ArrayList() // 图片存储位置

    var newsListAdapter: NewsListAdapter?=null

    companion object {
        fun newInstance(): NewsListFragment {
            val fg = NewsListFragment()
            val bundle = Bundle()
            fg.arguments = bundle
            return fg
        }
    }

    override fun initView() {
        binding.recyclerView.layoutManager =
            LinearLayoutManager(requireActivity(), LinearLayoutManager.VERTICAL, false)
        newsListAdapter=NewsListAdapter().apply {
            addData(NewsData())
            addData(NewsData())
            addData(NewsData())
            addData(NewsData())
            addData(NewsData())
            addData(NewsData())
            addData(NewsData())
            addData(NewsData())
        }
        addHeadView()
        binding.recyclerView.adapter = newsListAdapter
    }

    var headNewBinding: HeaderNewsListBinding?=null

    private fun addHeadView() {
        if (headNewBinding == null) {
            headNewBinding = DataBindingUtil.inflate(
                LayoutInflater.from(requireContext()),
                R.layout.header_news_list,
                binding.recyclerView,
                false
            )
            headNewBinding?.let {
                newsListAdapter?.addHeaderView(it.root,0)
                it.bViewpager.setAdapter(NewsBannerAdapter())
                it.bViewpager.setCanLoop(true)
                it.bViewpager.setIndicatorView(it.drIndicator)
                it.bViewpager.setAutoPlay(true)
                it.bViewpager.create(getPicList())
                it.bViewpager.setScrollDuration(5000)
            }
            setIndicator()

        }
    }



    /**
     * 设置指示器
     * */
    private fun setIndicator() {
        val dp6 = resources.getDimensionPixelOffset(R.dimen.dp_6)
        headNewBinding?.drIndicator?.setIndicatorDrawable(
            R.drawable.shape_home_banner_normal,
            R.drawable.shape_home_banner_focus
        )
            ?.setIndicatorSize(dp6, dp6, resources.getDimensionPixelOffset(R.dimen.dp_20), dp6)
            ?.setIndicatorGap(resources.getDimensionPixelOffset(R.dimen.dp_5))
    }

    override fun initData() {

    }

    private fun getPicList(): MutableList<String> {
        mPictureList.add("https://img.oushangstyle.com/images/article_img/2021/09/528614463ed76ffa.png")
        mPictureList.add("https://img.oushangstyle.com/images/article_img/2021/09/528614463ed76ffa.png")
        mPictureList.add("https://img.oushangstyle.com/images/article_img/2021/09/528614463ed76ffa.png")
        mPictureList.add("https://img.oushangstyle.com/images/article_img/2021/09/528614463ed76ffa.png")
        return mPictureList
    }
}