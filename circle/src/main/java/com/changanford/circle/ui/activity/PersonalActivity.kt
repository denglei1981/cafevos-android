package com.changanford.circle.ui.activity

import android.view.View
import com.alibaba.android.arouter.facade.annotation.Route
import com.changanford.circle.adapter.PersonalAdapter
import com.changanford.circle.databinding.ActivityPersonalBinding
import com.changanford.circle.viewmodel.PersonalViewModel
import com.changanford.circle.widget.dialog.QuitCircleDialog
import com.changanford.common.basic.BaseActivity
import com.changanford.common.router.path.ARouterCirclePath
import com.changanford.common.router.path.ARouterMyPath
import com.changanford.common.router.startARouter
import com.changanford.common.util.AppUtils
import com.changanford.common.utilext.toast

/**
 *Author lcw
 *Time on 2021/9/22
 *Purpose 成员列表
 */
@Route(path = ARouterCirclePath.PersonalActivity)
class PersonalActivity : BaseActivity<ActivityPersonalBinding, PersonalViewModel>() {

    private var page = 1
    private var circleId = ""
    private var isApply = ""
    private val adapter by lazy { PersonalAdapter() }

    override fun initView() {
        circleId = intent.getStringExtra("circleId").toString()
        isApply = intent.getStringExtra("isApply").toString()
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
        initListener()
    }

    private fun initListener() {
        if (isApply == "2") {
            binding.title.tvRightMenu.text = "退出"
            binding.title.tvRightMenu.setOnClickListener {
                QuitCircleDialog(this) { viewModel.quitCircle(circleId) }.show()
            }
        }
    }

    override fun initData() {
        viewModel.getData(circleId, page)
    }

    override fun observe() {
        super.observe()
        viewModel.personalBean.observe(this, {
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
        viewModel.isStarRole.observe(this, {
            if (it == "1") {
                binding.title.tvRightMenu.text = "设置"
                binding.title.tvRightMenu.setOnClickListener {
                    startARouter(ARouterMyPath.CircleMemberUI)
                }
            }
        })
        viewModel.quitCircleBean.observe(this, {
            it.msg.toast()
            if (it.code == 0) {
                binding.title.tvRightMenu.visibility = View.GONE
            }
        })
    }
}