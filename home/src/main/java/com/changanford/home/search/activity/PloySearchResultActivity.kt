package com.changanford.home.search.activity

import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.alibaba.android.arouter.facade.annotation.Route
import com.changanford.common.MyApp
import com.changanford.common.basic.BaseActivity
import com.changanford.common.basic.EmptyViewModel
import com.changanford.common.constant.JumpConstant
import com.changanford.common.router.path.ARouterHomePath
import com.changanford.home.R
import com.changanford.home.adapter.HomeSearchAcAdapter
import com.changanford.home.databinding.ActivityPloySearchResultBinding
import com.changanford.home.search.adapter.SearchResultViewpagerAdapter
import com.changanford.home.search.fragment.*
import com.changanford.home.search.request.PolySearchViewModel
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.gyf.immersionbar.ImmersionBar


@Route(path = ARouterHomePath.PloySearchResultActivity)
class PloySearchResultActivity :
    BaseActivity<ActivityPloySearchResultBinding, PolySearchViewModel>() {

    var pagerAdapter: SearchResultViewpagerAdapter? = null

    var fragmentList: ArrayList<Fragment> = arrayListOf()

    var titleList = mutableListOf<String>()

    var searchContent: String = ""

    //搜索列表
    private val sAdapter by lazy {
        HomeSearchAcAdapter()
    }

    override fun initView() {
        ImmersionBar.with(this)
            .fitsSystemWindows(true)
            .statusBarColor(R.color.color_ee)

        var searchType = intent.getIntExtra(JumpConstant.SEARCH_TYPE, -1) // 用于决定滑动到那个条目。
        searchContent = intent.getStringExtra(JumpConstant.SEARCH_CONTENT).toString()
        binding.rvAuto.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        binding.rvAuto.adapter = sAdapter

        binding.layoutSearch.searchContent.setText(searchContent)
        fragmentList.add(SearchActsFragment.newInstance(searchContent!!))
        fragmentList.add(SearchNewsFragment.newInstance(searchContent))
        fragmentList.add(SearchPostFragment.newInstance(searchContent))
        fragmentList.add(SearchShopFragment.newInstance(searchContent))
        fragmentList.add(SearchUserFragment.newInstance(searchContent))
        titleList.add(getString(R.string.home_acts))
        titleList.add(getString(R.string.home_news))
        titleList.add(getString(R.string.home_search_post))
        titleList.add(getString(R.string.home_search_shop))
        titleList.add(getString(R.string.home_search_user))
        pagerAdapter = SearchResultViewpagerAdapter(this, fragmentList)
        binding.viewpager.adapter = pagerAdapter

        binding.viewpager.isSaveEnabled = false
        binding.searchTab.setSelectedTabIndicatorColor(
            ContextCompat.getColor(
                MyApp.mContext,
                R.color.blue_tab
            )
        )
        binding.searchTab.tabRippleColor = null
//        setAppbarPercent()

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

//        binding.layoutSearch.searchContent.setOnClickListener {
//            onBackPressed()
//        }
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

        binding.layoutSearch.cancel.setOnClickListener {
            onBackPressed()
        }
    }

    private fun selectTab(tab: TabLayout.Tab, isSelect: Boolean) {
        var mTabText = tab.customView?.findViewById<TextView>(R.id.tv_title)
        if (isSelect) {
            mTabText?.isSelected = true
            mTabText?.setTextColor(ContextCompat.getColor(MyApp.mContext, R.color.black))
            mTabText?.paint?.isFakeBoldText = true
            mTabText?.textSize = 18f
        } else {
            mTabText?.setTextColor(ContextCompat.getColor(MyApp.mContext, R.color.black))
            mTabText?.textSize = 15f
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
                mTabText.setTextColor(ContextCompat.getColor(MyApp.mContext, R.color.black))
                mTabText.paint.isFakeBoldText = true
                mTabText.textSize = 18f

            } else {
                mTabText.setTextColor(ContextCompat.getColor(MyApp.mContext, R.color.black))
                mTabText.textSize = 15f
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