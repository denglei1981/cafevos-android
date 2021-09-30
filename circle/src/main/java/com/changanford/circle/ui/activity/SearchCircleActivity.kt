package com.changanford.circle.ui.activity

import android.os.Bundle
import com.alibaba.android.arouter.facade.annotation.Route
import com.changanford.circle.databinding.ActivitySearchCircleBinding
import com.changanford.circle.viewmodel.SearchCircleViewModel
import com.changanford.common.basic.BaseActivity
import com.changanford.common.router.path.ARouterCirclePath
import com.changanford.common.util.AppUtils

import android.view.inputmethod.EditorInfo
import com.changanford.circle.R

import com.changanford.circle.adapter.CircleListAdapter
import com.changanford.circle.utils.HideKeyboardUtil
import com.changanford.common.router.startARouter
import com.changanford.common.utilext.toast


/**
 *Author lcw
 *Time on 2021/9/30
 *Purpose 圈子搜索
 */
@Route(path = ARouterCirclePath.SearchCircleActivity)
class SearchCircleActivity : BaseActivity<ActivitySearchCircleBinding, SearchCircleViewModel>() {

    private val adapter by lazy {
        CircleListAdapter()
    }

    private var page = 1

    override fun initView() {
        AppUtils.setStatusBarMarginTop(binding.llTitle, this)
        binding.ryCircle.adapter = adapter
        initListener()
    }

    private fun initListener() {
        binding.run {
            tvCancel.setOnClickListener { finish() }
            tvSearch.setOnEditorActionListener { v, actionId, event ->
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    val content = v.text.toString()
                    if (content.isEmpty()) {
                        "请输入关键字".toast()
                    } else {
                        page = 1
                        viewModel.getData(content, page)
                    }
                    HideKeyboardUtil.hideKeyboard(tvSearch.windowToken)
                }
                false
            }
        }
        adapter.loadMoreModule.setOnLoadMoreListener {
            page++
            val content = binding.tvSearch.text.toString()
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
        viewModel.circleBean.observe(this, {
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
        })
    }
}