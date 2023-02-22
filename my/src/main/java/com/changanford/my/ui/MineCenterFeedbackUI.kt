package com.changanford.my.ui

import androidx.recyclerview.widget.LinearLayoutManager
import com.alibaba.android.arouter.facade.annotation.Route
import com.changanford.common.bean.UserInfoBean
import com.changanford.common.net.onSuccess
import com.changanford.common.router.path.ARouterMyPath
import com.changanford.common.util.JumpUtils
import com.changanford.common.util.MineUtils
import com.changanford.common.util.gio.updateMainGio
import com.changanford.my.BaseMineUI
import com.changanford.my.R
import com.changanford.my.adapter.MineCommAdapter
import com.changanford.my.databinding.UiCenterFeedbackBinding
import com.changanford.my.viewmodel.SignViewModel
import com.google.gson.Gson
import com.scwang.smart.refresh.layout.SmartRefreshLayout

@Route(path = ARouterMyPath.MineCenterFeedbackUI)
class MineCenterFeedbackUI : BaseMineUI<UiCenterFeedbackBinding, SignViewModel>() {
    private var adapter = MineCommAdapter.FeedbackAdapter(R.layout.item_feedback_list)
    var mobile: String = "4008877766"//节假日热线
    override fun initView() {
        setLoadSir(binding.root)
        updateMainGio("帮助与反馈页", "帮助与反馈页")
        binding.mineToolbar.toolbarTitle.text = "帮助与反馈"
        binding.mineToolbar.toolbar.setNavigationOnClickListener {
            back()
        }
        binding.kefu.setOnClickListener {
            MineUtils.callPhone(this, mobile)
        }
        var hasFeedbacks: Int = 0
        viewModel.userDatabase.getUniUserInfoDao().getUser().observe(this) {
            it?.let {
                var userInfoBean: UserInfoBean =
                    Gson().fromJson(it.userJson, UserInfoBean::class.java)
                hasFeedbacks = userInfoBean?.hasFeedbacks!!
            }
        }
        binding.yijian.setOnClickListener {
            JumpUtils.instans?.jump(
                when (hasFeedbacks) {
                    1 -> {
                        42
                    }
                    else -> {
                        11
                    }
                }
            )
        }
        binding.more.setOnClickListener {
            JumpUtils.instans?.jump(39)
        }

        binding.mineRefresh.refreshRv.layoutManager = LinearLayoutManager(this)
        binding.mineRefresh.refreshRv.adapter = adapter
    }

    override fun bindSmartLayout(): SmartRefreshLayout? {
        return binding.mineRefresh.refreshLayout

    }

    override fun hasRefresh(): Boolean {
        return true
    }

    override fun initRefreshData(pageNo: Int) {
        super.initRefreshData(pageNo)
        viewModel.querySettingPhone {
            it.onSuccess {
                it?.mobile?.let {
                    this.mobile = it
                }
            }
        }
        viewModel.getFeedbackQ()
        viewModel._feedBackBean.observe(this, {
            showContent()
            if (!it.dataList.isNullOrEmpty()) {
                if (it.dataList?.size!! > 5) {
                    completeRefresh(it.dataList?.subList(0, 5), adapter)
                } else {
                    completeRefresh(it.dataList, adapter)
                }
            } else {
                completeRefresh(it.dataList, adapter)
            }
        })
    }

}