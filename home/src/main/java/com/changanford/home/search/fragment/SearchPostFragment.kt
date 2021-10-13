package com.changanford.home.search.fragment

import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import com.changanford.common.basic.BaseFragment
import com.changanford.home.databinding.HomeBaseRecyclerViewBinding
import com.changanford.home.search.adapter.SearchNewsResultAdapter
import com.changanford.home.search.data.SearchData
import com.changanford.home.search.request.PolySearchNewsResultViewModel

class SearchPostFragment : BaseFragment<HomeBaseRecyclerViewBinding, PolySearchNewsResultViewModel>() {

    val searchPostResultAdapter : SearchNewsResultAdapter by lazy {
        SearchNewsResultAdapter(this)
    }


    companion object {
        fun newInstance(): SearchPostFragment {
            val fg = SearchPostFragment()
            val bundle = Bundle()
            fg.arguments = bundle
            return fg
        }
    }
    override fun initView() {
        binding.recyclerView.layoutManager=
            LinearLayoutManager(requireActivity(), LinearLayoutManager.VERTICAL,false)
        binding.recyclerView.adapter=searchPostResultAdapter
    }

    override fun initData() {

    }
}