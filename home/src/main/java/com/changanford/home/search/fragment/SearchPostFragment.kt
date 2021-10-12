package com.changanford.home.search.fragment

import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import com.changanford.common.basic.BaseFragment
import com.changanford.common.basic.EmptyViewModel
import com.changanford.home.databinding.HomeBaseRecyclerViewBinding
import com.changanford.home.search.adapter.SearchPostResultAdapter
import com.changanford.home.search.data.SearchData

class SearchPostFragment : BaseFragment<HomeBaseRecyclerViewBinding, EmptyViewModel>() {

    var  shopLists = mutableListOf<SearchData>()
    val searchPostResultAdapter : SearchPostResultAdapter by lazy {
        SearchPostResultAdapter(mutableListOf())
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
        shopLists.add(SearchData())
        shopLists.add(SearchData())
        shopLists.add(SearchData())
        shopLists.add(SearchData())
        shopLists.add(SearchData())
        binding.recyclerView.adapter=SearchPostResultAdapter(shopLists)
    }

    override fun initData() {

    }
}