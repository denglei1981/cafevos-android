package com.changanford.circle.ui.activity

import com.alibaba.android.arouter.facade.annotation.Route
import com.changanford.circle.adapter.MyCircleNoticeAdapter
import com.changanford.circle.databinding.ActivityMyCircleNoticeBinding
import com.changanford.circle.viewmodel.MyCircleNoticeViewModel
import com.changanford.common.basic.BaseActivity
import com.changanford.common.bean.TestBean
import com.changanford.common.router.path.ARouterCirclePath
import com.changanford.common.util.AppUtils

/**
 *Author lcw
 *Time on 2022/8/17
 *Purpose 圈子-我的公告
 */
@Route(path = ARouterCirclePath.MyCircleNoticeActivity)
class MyCircleNoticeActivity :
    BaseActivity<ActivityMyCircleNoticeBinding, MyCircleNoticeViewModel>() {

    private val noticeAdapter by lazy {
        MyCircleNoticeAdapter()
    }

    override fun initView() {
        AppUtils.setStatusBarMarginTop(binding.rlTitle, this)
        initMyListener()
        binding.ryMyNotice.adapter = noticeAdapter
    }

    private fun initMyListener() {
        binding.run {
            ivBack.setOnClickListener { finish() }
        }
    }

    override fun initData() {
        val list = arrayListOf(
            TestBean("公告详情最多4排，超过点击【展开】下拉展开；公告详情最多4排，超过点击【展开】下拉展开；"),
            TestBean("公告详情最多4排，超过点击【展开】下拉展开；公告详情最多4排，超过点击【展开】下拉展开；公告详情最多4排，超过点击【展开】下拉展开；公告详情最多4排，超过点击【展开】下拉展开"),
            TestBean("公告详情最多4排，超过点击【展开】下拉展开；公告详情最多4排，超过点击【展开】下拉展开；"),
            TestBean("公告详情最多4排，超过点击【展开】下拉展开；公告详情最多4排，超过点击【展开】下拉展开；"),
            TestBean("公告详情最多4排，超过点击【展开】下拉展开；公告详情最多4排，超过点击【展开】下拉展开；"),
            TestBean("公告详情最多4排，超过点击【展开】下拉展开；公告详情最多4排，超过点击【展开】下拉展开；"),
            TestBean("公告详情最多4排，超过点击【展开】下拉展开；公告详情最多4排，超过点击【展开】下拉展开公告详情最多4排，超过点击【展开】下拉展开；公告详情最多4排，超过点击【展开】下拉展开；"),
        )
        noticeAdapter.setList(list)
    }
}