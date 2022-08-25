package com.changanford.circle.ui.activity

import android.os.Bundle
import android.view.View
import com.alibaba.android.arouter.facade.annotation.Route
import com.changanford.circle.R
import com.changanford.circle.adapter.CircleNoticeAdapter
import com.changanford.circle.databinding.ActivityCircleNoticeBinding
import com.changanford.circle.viewmodel.CircleNoticeViewMode
import com.changanford.common.basic.BaseActivity
import com.changanford.common.constant.IntentKey
import com.changanford.common.router.path.ARouterCirclePath
import com.changanford.common.router.startARouter
import com.changanford.common.util.AppUtils
import com.changanford.common.util.toolbar.initTitleBar
import com.gyf.immersionbar.ImmersionBar

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
    private var noticeId = ""
    private var hasLookNotice = false

    override fun initView() {
        ImmersionBar.with(this).statusBarColor(R.color.white).keyboardEnable(false).init()
        circleId = intent.getStringExtra(IntentKey.CREATE_NOTICE_CIRCLE_ID).toString()
        noticeId = intent.getStringExtra(IntentKey.NOTICE_ID).toString()
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
                val bundle = Bundle()
                bundle.putString(IntentKey.CREATE_NOTICE_CIRCLE_ID, circleId)
                startARouter(ARouterCirclePath.MyCircleNoticeActivity, bundle)
            }
            refreshLayout.setOnRefreshListener {
                page = 1
                initData()
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
                binding.refreshLayout.finishRefresh()
                noticeAdapter.setList(it?.dataList)
            } else {
                it?.dataList?.let { it1 -> noticeAdapter.addData(it1) }
                noticeAdapter.loadMoreModule.loadMoreComplete()
            }
            if (it?.dataList?.size != 20) {
                noticeAdapter.loadMoreModule.loadMoreEnd()
            }
            if (noticeId.isNotEmpty()) {
                noticeAdapter.data.forEachIndexed { index, circleNoticeItem ->
                    if (circleNoticeItem.noticeId.toString() == noticeId) {
                        binding.ryNotice.scrollToPosition(index)
                        noticeId = ""
                    }
                }
            }
        }
    }
}