package com.changanford.home.search.activity

import androidx.lifecycle.Observer
import androidx.recyclerview.widget.GridLayoutManager
import com.alibaba.android.arouter.facade.annotation.Route
import com.changanford.common.basic.BaseActivity

import com.changanford.common.router.path.ARouterHomePath
import com.changanford.common.router.startARouter
import com.changanford.home.bean.SearchKeyBean
import com.changanford.home.databinding.ActivityPolySearchBinding
import com.changanford.home.search.adapter.SearchHistoryAdapter
import com.changanford.home.search.adapter.SearchHotAdapter
import com.changanford.home.search.request.PolySearchViewModel
import com.google.android.flexbox.FlexDirection
import com.google.android.flexbox.FlexboxLayoutManager
import com.google.android.flexbox.JustifyContent
import com.gyf.immersionbar.ImmersionBar

/**
 *  聚合搜索页面
 *
 *  author:ny
 *  * */
@Route(path = ARouterHomePath.PolySearchActivity)
class PolySearchActivity : BaseActivity<ActivityPolySearchBinding, PolySearchViewModel>() {
    var flexboxLayoutManagerHistory: FlexboxLayoutManager? = null

    val searchHotAdapter: SearchHotAdapter by lazy {
        SearchHotAdapter(arrayListOf())
    }
    var historyAdapter: SearchHistoryAdapter? = null

    override fun initView() {
        ImmersionBar.with(this).fitsSystemWindows(true)
        var historyList = mutableListOf<SearchKeyBean>()
        historyAdapter = SearchHistoryAdapter(historyList)
        flexboxLayoutManagerHistory = FlexboxLayoutManager(this)
        flexboxLayoutManagerHistory!!.flexDirection = FlexDirection.ROW
        flexboxLayoutManagerHistory!!.justifyContent = JustifyContent.FLEX_START
        binding.recyclerViewHistory.layoutManager = flexboxLayoutManagerHistory
        binding.recyclerViewHistory.adapter = historyAdapter
        binding.recyclerViewFind.layoutManager = GridLayoutManager(this, 2)

        binding.recyclerViewFind.adapter = searchHotAdapter
        binding.ivBack.setOnClickListener {
            onBackPressed()
        }
        historyAdapter?.setOnItemClickListener { adapter, view, position ->
            startARouter(ARouterHomePath.PloySearchResultActivity)
        }
    }

    override fun initData() {

          viewModel.getSearchKeyList()
    }

    override fun observe() {
        super.observe()
        viewModel.searchKeyLiveData.observe(this, Observer {
                if(it.isSuccess){
                    searchHotAdapter.setNewInstance(it.data as? MutableList<SearchKeyBean>)
                }else{

                }

        })
    }


}