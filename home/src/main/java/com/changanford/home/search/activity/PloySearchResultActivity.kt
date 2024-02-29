package com.changanford.home.search.activity

import android.content.Intent
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.alibaba.android.arouter.facade.annotation.Route
import com.changanford.common.MyApp
import com.changanford.common.adapter.HomeSearchAcAdapter
import com.changanford.common.basic.BaseActivity
import com.changanford.common.constant.JumpConstant
import com.changanford.common.constant.SearchTypeConstant
import com.changanford.common.router.path.ARouterHomePath
import com.changanford.common.util.HideKeyboardUtil
import com.changanford.common.util.JumpUtils
import com.changanford.common.util.bus.LiveDataBus
import com.changanford.common.util.bus.LiveDataBusKey
import com.changanford.common.util.gio.GioPageConstant
import com.changanford.common.utilext.toastShow
import com.changanford.home.R
import com.changanford.home.databinding.ActivityPloySearchResultBinding
import com.changanford.home.search.adapter.SearchResultViewpagerAdapter
import com.changanford.home.search.fragment.SearchActsFragment
import com.changanford.home.search.fragment.SearchAskFragment
import com.changanford.home.search.fragment.SearchNewsFragment
import com.changanford.home.search.fragment.SearchPostFragment
import com.changanford.home.search.fragment.SearchShopFragment
import com.changanford.home.search.fragment.SearchUserFragment
import com.changanford.home.search.request.PolySearchViewModel
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.gyf.immersionbar.ImmersionBar


