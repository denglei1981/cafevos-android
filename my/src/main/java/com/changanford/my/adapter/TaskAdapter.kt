package com.changanford.my.adapter

import android.graphics.Color
import android.view.View
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseDataBindingHolder
import com.changanford.common.bean.GrowUpItem
import com.changanford.common.bean.ItemTaskBean
import com.changanford.common.bean.RootTaskBean
import com.changanford.common.util.JumpUtils
import com.changanford.common.util.TimeUtils
import com.changanford.common.util.bus.LiveDataBus
import com.changanford.my.R
import com.changanford.my.databinding.ItemGrowUpBinding
import com.changanford.my.databinding.ItemTaskContentBinding
import com.changanford.my.databinding.ItemTaskTitleBinding

/**
 *  文件名：TaskAdapter
 *  创建者: zcy
 *  创建日期：2021/9/17 14:20
 *  描述: TODO
 *  修改描述：TODO
 */

class TaskTitleAdapter() :
    BaseQuickAdapter<RootTaskBean, BaseDataBindingHolder<ItemTaskTitleBinding>>(
        R.layout.item_task_title
    ) {
    override fun convert(holder: BaseDataBindingHolder<ItemTaskTitleBinding>, item: RootTaskBean) {
        holder.dataBinding?.let {
            it.itmTaskTitle.text = item.taskTypeName

            it.itemRcyTask.adapter = TaskContentAdapter().apply {
                addData(item.list)
            }
        }
    }
}

class TaskContentAdapter() :
    BaseQuickAdapter<ItemTaskBean, BaseDataBindingHolder<ItemTaskContentBinding>>(
        R.layout.item_task_content
    ) {
    override fun convert(
        holder: BaseDataBindingHolder<ItemTaskContentBinding>,
        item: ItemTaskBean
    ) {
        holder.dataBinding?.let {
            it.taskTitle.text = item.taskName
            it.taskTitleDes.text = item.taskBrief
            it.itemNum.text = "+${item.taskScore}"
            if (item.taskAbcCount > 0) {

            } else if (item.taskAllCount > 1) {
                it.progress.max = item.taskAllCount
                it.progress.progress = item.taskDoneCount
                it.taskTitleInfo.text = "${item.taskDoneCount}/${item.taskAllCount}"
            } else {
                it.progress.max = 1
                it.progress.progress = if (item.taskIsOpen == 1) 0 else 1
                it.taskTitleInfo.text = "${if (item.taskIsOpen == 1) 0 else 1}/1"
            }
            it.itemTaskDes.text = ("奖励: " + item.taskScore + "U币/次 | "
                    + item.taskGrowthValue + "成长值/次") //奖励: 50积分 | 200成长值

            if (item.taskIsDone == 1) {
                it.taskFinish.text = "已完成"
                it.taskFinish.isSelected = false
                it.taskFinish.setTextColor(Color.parseColor("#999999"))
            } else {
                it.taskFinish.text = "去完成"
                it.taskFinish.isSelected = true
                it.taskFinish.setOnClickListener(View.OnClickListener {
                    if (item.jumpDataType == 14) {
                        try {
                            LiveDataBus.get()
                                .with("SEND_POST", Boolean::class.java)
                                .postValue(true)
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    } else {
                        LiveDataBus.get()
                            .with("SEND_POST", Boolean::class.java)
                            .postValue(false)
                        JumpUtils.instans?.jump(item.jumpDataType, item.jumpDataValue)
                    }
                })
                it.taskFinish.setTextColor(Color.parseColor("#1B3B89"))
            }
        }
    }
}

/**
 * 成长值，积分
 */
fun GrowUpAndJifenViewHolder(
    holder: BaseDataBindingHolder<ItemGrowUpBinding>,
    item: GrowUpItem,
    isGrowUp: Boolean,
    source: String = ""
) {
    holder.dataBinding?.let {
        it.title.text = item.actionName
        it.date.text = "${TimeUtils.MillisToStr(item.createTime)}"
        it.from.text =
            if (item.source.isNullOrEmpty()) source else item.source

        if (isGrowUp) {
            if (item.growth < 0) {
                it.num.text = "${item.growth}"
                it.num.setTextColor(Color.parseColor("#1B3B89"))
            } else {
                it.num.text = "+${item.growth}"
                it.num.setTextColor(Color.parseColor("#333333"))
            }
        } else {
            if (item.integral < 0) {
                holder.dataBinding?.num?.text = "${item.integral}"
            } else {
                holder.dataBinding?.num?.text = "+${item.integral}"
            }
        }
    }
}

