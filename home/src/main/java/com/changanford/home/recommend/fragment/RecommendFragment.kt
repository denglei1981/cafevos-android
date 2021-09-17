package com.changanford.home.recommend.fragment

import android.os.Bundle
import com.changanford.common.basic.BaseFragment
import com.changanford.common.basic.EmptyViewModel
import com.changanford.home.databinding.FragmentRecommendListBinding


/**
 *  推荐列表
 * */
class RecommendFragment : BaseFragment<FragmentRecommendListBinding, EmptyViewModel>() {

    companion object {
        fun newInstance(): RecommendFragment {
            val fg = RecommendFragment()
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