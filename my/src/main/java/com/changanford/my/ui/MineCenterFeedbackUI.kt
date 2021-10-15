package com.changanford.my.ui

import androidx.recyclerview.widget.LinearLayoutManager
import com.alibaba.android.arouter.facade.annotation.Route
import com.changanford.common.router.path.ARouterMyPath
import com.changanford.common.util.JumpUtils
import com.changanford.common.util.MineUtils
import com.changanford.my.BaseMineUI
import com.changanford.my.R
import com.changanford.my.adapter.MineCommAdapter
import com.changanford.my.databinding.UiCenterFeedbackBinding
import com.changanford.my.viewmodel.SignViewModel
import com.scwang.smart.refresh.layout.SmartRefreshLayout

@Route(path = ARouterMyPath.MineCenterFeedbackUI)
class MineCenterFeedbackUI : BaseMineUI<UiCenterFeedbackBinding, SignViewModel>() {
    private var adapter = MineCommAdapter.FeedbackAdapter(R.layout.item_feedback_list)
    var holidayHotline: String = "951998"//服务热线
    var mobile: String = "951999"//节假日热线
    override fun initView() {
        binding.mineToolbar.toolbarTitle.text = "帮助与反馈"
        binding.mineToolbar.toolbar.setNavigationOnClickListener {
            back()
        }
        binding.kefu.setOnClickListener {
            MineUtils.callPhone(this, mobile)

        }
        binding.yijian.setOnClickListener {
            JumpUtils.instans?.jump(42)
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
        viewModel.getFeedbackQ()
        viewModel._feedBackBean.observe(this, {
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