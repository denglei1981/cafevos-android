package com.changanford.circle.ui.activity

import android.os.Bundle
import android.view.inputmethod.EditorInfo
import com.alibaba.android.arouter.facade.annotation.Route
import com.changanford.circle.R
import com.changanford.circle.adapter.circle.CircleSearchAdapter
import com.changanford.circle.databinding.ActivitySearchCircleBinding
import com.changanford.circle.utils.HideKeyboardUtil
import com.changanford.circle.viewmodel.SearchCircleViewModel
import com.changanford.common.basic.BaseActivity
import com.changanford.common.router.path.ARouterCirclePath
import com.changanford.common.router.startARouter
import com.changanford.common.utilext.toast
import com.gyf.immersionbar.ImmersionBar


/**
 *Author lcw
 *Time on 2021/9/30
 *Purpose 圈子搜索
 */
@Route(path = ARouterCirclePath.SearchCircleActivity)
class SearchCircleActivity : BaseActivity<ActivitySearchCircleBinding, SearchCircleViewModel>() {

    private val adapter by lazy {
        CircleSearchAdapter()
    }

    private var page = 1
    private val searchRight = "搜索"
    private val cancelRight = "取消"

    override fun initView() {
        ImmersionBar.with(this)
            .fitsSystemWindows(true)
            .statusBarColor(R.color.white)
//        AppUtils.setStatusBarMarginTop(binding.llTitle, this)
        binding.ryCircle.adapter = adapter
        binding.layoutSearch.cancel.text = searchRight
        initListener()
    }

    private fun initListener() {
        binding.run {
            ivBack.setOnClickListener { finish() }
            layoutSearch.cancel.setOnClickListener {
                if (binding.layoutSearch.cancel.text == searchRight) {
                    val content = binding.layoutSearch.searchContent.text.toString()
                    if (content.isEmpty()) {
                        "请输入关键字".toast()
                    } else {
                        page = 1
                        viewModel.getData(content, page)
                        binding.layoutSearch.cancel.text = cancelRight
                    }
                } else {
                    finish()
                }
            }
            layoutSearch.searchContent.setOnEditorActionListener { v, actionId, event ->
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    val content = v.text.toString()
                    if (content.isEmpty()) {
                        "请输入关键字".toast()
                    } else {
                        page = 1
                        viewModel.getData(content, page)
                        binding.layoutSearch.cancel.text = cancelRight
                    }
                    HideKeyboardUtil.hideKeyboard(layoutSearch.cancel.windowToken)
                }
                false
            }
        }
        adapter.loadMoreModule.setOnLoadMoreListener {
            page++
            val content = binding.layoutSearch.searchContent.text.toString()
            viewModel.getData(content, page)
        }
        adapter.setOnItemClickListener { _, view, position ->
            val bundle = Bundle()
            bundle.putString("circleId", adapter.data[position].circleId)
            startARouter(ARouterCirclePath.CircleDetailsActivity, bundle)
        }
    }

    override fun initData() {

    }

    override fun observe() {
        super.observe()
        viewModel.circleBean.observe(this) {
            adapter.searchContent = binding.layoutSearch.searchContent.text.toString()
            if (page == 1) {
                if (it.dataList.size == 0) {
                    adapter.setEmptyView(R.layout.circle_empty_layout)
                }
                adapter.setList(it.dataList)
            } else {
                adapter.addData(it.dataList)
                adapter.loadMoreModule.loadMoreComplete()
            }
            if (it.dataList.size != 20) {
                adapter.loadMoreModule.loadMoreEnd()
            }
        }
    }
}