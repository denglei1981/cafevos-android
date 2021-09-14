package com.changanford.my.ui

import com.alibaba.android.arouter.facade.annotation.Route
import com.chad.library.adapter.base.BaseNodeAdapter
import com.chad.library.adapter.base.entity.node.BaseNode
import com.changanford.common.basic.EmptyViewModel
import com.changanford.common.router.path.ARouterMyPath
import com.changanford.my.BaseMineUI
import com.changanford.my.adapter.RootNodeProvider
import com.changanford.my.adapter.SecondNodeProvider
import com.changanford.my.bean.ItemTaskBean
import com.changanford.my.bean.RootTaskBean
import com.changanford.my.databinding.UiTaskBinding
import com.changanford.my.databinding.ViewTaskHead1Binding
import com.changanford.my.databinding.ViewTaskHead2Binding
import java.util.*

/**
 *  文件名：TaskListUI
 *  创建者: zcy
 *  创建日期：2021/9/13 11:14
 *  描述: TODO
 *  修改描述：TODO
 */
@Route(path = ARouterMyPath.MineTaskListUI)
class TaskListUI : BaseMineUI<UiTaskBinding, EmptyViewModel>() {

    val taskAdapter: TaskAdapter by lazy {
        TaskAdapter()
    }

    override fun initView() {

        taskAdapter.addData(getEntity())

        var headView = ViewTaskHead1Binding.inflate(layoutInflater, null, false)
        taskAdapter.addHeaderView(headView.root)

        var headViewQD = ViewTaskHead2Binding.inflate(layoutInflater, null, false)
        taskAdapter.addHeaderView(headViewQD.root)

        binding.taskRcy.rcyCommonView.adapter = taskAdapter
        binding.taskRcy.rcyCommonView.scheduleLayoutAnimation()

    }

    override fun initData() {

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

    private fun getEntity(): List<BaseNode> {
        val list: MutableList<BaseNode> = ArrayList()
        for (i in 1..3) {
            //Item Node
            val itemEntity1 = ItemTaskBean()
            val itemEntity2 = ItemTaskBean()
            val itemEntity3 = ItemTaskBean()
            val items: MutableList<BaseNode> = ArrayList()
            items.add(itemEntity1)
            items.add(itemEntity2)
            items.add(itemEntity3)
            // Root Node
            val entity = RootTaskBean(items, "我的任务 $i")
            if (i == 1) {
                // 第1号数据默认不展开
                entity.isExpanded = false
            }
            list.add(entity)
        }
        return list
    }
}