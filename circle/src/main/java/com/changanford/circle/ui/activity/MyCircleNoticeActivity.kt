package com.changanford.circle.ui.activity

import com.alibaba.android.arouter.facade.annotation.Route
import com.changanford.circle.R
import com.changanford.circle.adapter.MyCircleNoticeAdapter
import com.changanford.circle.databinding.ActivityMyCircleNoticeBinding
import com.changanford.circle.viewmodel.MyCircleNoticeViewModel
import com.changanford.common.basic.BaseActivity
import com.changanford.common.bean.TestBean
import com.changanford.common.constant.IntentKey
import com.changanford.common.router.path.ARouterCirclePath
import com.changanford.common.util.AppUtils
import com.changanford.common.util.MConstant
import com.gyf.immersionbar.ImmersionBar

/**
 *Author lcw
 *Time on 2022/8/17
 *Purpose 圈子-我的公告
 */
@Route(path = ARouterCirclePath.MyCircleNoticeActivity)
class MyCircleNoticeActivity :
    BaseActivity<ActivityMyCircleNoticeBinding, MyCircleNoticeViewModel>() {

    private var circleId = ""
    private var page = 1

    private val noticeAdapter by lazy {
        MyCircleNoticeAdapter()
    }

    override fun initView() {
        ImmersionBar.with(this).statusBarColor(R.color.white).keyboardEnable(false).init()
        circleId = intent.getStringExtra(IntentKey.CREATE_NOTICE_CIRCLE_ID).toString()
        AppUtils.setStatusBarMarginTop(binding.rlTitle, this)
        initMyListener()
        binding.ryMyNotice.adapter = noticeAdapter
    }

    private fun initMyListener() {
        binding.run {
            ivBack.setOnClickListener { finish() }
        }
        noticeAdapter.loadMoreModule.setOnLoadMoreListener {
            page++
            viewModel.circleMyNotices(page, circleId)
        }
    }

    override fun initData() {

    }

    override fun onResume() {
        super.onResume()
        if(MConstant.token.isNotEmpty()){
            page = 1
            viewModel.circleMyNotices(page, circleId)
        }
    }

    override fun observe() {
        super.observe()
        viewModel.noticeListBean.observe(this) {
            if (it.dataList.isNullOrEmpty()) {
                noticeAdapter.setEmptyView(R.layout.base_layout_empty)
                return@observe
            }
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