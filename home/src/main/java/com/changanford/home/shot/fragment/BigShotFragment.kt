package com.changanford.home.shot.fragment

import android.os.Bundle
import com.changanford.common.basic.BaseFragment
import com.changanford.common.basic.EmptyViewModel
import com.changanford.home.databinding.FragmentBigShotBinding

/**
 *  大咖
 * */
class BigShotFragment : BaseFragment<FragmentBigShotBinding, EmptyViewModel>() {


    companion object {
        fun newInstance(): BigShotFragment {
            val fg = BigShotFragment()
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