package com.changanford.my.ui

import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.alibaba.android.arouter.facade.annotation.Route
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseDataBindingHolder
import com.changanford.common.router.path.ARouterMyPath
import com.changanford.my.BaseMineUI
import com.changanford.my.R
import com.changanford.my.adapter.TaskTitleAdapter
import com.changanford.my.databinding.ItemSignDayBinding
import com.changanford.my.databinding.UiTaskBinding
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

    val taskAdapter: TaskTitleAdapter by lazy {
        TaskTitleAdapter()
    }

    override fun initView() {
//        StatusBarUtil.setTranslucentForImageView(this, null)

        binding.taskRcy.rcyCommonView.adapter = taskAdapter
        binding.taskRcy.rcyCommonView.scheduleLayoutAnimation()

        viewModel.taskBean.observe(this, Observer {
            completeRefresh(it, taskAdapter, 0)
        })

        binding.rcyDay.layoutManager = LinearLayoutManager(this).apply {
            orientation = RecyclerView.HORIZONTAL
        }
        binding.rcyDay.adapter = DayAdapter().apply {
            addData("")
            addData("")
            addData("")
            addData("")
            addData("")
            addData("")
            addData("")
        }
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

    inner class DayAdapter :
        BaseQuickAdapter<String, BaseDataBindingHolder<ItemSignDayBinding>>(R.layout.item_sign_day) {
        override fun convert(holder: BaseDataBindingHolder<ItemSignDayBinding>, item: String) {

            holder.dataBinding?.let {
                when (holder.layoutPosition) {
                    0, 1, 2 -> {
                        it.clLayout.isSelected = true
                        it.num.isSelected = true
                    }
                    else -> {
                        it.clLayout.isSelected = false
                        it.num.isSelected = false
                    }
                }
            }
        }
    }

    override fun hasRefresh(): Boolean {
        return false
    }

    override fun isUseFullScreenMode(): Boolean {
        return true
    }

//    inner class TaskAdapter : BaseNodeAdapter() {
//        init {
//            addFullSpanNodeProvider(RootNodeProvider())
//            addNodeProvider(SecondNodeProvider())
//        }
//
//        override fun getItemType(data: List<BaseNode>, position: Int): Int {
//            return when (data[position]) {
//                is RootTaskBean -> {
//                    0
//                }
//                is ItemTaskBean -> {
//                    1
//                }
//                else -> {
//                    -1
//                }
//            }
//        }
//    }
}