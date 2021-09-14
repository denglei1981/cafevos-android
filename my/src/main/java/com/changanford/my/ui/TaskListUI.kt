package com.changanford.my.ui

import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import com.alibaba.android.arouter.facade.annotation.Route
import com.chad.library.adapter.base.BaseNodeAdapter
import com.chad.library.adapter.base.entity.node.BaseNode
import com.changanford.common.router.path.ARouterMyPath
import com.changanford.my.BaseMineUI
import com.changanford.my.adapter.RootNodeProvider
import com.changanford.my.adapter.SecondNodeProvider
import com.changanford.my.bean.ItemTaskBean
import com.changanford.my.bean.RootTaskBean
import com.changanford.my.databinding.UiTaskBinding
import com.changanford.my.databinding.ViewTaskHead1Binding
import com.changanford.my.databinding.ViewTaskHead2Binding
import com.changanford.my.viewmodel.SignViewModel
import com.scwang.smart.refresh.layout.SmartRefreshLayout
import kotlinx.coroutines.launch

/**
 *  文件名：TaskListUI
 *  创建者: zcy
 *  创建日期：2021/9/13 11:14
 *  描述: TODO
 *  修改描述：TODO
 */
@Route(path = ARouterMyPath.MineTaskListUI)
class TaskListUI : BaseMineUI<UiTaskBinding, SignViewModel>() {

    val taskAdapter: TaskAdapter by lazy {
        TaskAdapter()
    }

    override fun initView() {

        var headView = ViewTaskHead1Binding.inflate(layoutInflater, null, false)
        taskAdapter.addHeaderView(headView.root)

        var headViewQD = ViewTaskHead2Binding.inflate(layoutInflater, null, false)
        taskAdapter.addHeaderView(headViewQD.root)

        binding.taskRcy.rcyCommonView.adapter = taskAdapter
        binding.taskRcy.rcyCommonView.scheduleLayoutAnimation()

        viewModel.taskBean.observe(this, Observer {
            completeRefresh(it, taskAdapter)
        })
    }

    override fun bindSmartLayout(): SmartRefreshLayout? {
        return binding.taskRcy.smartCommonLayout
    }

    override fun initRefreshData(pageSize: Int) {
        super.initRefreshData(pageSize)
        lifecycleScope.launch {
            task()
        }
    }

    suspend fun task() {
        viewModel.queryTasksList()
    }

    inner class TaskAdapter : BaseNodeAdapter() {
        init {
            addFullSpanNodeProvider(RootNodeProvider())
            addNodeProvider(SecondNodeProvider())
        }

        override fun getItemType(data: List<BaseNode>, position: Int): Int {
            return when (data[position]) {
                is RootTaskBean -> {
                    0
                }
                is ItemTaskBean -> {
                    1
                }
                else -> {
                    -1
                }
            }
        }
    }
}