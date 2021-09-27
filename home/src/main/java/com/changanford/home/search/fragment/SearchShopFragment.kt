package com.changanford.home.search.fragment

import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import com.changanford.common.basic.BaseFragment
import com.changanford.common.basic.EmptyViewModel
import com.changanford.home.databinding.HomeBaseRecyclerViewBinding
import com.changanford.home.search.adapter.SearchShopResultAdapter
import com.changanford.home.search.data.SearchData

class SearchShopFragment: BaseFragment<HomeBaseRecyclerViewBinding, EmptyViewModel>() {


    var  shopLists = mutableListOf<SearchData>()
    val searchShopResultAdapter : SearchShopResultAdapter by lazy {
        SearchShopResultAdapter(mutableListOf())
    }
    companion object {
        fun newInstance(): SearchShopFragment {
            val fg = SearchShopFragment()
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
        binding.recyclerView.adapter=SearchShopResultAdapter(shopLists)
    }

    override fun initData() {

    }
}