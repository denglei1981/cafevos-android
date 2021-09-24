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

    private val adapter by lazy { PersonalAdapter(this) }

    override fun initView() {
        binding.ryPersonal.adapter = adapter
        binding.title.run {
            AppUtils.setStatusBarMarginTop(binding.title.root, this@PersonalActivity)
            tvTitle.text = "成员"
            ivBack.setOnClickListener { finish() }
        }
    }

    override fun initData() {
        val list = arrayListOf("", "", "", "", "", "", "", "", "", "", "", "")
        adapter.setItems(list)
        adapter.notifyDataSetChanged()
    }
}