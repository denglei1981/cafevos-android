package com.changanford.circle.ui.fragment

import android.os.Bundle
import com.changanford.circle.adapter.CircleListAdapter
import com.changanford.circle.databinding.FragmentCircleListBinding
import com.changanford.circle.utils.MUtils
import com.changanford.circle.viewmodel.CircleListViewModel
import com.changanford.common.basic.BaseFragment

/**
 *Author lcw
 *Time on 2021/9/18
 *Purpose
 */
class CircleListFragment : BaseFragment<FragmentCircleListBinding, CircleListViewModel>() {

    companion object {
        fun newInstance(type: String): CircleListFragment {
            val bundle = Bundle()
            bundle.putString("type", type)
            val fragment = CircleListFragment()
            fragment.arguments = bundle
            return fragment
        }
    }

    private val adapter by lazy {
        CircleListAdapter(requireContext())
    }

    override fun initView() {
        MUtils.scrollStopLoadImage(binding.ryCircle)
        binding.ryCircle.adapter = adapter
    }

    override fun initData() {
        val list =
            arrayListOf("", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "")
        adapter.setItems(list)
        adapter.notifyDataSetChanged()
    }
}