package com.changanford.home.search.fragment

import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import com.changanford.common.basic.BaseFragment
import com.changanford.common.basic.EmptyViewModel
import com.changanford.home.data.ActBean
import com.changanford.home.databinding.HomeBaseRecyclerViewBinding
import com.changanford.home.search.adapter.SearchActsResultAdapter

class SearchActsFragment : BaseFragment<HomeBaseRecyclerViewBinding, EmptyViewModel>() {
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