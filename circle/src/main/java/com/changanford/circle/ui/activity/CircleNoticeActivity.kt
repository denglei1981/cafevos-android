package com.changanford.circle.ui.activity

import android.view.View
import com.alibaba.android.arouter.facade.annotation.Route
import com.changanford.circle.adapter.CircleNoticeAdapter
import com.changanford.circle.databinding.ActivityCircleNoticeBinding
import com.changanford.circle.viewmodel.CircleNoticeViewMode
import com.changanford.common.basic.BaseActivity
import com.changanford.common.bean.TestBean
import com.changanford.common.constant.IntentKey
import com.changanford.common.router.path.ARouterCirclePath
import com.changanford.common.router.startARouter
import com.changanford.common.util.AppUtils

/**
 *Author lcw
 *Time on 2022/8/17
 *Purpose 圈子公告
 */
@Route(path = ARouterCirclePath.CircleNoticeActivity)
class CircleNoticeActivity : BaseActivity<ActivityCircleNoticeBinding, CircleNoticeViewMode>() {

    private val noticeAdapter by lazy {
        CircleNoticeAdapter()
    }

    private var page = 1
    private var circleId = ""
    private var hasLookNotice = false

    override fun initView() {
        circleId = intent.getStringExtra(IntentKey.CREATE_NOTICE_CIRCLE_ID).toString()
        hasLookNotice = intent.getBooleanExtra(IntentKey.HAS_LOOK_NOTICE, false)
        binding.tvRightMenu.visibility = if (hasLookNotice) View.VISIBLE else View.GONE
        AppUtils.setStatusBarMarginTop(binding.rlTitle, this)
        binding.ryNotice.adapter = noticeAdapter
        initMyListener()
    }

    private fun initMyListener() {
        binding.run {
            ivBack.setOnClickListener { finish() }
            tvRightMenu.setOnClickListener {
                startARouter(ARouterCirclePath.MyCircleNoticeActivity)
            }
        }
        noticeAdapter.loadMoreModule.setOnLoadMoreListener {
            page++
            viewModel.circleNotices(page, circleId)
        }
    }

    override fun initData() {
        viewModel.circleNotices(page, circleId)
    }

    override fun observe() {
        super.observe()
        viewModel.noticeListBean.observe(this) {
            if (page == 1) {
                noticeAdapter.setList(it.dataList)
            } else {
                noticeAdapter.addData(it.dataList)
                noticeAdapter.loadMoreModule.loadMoreComplete()
            }
            if (it.dataList.size != 20) {
                noticeAdapter.loadMoreModule.loadMoreEnd()
            }
        }
    }
}