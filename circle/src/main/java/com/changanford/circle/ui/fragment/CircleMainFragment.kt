package com.changanford.circle.ui.fragment

import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.changanford.circle.adapter.CircleInterestAdapter
import com.changanford.circle.adapter.CircleMainAddress
import com.changanford.circle.databinding.FragmentCircleMainBinding
import com.changanford.circle.viewmodel.CircleMainViewModel
import com.changanford.common.basic.BaseFragment
import com.changanford.common.basic.adapter.OnRecyclerViewItemClickListener
import com.changanford.common.router.path.ARouterCirclePath
import com.changanford.common.router.startARouter

/**
 *Author lcw
 *Time on 2021/9/23
 *Purpose
 */
class CircleMainFragment : BaseFragment<FragmentCircleMainBinding, CircleMainViewModel>() {

    companion object {
        fun newInstance(type: String): CircleMainFragment {
            val bundle = Bundle()
            bundle.putString("type", type)
            val fragment = CircleMainFragment()
            fragment.arguments = bundle
            return fragment
        }
    }

    private lateinit var layoutManager: RecyclerView.LayoutManager

    private val adapter by lazy { CircleInterestAdapter(requireContext()) }

    private val addressAdapter by lazy { CircleMainAddress(requireContext()) }

    override fun initView() {
        val type = arguments?.getString("type")
        type?.let { initLayoutManage(it) }
        binding.ryCircle.layoutManager = layoutManager
    }

    override fun initData() {
        val addressList = arrayListOf("", "", "", "")
        addressAdapter.setItems(addressList)
        addressAdapter.notifyDataSetChanged()
        val list = arrayListOf("", "", "", "", "", "", "", "", "")
        adapter.setItems(list)
        adapter.notifyDataSetChanged()

        addressAdapter.setOnItemClickListener(object :OnRecyclerViewItemClickListener{
            override fun onItemClick(view: View?, position: Int) {
                startARouter(ARouterCirclePath.CircleDetailsActivity)
            }

        })
    }

    private fun initLayoutManage(type: String) {
        layoutManager = when (type) {
            "0" -> {//
                binding.ryCircle.adapter = addressAdapter
                GridLayoutManager(requireContext(), 2)
//                LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
            }
            else -> {//九宫格
                binding.ryCircle.adapter = adapter
                GridLayoutManager(requireContext(), 3)
            }

        }
    }
}