package com.changanford.circle.ui.activity

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import androidx.core.content.ContextCompat
import com.alibaba.android.arouter.facade.annotation.Route
import com.changanford.circle.R
import com.changanford.circle.adapter.PersonalAdapter
import com.changanford.circle.databinding.ActivityPersonalBinding
import com.changanford.circle.viewmodel.PersonalViewModel
import com.changanford.circle.widget.dialog.QuitCircleDialog
import com.changanford.common.basic.BaseActivity
import com.changanford.common.router.path.ARouterCirclePath
import com.changanford.common.router.startARouter
import com.changanford.common.util.AppUtils
import com.changanford.common.util.bus.LiveDataBus
import com.changanford.common.util.bus.LiveDataBusKey
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
        adapter.isApply = isApply
        binding.ryPersonal.adapter = adapter
        binding.title.run {
            AppUtils.setStatusBarMarginTop(binding.title.root, this@PersonalActivity)
            tvTitle.text = "成员"
            ivBack.setOnClickListener { finish() }
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

        adapter.setOnItemChildClickListener { _, view, _ ->
            if (view.id == R.id.tv_out) {
                QuitCircleDialog(this) { viewModel.quitCircle(circleId) }.show()
            }
        }

        adapter.loadMoreModule.setOnLoadMoreListener {
            page++
            viewModel.getData(circleId, page)
        }
        bus()
    }

    override fun initData() {
        viewModel.getData(circleId, page)
    }

    @SuppressLint("SetTextI18n")
    override fun observe() {
        super.observe()
        viewModel.personalBean.observe(this) {
            binding.tvCount.text="共${it.total}位成员"
            if (page == 1) {
                adapter.setList(it.dataList)
            } else {
                adapter.addData(it.dataList)
                adapter.loadMoreModule.loadMoreComplete()
            }
            if (it.dataList.size != 20) {
                adapter.loadMoreModule.loadMoreEnd()
            }

            if (it.extend.isStarRole == "1") {
                binding.title.tvRightMenu.apply {
                    setTextColor(ContextCompat.getColor(this@PersonalActivity,R.color.color_00095B))
                    text = "选择"
                    setOnClickListener { _ ->
                        val bundle = Bundle()
                        bundle.putString("circleId", circleId)
                        bundle.putString("isCircle", it.extend.isCircler)
                        startARouter(ARouterCirclePath.CircleMemberManageActivity, bundle)
                    }
                }
            }
        }
        viewModel.quitCircleBean.observe(this) {
            it.msg.toast()
            if (it.code == 0) {
                binding.title.tvRightMenu.visibility = View.GONE
            }
        }
    }

    private fun bus() {
        LiveDataBus.get().with(LiveDataBusKey.HOME_CIRCLE_MEMBER_MANAGE_FINISH)
            .observe(this) {
                page = 1
                viewModel.getData(circleId, page)
            }
    }
}