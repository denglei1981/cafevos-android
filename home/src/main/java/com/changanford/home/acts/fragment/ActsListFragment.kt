package com.changanford.home.acts.fragment

import android.os.Bundle
import com.changanford.common.basic.BaseFragment
import com.changanford.common.basic.EmptyViewModel
import com.changanford.home.databinding.FragmentActsListBinding

/**
 *  活动列表
 * */
class ActsListFragment :BaseFragment<FragmentActsListBinding,EmptyViewModel>() {

    companion object {
        fun newInstance(): ActsListFragment {
            val fg = ActsListFragment()
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