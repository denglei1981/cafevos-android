package com.changanford.circle.ui.activity

import android.annotation.SuppressLint
import android.view.View
import androidx.core.content.ContextCompat
import com.alibaba.android.arouter.facade.annotation.Route
import com.changanford.circle.R
import com.changanford.circle.adapter.CircleMemberManageAdapter
import com.changanford.circle.bean.CircleMemberBean
import com.changanford.circle.databinding.ActivityCircleMemberManageBinding
import com.changanford.circle.viewmodel.CircleMemberManageViewModel
import com.changanford.circle.widget.dialog.CircleMemberManageDialog
import com.changanford.common.basic.BaseActivity
import com.changanford.common.router.path.ARouterCirclePath
import com.changanford.common.util.AppUtils
import com.changanford.common.util.bus.LiveDataBus
import com.changanford.common.util.bus.LiveDataBusKey
import com.changanford.common.utilext.toast

/**
 *Author lcw
 *Time on 2021/10/14
 *Purpose 成员管理
 */
@Route(path = ARouterCirclePath.CircleMemberManageActivity)
class CircleMemberManageActivity :
    BaseActivity<ActivityCircleMemberManageBinding, CircleMemberManageViewModel>() {

    private var page = 1
    private var mCheckNum = 0
    var circleId: String = ""
    var isCircle: String = ""

    private var list: ArrayList<CircleMemberBean> = ArrayList()

    private val adapter by lazy {
        CircleMemberManageAdapter()
    }

    override fun initView() {
        circleId = intent.getStringExtra("circleId").toString()
        isCircle = intent.getStringExtra("isCircle").toString()

        binding.titleBar.run {
            AppUtils.setStatusBarMarginTop(binding.titleBar.root, this@CircleMemberManageActivity)
            tvTitle.text = "成员"
            tvRightMenu.text = "完成"
            tvRightMenu.setTextColor(ContextCompat.getColor(this@CircleMemberManageActivity,R.color.color_00095B))
            ivBack.setOnClickListener { finish() }
            tvRightMenu.setOnClickListener { finish() }
        }

        binding.run {
            recyclerView.adapter = adapter
            if (isCircle == "1") {
                tvSet.visibility = View.VISIBLE
            } else {
                tvSet.visibility = View.INVISIBLE
            }
        }
        initListener()
        bus()
    }

    private fun initListener() {
        adapter.loadMoreModule.setOnLoadMoreListener {
            page++
            viewModel.getData(circleId, page)
        }

        binding.refreshLayout.setOnRefreshListener {
            page = 1
            initData()
            it.finishRefresh()
        }

        binding.tvSet.setOnClickListener {
            if (list.size == 0) {
                return@setOnClickListener
            }
            CircleMemberManageDialog(
                this,
                this,
                mCheckNum,
                circleId,
                list,
                object : CircleMemberManageDialog.SureListener {
                    override fun sure(circleStarRoleId: String, list: ArrayList<CircleMemberBean>) {
                        val fixedSizeArr = arrayOfNulls<String>(list.size)
                        list.forEachIndexed { index, bean ->
                            fixedSizeArr[index] = bean.userId
                        }
                        viewModel.setStarsRole(circleId, circleStarRoleId, fixedSizeArr)
                    }

                }).show()
        }

        binding.tvDelete.setOnClickListener {
            if (list.size == 0) {
                return@setOnClickListener
            }
            val fixedSizeArr = arrayOfNulls<String>(list.size)
            list.forEachIndexed { index, bean ->
                fixedSizeArr[index] = bean.userId
            }
            viewModel.deleteCircleUsers(circleId, fixedSizeArr)

        }
    }

    override fun initData() {
        viewModel.getData(circleId, page)
    }

    @SuppressLint("SetTextI18n")
    override fun observe() {
        super.observe()
        viewModel.total.observe(this) {
            binding.titleBar.tvTitle.text = "成员($it)"
        }
        viewModel.personalBean.observe(this) {
            if (page == 1) {
                adapter.setList(it)
                if (it.size == 0) {
                    adapter.setEmptyView(R.layout.circle_empty_layout)
                }
            } else {
                adapter.addData(it)
                adapter.loadMoreModule.loadMoreComplete()
            }
            if (it.size != 20) {
                adapter.loadMoreModule.loadMoreEnd()
            }
        }
        viewModel.setStarsRoleBean.observe(this) {
            it.msg.toast()
            if (it.code == 0) {
                page = 1
                initData()
            }
        }
        viewModel.deletePersonalBean.observe(this) {
            it.msg.toast()
            if (it.code == 0) {
                page = 1
                initData()
                binding.tvCheckNum.text = "已选中0人"
            }
        }
    }

    @SuppressLint("SetTextI18n")
    private fun bus() {
        LiveDataBus.get().with(LiveDataBusKey.HOME_CIRCLE_MEMBER_MANAGE).observe(this) {
            var checkNum = 0
            list.clear()
            adapter.data.forEach { bean ->
                if (bean.isCheck) {
                    list.add(bean)
                    checkNum++
                }
            }
            mCheckNum = checkNum
            binding.tvCheckNum.text = "已选中${checkNum}人"
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        LiveDataBus.get().with(LiveDataBusKey.HOME_CIRCLE_MEMBER_MANAGE_FINISH).postValue("")
    }
}