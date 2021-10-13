package com.changanford.home.search.activity

import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.view.KeyEvent
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.TextView
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.GridLayoutManager
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
import com.changanford.home.adapter.HomeSearchAcAdapter
import com.changanford.home.bean.SearchKeyBean
import com.changanford.home.databinding.ActivityPolySearchBinding
import com.changanford.home.room.SearchRecordDatabase
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

    override fun initView() {
        searchType = intent.getIntExtra(SEARCH_TYPE, -1)

        ImmersionBar.with(this).fitsSystemWindows(true)


        flexboxLayoutManagerHistory = FlexboxLayoutManager(this)
        flexboxLayoutManagerHistory!!.flexDirection = FlexDirection.ROW
        flexboxLayoutManagerHistory!!.justifyContent = JustifyContent.FLEX_START
        binding.recyclerViewHistory.layoutManager = flexboxLayoutManagerHistory
        binding.recyclerViewHistory.adapter = historyAdapter
        binding.recyclerViewFind.layoutManager = GridLayoutManager(this, 2)
        binding.rvAuto.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        binding.rvAuto.adapter = sAdapter
        binding.recyclerViewFind.adapter = searchHotAdapter
        binding.ivBack.setOnClickListener {
            onBackPressed()
        }
        historyAdapter.setOnItemClickListener { adapter, view, position ->
            val bean = historyAdapter.getItem(position)
            search(bean.keyword, true)

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
                (binding.layoutSearch.searchContent.context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager)
                    .hideSoftInputFromWindow(
                        this@PolySearchActivity.currentFocus?.windowToken,
                        InputMethodManager.HIDE_NOT_ALWAYS
                    )
                if (actionId == EditorInfo.IME_ACTION_SEARCH ||
                    event != null && event.keyCode == KeyEvent.KEYCODE_ENTER
                ) {
                    if (!Objects.requireNonNull(binding.layoutSearch.searchContent.text).toString()
                            .trim { it <= ' ' }.isEmpty()
                    ) {
                        search(
                            binding.layoutSearch.searchContent.text.toString().trim { it <= ' ' },
                            false
                        )
                    }
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
//                        binding.verLin.visibility = View.VISIBLE
//                        binding.recordRecyclerView.visibility = View.VISIBLE
//                        binding.sRecyclerView.visibility = View.GONE
//                        binding.clearImg.visibility = View.GONE
                        binding.rvAuto.visibility = View.GONE
                    } else {
//                        binding.verLin.visibility = View.GONE
//                        binding.recordRecyclerView.visibility = View.GONE
//                        binding.sRecyclerView.visibility = View.VISIBLE
//                        binding.clearImg.visibility = View.VISIBLE
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
                    binding.tvHistory.visibility = View.VISIBLE
                    binding.recyclerViewHistory.visibility = View.VISIBLE
                } else {
                    binding.tvHistory.visibility = View.GONE
                    binding.recyclerViewHistory.visibility = View.GONE
                }
                historyAdapter.setList(it)
            })
        binding.layoutSearch.cancel.setOnClickListener {
            onBackPressed()
        }
    }


    fun search(searchContent: String, needHide: Boolean) {
        if (TextUtils.isEmpty(searchContent)) {
            toastShow("请输入你喜欢的内容")
            return
        }
        if (needHide) {
            HideKeyboardUtil.hideKeyboard(binding.layoutSearch.searchContent.windowToken)
        }
        val bundle = Bundle()
        bundle.putInt(SEARCH_TYPE, searchType)
        bundle.putString(SEARCH_CONTENT, searchContent)
//        bundle.putInt(SEARCH_KEY_ID, keyId)
        startARouter(ARouterHomePath.PloySearchResultActivity, bundle)
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
    }


}