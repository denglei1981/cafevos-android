package com.changanford.home.news.fragment

import android.os.Bundle
import com.changanford.common.basic.BaseFragment
import com.changanford.common.basic.EmptyViewModel
import com.changanford.home.databinding.FragmentNewsListBinding
import com.changanford.home.shot.fragment.BigShotFragment

/**
 *  新闻列表
 * */
class NewsListFragment :BaseFragment<FragmentNewsListBinding,EmptyViewModel>() {

    companion object {
        fun newInstance(): NewsListFragment {
            val fg = NewsListFragment()
            val bundle = Bundle()
            fg.arguments = bundle
            return fg
        }
    }
    override fun initView() {

    }

    override fun initData() {

    }
}