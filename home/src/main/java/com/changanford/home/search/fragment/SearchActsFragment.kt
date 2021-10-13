package com.changanford.home.search.fragment

import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import com.changanford.common.basic.BaseFragment
import com.changanford.home.data.ActBean
import com.changanford.home.databinding.HomeBaseRecyclerViewBinding
import com.changanford.home.search.adapter.SearchActsResultAdapter
import com.changanford.home.search.request.PolySearchNewsResultViewModel

class SearchActsFragment : BaseFragment<HomeBaseRecyclerViewBinding, PolySearchNewsResultViewModel>() {
    var  shopLists = mutableListOf<ActBean>()

    val searchActsResultAdapter : SearchActsResultAdapter by lazy {
        SearchActsResultAdapter()
    }
    companion object {
        fun newInstance(): SearchActsFragment {
            val fg = SearchActsFragment()
            val bundle = Bundle()
            fg.arguments = bundle
            return fg
        }
    }

    override fun initView() {
        binding.recyclerView.layoutManager=
            LinearLayoutManager(requireActivity(), LinearLayoutManager.VERTICAL,false)

        binding.recyclerView.adapter= SearchActsResultAdapter()
    }

    override fun initData() {

    }
}