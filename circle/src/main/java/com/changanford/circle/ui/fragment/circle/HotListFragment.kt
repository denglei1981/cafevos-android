package com.changanford.circle.ui.fragment.circle

import android.os.Bundle
import com.changanford.circle.databinding.FragmentHotlistBinding
import com.changanford.circle.ui.fragment.CircleListFragment
import com.changanford.circle.viewmodel.circle.NewCircleViewModel
import com.changanford.common.basic.BaseFragment
import com.changanford.common.wutil.FlowLayoutManager

/**
 * @Author : wenke
 * @Time : 2022/1/5 0005
 * @Description : HotListFragment
 */
class HotListFragment:BaseFragment<FragmentHotlistBinding, NewCircleViewModel>() {
    companion object {
        fun newInstance(type: Int): CircleListFragment {
            val bundle = Bundle()
            bundle.putInt("type", type)
            val fragment = CircleListFragment()
            fragment.arguments = bundle
            return fragment
        }
    }
    override fun initView() {
        binding.recyclerView.layoutManager=FlowLayoutManager(requireContext(),true,true)
    }

    override fun initData() {

    }
}