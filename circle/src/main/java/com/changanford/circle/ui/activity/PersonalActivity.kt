package com.changanford.circle.ui.activity

import com.alibaba.android.arouter.facade.annotation.Route
import com.changanford.circle.adapter.PersonalAdapter
import com.changanford.circle.databinding.ActivityPersonalBinding
import com.changanford.circle.viewmodel.PersonalViewModel
import com.changanford.common.basic.BaseActivity
import com.changanford.common.router.path.ARouterCirclePath
import com.changanford.common.util.AppUtils

/**
 *Author lcw
 *Time on 2021/9/22
 *Purpose
 */
@Route(path = ARouterCirclePath.PersonalActivity)
class PersonalActivity : BaseActivity<ActivityPersonalBinding, PersonalViewModel>() {

    private var page = 1
    private var circleId = ""
    private val adapter by lazy { PersonalAdapter() }

    override fun initView() {
        circleId = intent.getStringExtra("circleId").toString()
        binding.ryPersonal.adapter = adapter
        binding.title.run {
            AppUtils.setStatusBarMarginTop(binding.title.root, this@PersonalActivity)
            tvTitle.text = "成员"
            ivBack.setOnClickListener { finish() }
        }
        adapter.loadMoreModule.setOnLoadMoreListener {
            page++
            viewModel.getData(circleId, page)
        }
    }

    override fun initData() {
        viewModel.getData(circleId, page)
    }

    override fun observe() {
        super.observe()
        viewModel.personalBean.observe(this,{
            if (page == 1) {
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