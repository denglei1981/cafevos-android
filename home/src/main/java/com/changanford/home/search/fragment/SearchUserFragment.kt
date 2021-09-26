package com.changanford.home.search.fragment

import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import com.changanford.common.basic.BaseFragment
import com.changanford.common.basic.EmptyViewModel
import com.changanford.home.databinding.HomeBaseRecyclerViewBinding
import com.changanford.home.search.adapter.SearchUserResultAdapter
import com.changanford.home.search.data.SearchData

class SearchUserFragment :BaseFragment<HomeBaseRecyclerViewBinding,EmptyViewModel>() {

    var userLists = mutableListOf<SearchData>()
    val searchUserResultAdapter :SearchUserResultAdapter by lazy {
        SearchUserResultAdapter(mutableListOf())
    }
    companion object {
        fun newInstance(): SearchUserFragment {
            val fg = SearchUserFragment()
            val bundle = Bundle()
            fg.arguments = bundle
            return fg
        }
    }

    override fun initView() {

        binding.recyclerView.layoutManager=LinearLayoutManager(requireActivity(),LinearLayoutManager.VERTICAL,false)
        userLists.add(SearchData())
        userLists.add(SearchData())
        userLists.add(SearchData())
        userLists.add(SearchData())
        userLists.add(SearchData())
        binding.recyclerView.adapter=SearchUserResultAdapter(userLists)

    }

    override fun initData() {

    }
}