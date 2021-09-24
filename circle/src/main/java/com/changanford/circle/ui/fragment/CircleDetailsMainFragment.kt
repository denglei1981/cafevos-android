package com.changanford.circle.ui.fragment

import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.changanford.circle.adapter.CircleDetailsBarAdapter
import com.changanford.circle.databinding.FragmentCircleMainBinding
import com.changanford.circle.viewmodel.CircleDetailsViewModel
import com.changanford.common.basic.BaseFragment
import com.changanford.common.basic.adapter.OnRecyclerViewItemClickListener
import com.changanford.common.router.path.ARouterCirclePath
import com.changanford.common.router.startARouter

/**
 *Author lcw
 *Time on 2021/9/22
 *Purpose
 */
class CircleDetailsMainFragment : BaseFragment<FragmentCircleMainBinding, CircleDetailsViewModel>() {

    private lateinit var staggeredGridLayoutManager: StaggeredGridLayoutManager

    private val adapter by lazy { CircleDetailsBarAdapter(requireContext()) }

    companion object {
        fun newInstance(type: String): CircleDetailsMainFragment {
            val bundle = Bundle()
            bundle.putString("type", type)
            val fragment = CircleDetailsMainFragment()
            fragment.arguments = bundle
            return fragment
        }
    }

    override fun initView() {

        staggeredGridLayoutManager = StaggeredGridLayoutManager(
            2,
            StaggeredGridLayoutManager.VERTICAL
        )
        staggeredGridLayoutManager.spanCount
        binding.ryCircle.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                staggeredGridLayoutManager.invalidateSpanAssignments()
            }
        })
        binding.ryCircle.layoutManager = staggeredGridLayoutManager

        binding.ryCircle.adapter = adapter

        adapter.setOnItemClickListener(object :OnRecyclerViewItemClickListener{
            override fun onItemClick(view: View?, position: Int) {
                startARouter(ARouterCirclePath.PostGraphicActivity)
            }

        })
    }

    override fun initData() {
        val list = arrayListOf(
            "http://139.186.199.89:8008/images/20210909/1631182063471.jpg",
            "http://139.186.199.89:8008/images/20210909/1631182101477.jpg",
            "http://139.186.199.89:8008//images/20210909/1631182170004.jpg",
            "http://139.186.199.89:8008/images/20210909/1631182063471.jpg",
            "http://139.186.199.89:8008/images/20210909/1631182101477.jpg",
            "http://139.186.199.89:8008//images/20210909/1631182170004.jpg",
            "http://139.186.199.89:8008/images/20210909/1631182063471.jpg",
            "http://139.186.199.89:8008/images/20210909/1631182101477.jpg",
            "http://139.186.199.89:8008//images/20210909/1631182170004.jpg"
        )
        adapter.setItems(list)
        adapter.notifyDataSetChanged()
    }
}