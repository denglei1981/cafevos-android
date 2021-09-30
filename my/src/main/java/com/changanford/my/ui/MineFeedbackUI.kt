package com.changanford.my.ui

import androidx.recyclerview.widget.LinearLayoutManager
import com.alibaba.android.arouter.facade.annotation.Route
import com.changanford.common.router.path.ARouterMyPath
import com.changanford.my.BaseMineUI
import com.changanford.my.R
import com.changanford.my.adapter.MineCommAdapter
import com.changanford.my.databinding.RefreshLayoutWithTitleBinding
import com.changanford.my.viewmodel.SignViewModel
import com.scwang.smart.refresh.layout.SmartRefreshLayout

/**
 *  文件名：MineFeedbackUI
 *  创建者: zcy
 *  创建日期：2020/5/11 18:28
 *  描述: 常见问题
 *  修改描述：TODO
 */
@Route(path = ARouterMyPath.MineFeedbackUI)
class MineFeedbackUI : BaseMineUI<RefreshLayoutWithTitleBinding, SignViewModel>() {

    private var adapter = MineCommAdapter.FeedbackAdapter(R.layout.item_feedback_list)


    override fun initView() {

        binding.mineToolbar.toolbarTitle.text = "常见问题"
        binding.mineToolbar.toolbar.setNavigationOnClickListener {
            back()
        }

        binding.mineRefresh.refreshRv.layoutManager = LinearLayoutManager(this)
        binding.mineRefresh.refreshRv.adapter = adapter


//        binding.mineToolbar.toolbarSave.setTextColor(Color.parseColor("#FC883B"))
//        binding.mineToolbar.toolbarSave.text = "我要反馈"
//        binding.mineToolbar.toolbarSave.setOnClickListener(object : View.OnClickListener {
//            override fun onClick(v: View?) {
//                startActivity(Intent(v?.context, MineFeedbackListUI::class.java))
//            }
//        })
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
        viewModel._feedBackBean.observe(this,{
            completeRefresh(it.dataList,adapter)
        })
    }

}