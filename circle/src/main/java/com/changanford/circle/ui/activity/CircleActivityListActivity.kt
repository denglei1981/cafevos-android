package com.changanford.circle.ui.activity

import com.alibaba.android.arouter.facade.annotation.Route
import com.changanford.circle.adapter.circle.CircleActivityListAdapter
import com.changanford.circle.databinding.ActivityCircleActivityListBinding
import com.changanford.circle.viewmodel.CircleActivityListViewModel
import com.changanford.common.basic.BaseActivity
import com.changanford.common.bean.TestBean
import com.changanford.common.router.path.ARouterCirclePath
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
        CircleActivityListAdapter()
    }

    override fun initView() {
        binding.run {
            title.toolbar.initTitleBar(
                this@CircleActivityListActivity,
                Builder().apply { title = "圈内活动" })
            ryActivity.adapter = activityListAdapter
        }
    }

    override fun initData() {
        val list = arrayListOf(
            TestBean(""),
            TestBean(""),
            TestBean(""),
            TestBean(""),
            TestBean(""),
            TestBean(""),
            TestBean(""),
            TestBean(""),
        )
        activityListAdapter.setList(list)
    }
}