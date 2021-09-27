package com.changanford.home.search.activity

import androidx.recyclerview.widget.GridLayoutManager
import com.alibaba.android.arouter.facade.annotation.Route
import com.changanford.common.basic.BaseActivity
import com.changanford.common.basic.EmptyViewModel

import com.changanford.common.router.path.ARouterHomePath
import com.changanford.common.router.startARouter
import com.changanford.home.databinding.ActivityPolySearchBinding
import com.changanford.home.search.adapter.SearchHistoryAdapter
import com.changanford.home.search.adapter.SearchHotAdapter
import com.changanford.home.search.data.SearchData
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
class PolySearchActivity : BaseActivity<ActivityPolySearchBinding, EmptyViewModel>() {
    var flexboxLayoutManagerHistory: FlexboxLayoutManager? = null
    var searchHotAdapter: SearchHotAdapter? = null
    var historyAdapter: SearchHistoryAdapter? = null

    override fun initView() {
        ImmersionBar.with(this).fitsSystemWindows(true)

    }

    override fun initData() {
        var historyList = mutableListOf<SearchData>()
        historyList.add(SearchData("长安福特"))
        historyList.add(SearchData("开车开一天是什么体验"))
        historyList.add(SearchData("迪丽热巴,穷哈"))
        historyList.add(SearchData("上热门"))
        historyList.add(SearchData("美女大集合"))
        historyList.add(SearchData("豪车盛宴"))
        historyAdapter = SearchHistoryAdapter(historyList)
        flexboxLayoutManagerHistory = FlexboxLayoutManager(this)
        flexboxLayoutManagerHistory!!.flexDirection = FlexDirection.ROW
        flexboxLayoutManagerHistory!!.justifyContent = JustifyContent.FLEX_START
        binding.recyclerViewHistory.layoutManager = flexboxLayoutManagerHistory
        binding.recyclerViewHistory.adapter = historyAdapter
        binding.recyclerViewFind.layoutManager = GridLayoutManager(this, 2)
        searchHotAdapter= SearchHotAdapter(historyList)
        binding.recyclerViewFind.adapter=searchHotAdapter
        binding.ivBack.setOnClickListener {
             onBackPressed()
        }
        historyAdapter?.setOnItemClickListener { adapter, view, position ->
            startARouter(ARouterHomePath.PloySearchResultActivity)
        }

    }


}