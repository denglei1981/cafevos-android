package com.changanford.circle.ui.activity

import com.alibaba.android.arouter.facade.annotation.Route
import com.changanford.circle.adapter.CircleNoticeAdapter
import com.changanford.circle.databinding.ActivityCircleNoticeBinding
import com.changanford.circle.viewmodel.CircleNoticeViewMode
import com.changanford.common.basic.BaseActivity
import com.changanford.common.bean.TestBean
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

    override fun initView() {
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
    }

    override fun initData() {
        val list = arrayListOf(
            TestBean("公告详情最多4排，超过点击【展开】下拉展开；公告详情最多4排，超过点击【展开】下拉展开；"),
            TestBean("公告详情最多4排，超过点击【展开】下拉展开；公告详情最多4排，超过点击【展开】下拉展开；"),
            TestBean("公告详情最多4排，超过点击【展开】下拉展开；公告详情最多4排，超过点击【展开】下拉展开；"),
            TestBean("公告详情最多4排，超过点击【展开】下拉展开；公告详情最多4排，超过点击【展开】下拉展开；公告详情最多4排，超过点击【展开】下拉展开；公告详情最多4排，超过点击【展开】下拉展开；"),
            TestBean("公告详情最多4排，超过点击【展开】下拉展开；公告详情最多4排，超过点击【展开】下拉展开；公告详情最多4排，超过点击【展开】下拉展开；公告详情最多4排，超过点击【展开】下拉展开；"),
            TestBean("公告详情最多4排，超过点击【展开】下拉展开；公告详情最多4排，超过点击【展开】下拉展开；公告详情最多4排，超过点击【展开】下拉展开；公告详情最多4排，超过点击【展开】下拉展开；"),
            TestBean("公告详情最多4排，超过点击【展开】下拉展开；公告详情最多4排，超过点击【展开】下拉展开；公告详情最多4排，超过点击【展开】下拉展开；公告详情最多4排，超过点击【展开】下拉展开；"),
            TestBean("公告详情最多4排，超过点击【展开】下拉展开；公告详情最多4排，超过点击【展开】下拉展开；公告详情最多4排，超过点击【展开】下拉展开；公告详情最多4排，超过点击【展开】下拉展开；"),
        )
        noticeAdapter.setList(list)
    }
}