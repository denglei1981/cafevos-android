package com.changanford.home.search.activity

import android.os.Bundle
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.view.KeyEvent
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.TextView
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.alibaba.android.arouter.facade.annotation.Route
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.listener.OnItemClickListener
import com.changanford.common.basic.BaseActivity
import com.changanford.common.constant.JumpConstant.SEARCH_CONTENT
import com.changanford.common.constant.JumpConstant.SEARCH_TYPE
import com.changanford.common.router.path.ARouterHomePath
import com.changanford.common.router.startARouter
import com.changanford.common.util.HideKeyboardUtil
import com.changanford.common.util.JumpUtils
import com.changanford.common.utilext.toastShow
import com.changanford.home.R
import com.changanford.home.adapter.HomeSearchAcAdapter
import com.changanford.home.bean.SearchKeyBean
import com.changanford.home.databinding.ActivityPolySearchBinding
import com.changanford.home.room.SearchRecordDatabase
import com.changanford.home.room.SearchRecordEntity
import com.changanford.home.search.adapter.SearchHistoryAdapter
import com.changanford.home.search.adapter.SearchHotAdapter
import com.changanford.home.search.request.PolySearchViewModel
import com.google.android.flexbox.FlexDirection
import com.google.android.flexbox.FlexboxLayoutManager
import com.google.android.flexbox.JustifyContent
import com.gyf.immersionbar.ImmersionBar
import java.util.*

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
    val historyAdapter: SearchHistoryAdapter by lazy {
        SearchHistoryAdapter(mutableListOf())
    }

    //搜索列表
    private val sAdapter by lazy {
        HomeSearchAcAdapter()
    }

    var searchType = -1

    var historyList: MutableList<SearchRecordEntity>? = null
    override fun initView() {
        val searchTypStr = intent.getStringExtra(SEARCH_TYPE)
        searchType = if (!TextUtils.isEmpty(searchTypStr)) {
            searchTypStr?.toIntOrNull()!!
        } else {
            -1
        }
        ImmersionBar.with(this)
            .fitsSystemWindows(true)
            .statusBarColor(R.color.color_F4)

        flexboxLayoutManagerHistory = FlexboxLayoutManager(this)
        flexboxLayoutManagerHistory!!.flexDirection = FlexDirection.ROW
        flexboxLayoutManagerHistory!!.justifyContent = JustifyContent.FLEX_START


        binding.recyclerViewHistory.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)

        binding.recyclerViewHistory.adapter = historyAdapter


        binding.recyclerViewFind.layoutManager = flexboxLayoutManagerHistory

        binding.smartLayout.setEnableLoadMore(false)
        binding.smartLayout.setEnableOverScrollDrag(true)
        binding.rvAuto.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)

        binding.rvAuto.adapter = sAdapter

        binding.recyclerViewFind.adapter = searchHotAdapter


        binding.ivBack.setOnClickListener {
            onBackPressed()
        }
        binding.tvClear.setOnClickListener {
            // 清空历史记录。
            viewModel.clearRecord(this)
        }
        historyAdapter.setOnItemClickListener { adapter, view, position ->
            val bean = historyAdapter.getItem(position)
            search(bean.keyword, true)
//            if (position == 7 && !historyAdapter.isExpand) {
//                historyList?.let {
//                    historyAdapter.setExpand(true)
//                    historyAdapter.setList(it)
//                }
//            } else {
//
//            }
        }
        sAdapter.setOnItemClickListener(object : OnItemClickListener {
            override fun onItemClick(adapter: BaseQuickAdapter<*, *>, view: View, position: Int) {
                val bean = sAdapter.getItem(position)
                if (bean.jumpDataType != 0) {//jump跳转
                    JumpUtils.instans?.jump(bean.jumpDataType, bean.jumpDataValue)
                } else {
                    search(bean.keyword, true)
                }
            }
        })
        searchHotAdapter.setOnItemClickListener(object : OnItemClickListener {
            override fun onItemClick(adapter: BaseQuickAdapter<*, *>, view: View, position: Int) {
                val bean = searchHotAdapter.getItem(position)
                if (bean.jumpDataType != 0) {//jump跳转
                    JumpUtils.instans?.jump(bean.jumpDataType, bean.jumpDataValue)
                } else {
                    search(bean.keyword, true)
                }
            }
        })
        binding.layoutSearch.searchContent.setOnEditorActionListener { v: TextView?, actionId: Int, event: KeyEvent? ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH || actionId == 0) {
                HideKeyboardUtil.showSoftInput(binding.layoutSearch.searchContent)
                if (actionId == EditorInfo.IME_ACTION_SEARCH || (event != null && event.keyCode == KeyEvent.KEYCODE_ENTER && event.keyCode == KeyEvent.ACTION_UP)) {
                    search(binding.layoutSearch.searchContent.text.toString(), false)
                }
            }
            false
        }

        binding.layoutSearch.searchContent.addTextChangedListener(
            object : TextWatcher {
                override fun afterTextChanged(s: Editable?) {
                }

                override fun beforeTextChanged(
                    s: CharSequence?,
                    start: Int,
                    count: Int,
                    after: Int
                ) {
                }

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    if (s.isNullOrEmpty()) {
                        binding.rvAuto.visibility = View.GONE
                    } else {
                        binding.rvAuto.visibility = View.VISIBLE
                        viewModel.getSearchAc(s.toString())
                    }
                }

            })


        //获取本地搜索历史
        SearchRecordDatabase.getInstance(this).getSearchRecordDao().findAll()
            .observe(this, Observer {
                if (!it.isNullOrEmpty()) {//有数据
                    Collections.reverse(it)//倒叙
                    binding.gHis.visibility = View.VISIBLE
                } else {
                    binding.gHis.visibility = View.GONE
                }
                historyList = it as? MutableList<SearchRecordEntity>
                historyAdapter.setList(it)
//                if (it.size > 8 && !historyAdapter.isExpand) {
//                    val subList = it.subList(0, 8) as MutableList
//                    historyAdapter.setList(subList)
//                } else {
//
//                }

            })
        binding.layoutSearch.cancel.setOnClickListener {
            onBackPressed()
        }

        HideKeyboardUtil.showSoftInput(binding.layoutSearch.searchContent)
    }

    var searchContent = ""
    fun search(searchContent: String, needHide: Boolean) {
        this.searchContent = searchContent
        if (TextUtils.isEmpty(searchContent)) {
            toastShow("请输入你喜欢的内容")
            return
        }
        if (needHide) {
            HideKeyboardUtil.hideKeyboard(binding.layoutSearch.searchContent.windowToken)
        }
        binding.layoutSearch.searchContent.setText(searchContent)
        isPs()

    }

    fun jumpNomarl() {
        val bundle = Bundle()
        bundle.putInt(SEARCH_TYPE, searchType)
        bundle.putString(SEARCH_CONTENT, searchContent)
        startARouter(ARouterHomePath.PloySearchResultActivity, bundle)
    }

    fun isPs() {
        viewModel.getSearchContent(searchContent)
        viewModel.insertRecord(this, searchContent) // 异步写入本地数据库。
    }

    override fun initData() {
        viewModel.getSearchHistoryList()
        viewModel.getSearchKeyList()
    }

    override fun observe() {
        super.observe()
        viewModel.searchKeyLiveData.observe(this, Observer {
            if (it.isSuccess) {
                searchHotAdapter.setNewInstance(it.data as? MutableList<SearchKeyBean>)
            } else {

            }
        })
        viewModel.searchAutoLiveData.observe(this, Observer {
            if (it.isSuccess) {
                sAdapter.setList(it.data)
            }
        })
        viewModel.searchKolingLiveData.observe(this, Observer {
            if (it.isSuccess) {
                if (it.data.extend != null && it.data.extend.jumpDataType > 0) {
                    val jumpDataType = it.data.extend.jumpDataType
                    val jumpDataValue = it.data.extend.jumpDataValue
                    JumpUtils.instans!!.jump(jumpDataType, jumpDataValue)
                } else {
                    jumpNomarl()
                }
            } else {
                jumpNomarl()
            }
        })


    }


}