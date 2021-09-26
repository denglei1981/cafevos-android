package com.changanford.home.search.fragment

import android.os.Bundle
import com.changanford.common.basic.BaseFragment
import com.changanford.common.basic.EmptyViewModel
import com.changanford.home.databinding.HomeBaseRecyclerViewBinding

class SearchNewsFragment : BaseFragment<HomeBaseRecyclerViewBinding, EmptyViewModel>() {

    companion object {
        fun newInstance(): SearchNewsFragment {
            val fg = SearchNewsFragment()
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