@Route(path = ARouterHomePath.PloySearchResultActivity)
class PloySearchResultActivity :
    BaseActivity<ActivityPloySearchResultBinding, PolySearchViewModel>() {

    private var pagerAdapter: SearchResultViewpagerAdapter? = null

    private var fragmentList: ArrayList<Fragment> = arrayListOf()

    private var titleList = mutableListOf<String>()

    var searchContent = ""

    private var tagId: String = ""

    private val searchActsFragment: SearchActsFragment by lazy {
        SearchActsFragment.newInstance(searchContent)
    }
    private val searchNewsFragment: SearchNewsFragment by lazy {
        SearchNewsFragment.newInstance(searchContent)
    }
    private val searchPostFragment: SearchPostFragment by lazy {
        SearchPostFragment.newInstance(searchContent, tagId = tagId)
    }
    private val searchShopFragment: SearchShopFragment by lazy {
        SearchShopFragment.newInstance(searchContent)
    }
    private val searchUserFragment: SearchUserFragment by lazy {
        SearchUserFragment.newInstance(searchContent)
    }
    private val searchAskFragment: SearchAskFragment by lazy {
        SearchAskFragment.newInstance(searchContent)
    }

    //    //搜索列表
    private val sAdapter by lazy {
        HomeSearchAcAdapter()
    }

    override fun initView() {
        title = "搜索结果页"
        GioPageConstant.infoEntrance = "搜索结果页"
        ImmersionBar.with(this)
            .fitsSystemWindows(true)
            .statusBarColor(R.color.white)

        val searchType = intent.getIntExtra(JumpConstant.SEARCH_TYPE, -1) // 用于决定滑动到那个条目。
        searchContent = intent.getStringExtra(JumpConstant.SEARCH_CONTENT).toString()
        tagId = intent.getStringExtra(JumpConstant.SEARCH_TAG_ID).toString()

        binding.rvAuto.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        binding.rvAuto.adapter = sAdapter
        binding.layoutSearch.searchContent.setText(searchContent)
        fragmentList.add(searchNewsFragment)
        fragmentList.add(searchPostFragment)
        fragmentList.add(searchShopFragment)
        fragmentList.add(searchActsFragment)
        fragmentList.add(searchAskFragment)
        fragmentList.add(searchUserFragment)
        titleList.add(getString(R.string.home_news))
        titleList.add(getString(R.string.home_search_post))
        titleList.add(getString(R.string.home_search_shop))
        titleList.add(getString(R.string.home_acts))
        titleList.add("问答")
        titleList.add(getString(R.string.home_search_user))
        pagerAdapter = SearchResultViewpagerAdapter(this, fragmentList)
        binding.viewpager.adapter = pagerAdapter
        binding.viewpager.offscreenPageLimit = 6
        binding.viewpager.isSaveEnabled = false
        binding.searchTab.setSelectedTabIndicatorColor(
            ContextCompat.getColor(
                MyApp.mContext,
                R.color.blue_tab
            )
        )
        binding.searchTab.tabRippleColor = null
        TabLayoutMediator(binding.searchTab, binding.viewpager) { tab: TabLayout.Tab, i: Int ->
            tab.text = titleList[i]
        }.attach().apply {
            initTab()
        }
        binding.searchTab.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab) {
                selectTab(tab, true)
            }

            override fun onTabUnselected(tab: TabLayout.Tab) {
                selectTab(tab, false)
            }

            override fun onTabReselected(tab: TabLayout.Tab?) {
            }
        })

        binding.ivBack.setOnClickListener {
            onBackPressed()
        }
        binding.layoutSearch.searchContent.setOnClickListener {
            showAuto()
        }
        binding.layoutSearch.searchContent.setOnFocusChangeListener { v, hasFocus ->
            if (hasFocus) {
                showAuto()
            }
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
                    showAuto()
                }

            })
        sAdapter.setOnItemClickListener { _, _, position ->
            val bean = sAdapter.getItem(position)
            if (bean.jumpDataType != 0 && bean.jumpDataType != null) {//jump跳转
                JumpUtils.instans?.jump(bean.jumpDataType, bean.jumpDataValue)
            } else {
                search(bean.keyword, true)
            }
            binding.rvAuto.visibility = View.GONE
        }
        binding.layoutSearch.cancel.setOnClickListener {
            onBackPressed()
        }

        binding.layoutSearch.searchContent.setOnEditorActionListener(object :
            TextView.OnEditorActionListener {
            override fun onEditorAction(v: TextView?, actionId: Int, event: KeyEvent?): Boolean {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    val content = binding.layoutSearch.searchContent.text.toString()
                    if (content.isNotEmpty()) {
                        search(content, true)
                    }
                    return true;
                }
                return false;
            }
        })
        if (searchType != -1) {
            binding.viewpager.currentItem = searchType
        }
    }

    private fun showAuto() {
        val s = binding.layoutSearch.searchContent.text
        if (s.isNullOrEmpty()) {
            binding.rvAuto.visibility = View.GONE
        } else {
            sAdapter.searchContent = s.toString()
            binding.rvAuto.visibility = View.VISIBLE
            viewModel.getSearchAc(s.toString())
        }
    }

    // 从帖子tag 点击跳转过来
    fun backWithTag() {
        if (!TextUtils.isEmpty(tagId) && "null" != tagId) {

            val intent = Intent(this, PolySearchActivity::class.java)
            intent.putExtra(JumpConstant.SEARCH_TYPE, SearchTypeConstant.SEARCH_POST.toString())
            startActivity(intent)
            overridePendingTransition(0, 0);

        } else {
            onBackPressed()
        }

    }


    private fun search(searchContent: String, needHide: Boolean) {
        if (TextUtils.isEmpty(searchContent)) {
            toastShow("请输入你喜欢的内容")
            return
        }
        if (needHide) {
            HideKeyboardUtil.hideKeyboard(binding.layoutSearch.searchContent.windowToken)
        }

        LiveDataBus.get().with(LiveDataBusKey.UPDATE_SEARCH_RESULT).postValue(searchContent)

        viewModel.insertRecord(this, searchContent) // 异步写入本地数据库。

    }

    private fun selectTab(tab: TabLayout.Tab, isSelect: Boolean) {
        var mTabText = tab.customView?.findViewById<TextView>(R.id.tv_title)
        if (isSelect) {
            mTabText?.isSelected = true
            mTabText?.setTextColor(ContextCompat.getColor(MyApp.mContext, R.color.color_1700f4))
            mTabText?.paint?.isFakeBoldText = false
            mTabText?.textSize = 18f
        } else {
            mTabText?.setTextColor(ContextCompat.getColor(MyApp.mContext, R.color.black))
            mTabText?.textSize = 18f
            mTabText?.paint?.isFakeBoldText = false// 取消加粗

        }
    }

    var itemPunchWhat: Int = 0

    //初始化tab
    private fun initTab() {
        for (i in 0 until binding.searchTab.tabCount) {
            //寻找到控件
            val view: View = LayoutInflater.from(MyApp.mContext).inflate(R.layout.tab_home, null)
            val mTabText = view.findViewById<TextView>(R.id.tv_title)

            mTabText.text = titleList[i]
            if (itemPunchWhat == i) {
                mTabText.isSelected = true
                mTabText.setTextColor(ContextCompat.getColor(MyApp.mContext, R.color.color_1700f4))
                mTabText.paint.isFakeBoldText = false
                mTabText.textSize = 18f

            } else {
                mTabText.setTextColor(ContextCompat.getColor(MyApp.mContext, R.color.black))
                mTabText.textSize = 18f
                mTabText.paint.isFakeBoldText = false// 取消加粗
            }
            //更改选中项样式
            //设置样式
            binding.searchTab.getTabAt(i)?.customView = view
        }
    }

    override fun initData() {

    }

    override fun observe() {
        super.observe()
        viewModel.searchAutoLiveData.observe(this, Observer {
            if (it.isSuccess) {
                sAdapter.setList(it.data)
            }
        })
    }
}