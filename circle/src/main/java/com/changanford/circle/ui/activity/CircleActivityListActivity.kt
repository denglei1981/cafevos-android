package com.changanford.circle.ui.activity

import com.alibaba.android.arouter.facade.annotation.Route
import com.changanford.circle.adapter.circle.CircleActivityListAdapter
import com.changanford.circle.databinding.ActivityCircleActivityListBinding
import com.changanford.circle.viewmodel.CircleActivityListViewModel
import com.changanford.common.adapter.SearchActsResultAdapter
import com.changanford.common.basic.BaseActivity
import com.changanford.common.bean.TestBean
import com.changanford.common.constant.IntentKey
import com.changanford.common.router.path.ARouterCirclePath
import com.changanford.common.util.JumpUtils
import com.changanford.common.util.MConstant
import com.changanford.common.util.toolbar.Builder
import com.changanford.common.util.toolbar.initTitleBar

/**
 *Author lcw
 *Time on 2022/8/18
 *Purpose 圈内活动列表
 */
@Route(path = ARouterCirclePath.CircleActivityListActivity)
class CircleActivityListActivity :
    BaseActivity<ActivityCircleActivityListBinding, CircleActivityListViewModel>() {

    private val activityListAdapter by lazy {
        SearchActsResultAdapter()
    }

    private var circleId = ""
    private var page = 1
    private var isFirst = true

    override fun initView() {
        circleId = intent.getStringExtra(IntentKey.CREATE_NOTICE_CIRCLE_ID).toString()
        binding.run {
            title.toolbar.initTitleBar(
                this@CircleActivityListActivity,
                Builder().apply { title = "圈内活动" })
            ryActivity.adapter = activityListAdapter
        }
        initMyListener()
    }

    private fun initMyListener() {
        binding.refreshLayout.setOnRefreshListener {
            page = 1
            initData()
        }
        activityListAdapter.setOnItemClickListener { adapter, view, position ->
            val bean = activityListAdapter.getItem(position)
            JumpUtils.instans?.jump(bean.jumpDto.jumpCode, bean.jumpDto.jumpVal)

        }
        activityListAdapter.loadMoreModule.setOnLoadMoreListener {
            page++
            viewModel.circleActivity(page, circleId)
        }
    }

    override fun initData() {
        viewModel.circleActivity(page, circleId)
    }

    override fun onResume() {
        super.onResume()
        if (isFirst) {
            isFirst = false
        } else {
            if (MConstant.token.isNotEmpty()) {
                page = 1
                initData()
            }
        }
    }

    override fun observe() {
        super.observe()
        viewModel.circleActivityBean.observe(this) {
            if (page == 1) {
                binding.refreshLayout.finishRefresh()
                activityListAdapter.setList(it.dataList)
            } else {
                activityListAdapter.addData(it.dataList)
                activityListAdapter.loadMoreModule.loadMoreComplete()
            }
            if (it.dataList.size != 20) {
                activityListAdapter.loadMoreModule.loadMoreEnd()
            }
        }
    }
}