package com.changanford.circle.ui.fragment

import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.changanford.circle.adapter.CircleMainAddress
import com.changanford.circle.bean.CircleInfo
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

    private var type = ""

    private lateinit var layoutManager: RecyclerView.LayoutManager

    //九宫格
//    private val adapter by lazy { CircleInterestAdapter(requireContext()) }

    //一行2个
    private val addressAdapter by lazy { CircleMainAddress(requireContext()) }

    override fun initView() {
        //0地域 1兴趣
        type = arguments?.getString("type").toString()
        initLayoutManage()
        binding.ryCircle.layoutManager = layoutManager
        binding.ryCircle.adapter = addressAdapter
        binding.ryCircle.isSaveEnabled = false
    }

    override fun initData() {

        addressAdapter.setOnItemClickListener(object : OnRecyclerViewItemClickListener {
            override fun onItemClick(view: View?, position: Int) {
                val bundle = Bundle()
                bundle.putString("circleId", addressAdapter.getItem(position)?.circleId.toString())
                startARouter(ARouterCirclePath.CircleDetailsActivity, bundle)
            }

        })
    }

    private fun initLayoutManage() {
        layoutManager = GridLayoutManager(requireContext(), 2)
    }

    fun setData(list: ArrayList<CircleInfo>) {
        addressAdapter.setItems(list)
        addressAdapter.notifyDataSetChanged()
    }
}