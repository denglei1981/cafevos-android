package com.changanford.circle.ui.fragment

import android.os.Bundle
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.changanford.circle.adapter.CircleDetailsBarAdapter
import com.changanford.circle.databinding.FragmentCircleDetailsBinding
import com.changanford.circle.utils.MUtils
import com.changanford.circle.viewmodel.CircleDetailsViewModel
import com.changanford.common.basic.BaseFragment

/**
 *Author lcw
 *Time on 2021/9/22
 *Purpose
 */
class CircleDetailsFragment : BaseFragment<FragmentCircleDetailsBinding, CircleDetailsViewModel>() {

    private lateinit var staggeredGridLayoutManager: StaggeredGridLayoutManager

    private val adapter by lazy { CircleDetailsBarAdapter(requireContext()) }

    companion object {
        fun newInstance(type: String): CircleDetailsFragment {
            val bundle = Bundle()
            bundle.putString("type", type)
            val fragment = CircleDetailsFragment()
            fragment.arguments = bundle
            return fragment
        }
    }

    override fun initView() {
        MUtils.scrollStopLoadImage(binding.ryCircle)
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