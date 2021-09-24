package com.changanford.circle.ui.activity

import com.alibaba.android.arouter.facade.annotation.Route
import com.changanford.circle.adapter.HotTopicAdapter
import com.changanford.circle.databinding.ActivityHotTopicBinding
import com.changanford.circle.viewmodel.HotTopicViewModel
import com.changanford.common.basic.BaseActivity
import com.changanford.common.router.path.ARouterCirclePath
import com.changanford.common.util.AppUtils

/**
 *Author lcw
 *Time on 2021/9/23
 *Purpose 热门话题
 */
@Route(path = ARouterCirclePath.HotTopicActivity)
class HotTopicActivity : BaseActivity<ActivityHotTopicBinding, HotTopicViewModel>() {

    private val adapter by lazy {
        HotTopicAdapter(this)
    }

    override fun initView() {
        AppUtils.setStatusBarPaddingTop(binding.title.root, this)
        binding.title.run {
            tvTitle.text = "热门话题"
            ivBack.setOnClickListener { finish() }
        }
        binding.ryTopic.adapter = adapter
    }

    override fun initData() {
        val list = arrayListOf(
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            ""
        )
        adapter.setItems(list)
        adapter.notifyDataSetChanged()
    }
}