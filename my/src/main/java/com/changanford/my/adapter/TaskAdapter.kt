package com.changanford.my.adapter

import android.graphics.Color
import android.view.View
import androidx.core.content.ContextCompat
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseDataBindingHolder
import com.changanford.common.bean.GrowUpItem
import com.changanford.common.bean.ItemTaskBean
import com.changanford.common.bean.RootTaskBean
import com.changanford.common.text.addImageTag
import com.changanford.common.text.addTextTag
import com.changanford.common.util.JumpUtils
import com.changanford.common.util.MUtils
import com.changanford.common.util.TimeUtils
import com.changanford.common.util.bus.LiveDataBus
import com.changanford.common.util.gio.GIOUtils
import com.changanford.common.util.gio.updateTaskList
import com.changanford.my.R
import com.changanford.my.databinding.ItemGrowUpBinding
import com.changanford.my.databinding.ItemTaskContentBinding
import com.changanford.my.databinding.ItemTaskTitleBinding
import com.core.util.dp
import com.core.util.sp

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

class TaskContentAdapter :
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
//            it.itemNum.text = "+${item.taskScore}"
            it.progress.visibility = if (item.taskAllCount == 0) View.INVISIBLE else View.VISIBLE
            if (item.taskAllCount > 1) {
                it.progress.max = item.taskAllCount
                it.progress.progress = item.taskDoneCount
                it.taskTitleInfo.text = "${item.taskDoneCount}/${item.taskAllCount}"
            } else {
                it.progress.max = 1
                it.progress.progress = if (item.taskIsDone == 1) 1 else 0
                it.taskTitleInfo.text =
                    if (item.taskAllCount == 0) "已完成${item.taskIsDone}次" else "${if (item.taskIsDone == 1) 1 else 0}/1"
            }
            it.itemTaskDes.text = ("奖励/次: ") //奖励: 50积分 | 200成长值

            it.itemTaskDes.apply {
                addImageTag {
                    imageHeight = 20.dp
                    imageWidth = 20.dp
                    imageResource = R.mipmap.question_fb
                    position = it.itemTaskDes.text.length
                    leftPadding = 4.dp
                    rightPadding = 0
                }
                addTextTag {
                    text = "+${item.taskScore}"
                    position = it.itemTaskDes.text.length - 1
                    textSize = 12.sp.toFloat()
                    leftPadding = 1.dp
                    backgroundColor = ContextCompat.getColor(context, R.color.white)
                    textColor = ContextCompat.getColor(context, R.color.color_16)
                }
                addTextTag {
                    text = "  成长值"
                    position = it.itemTaskDes.text.length - 2
                    textSize = 12.sp.toFloat()
                    rightPadding = 0
                    backgroundColor = ContextCompat.getColor(context, R.color.white)
                    textColor = ContextCompat.getColor(context, R.color.color_8016)
                }
                addTextTag {
                    text = "+${item.taskGrowthValue}"
                    position = it.itemTaskDes.text.length - 3
                    textSize = 12.sp.toFloat()
                    leftPadding = 1.dp
                    backgroundColor = ContextCompat.getColor(context, R.color.white)
                    textColor = ContextCompat.getColor(context, R.color.color_16)
                }
            }
            if (item.taskIsDone == 1) {
                it.taskFinish.text = "已完成"
                it.taskFinish.isSelected = false
                it.taskFinish.background =
                    ContextCompat.getDrawable(context, R.drawable.bg_80a6_100)
                it.taskFinish.setTextColor(ContextCompat.getColor(context, R.color.color_8016))
            } else {
                it.taskFinish.background =
                    ContextCompat.getDrawable(context, R.drawable.cm_follow_new_bg)
                it.taskFinish.setTextColor(
                    ContextCompat.getColor(
                        context,
                        R.color.circle_app_color
                    )
                )
                it.taskFinish.text = "去完成"
                it.taskFinish.isSelected = true
                it.taskFinish.setOnClickListener {
                    GIOUtils.taskCenterCtaClick(
                        "去完成",
                        item.taskScore.toString(),
                        item.taskName,
                        item.actionCode
                    )
                    when (item.jumpDataType) {
                        18 -> {
                            updateTaskList("绑定手机号页", "绑定手机号页")
                        }

                        17 -> {
                            updateTaskList("车主认证页", "车主认证页")
                        }

                        1 -> {
                            updateTaskList("无", "无")
                        }

                        34 -> {
                            updateTaskList("基本信息页", "基本信息页")
                        }

                        21 -> {
                            updateTaskList("设置页", "设置页")
                        }

                    }
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
                }
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
        MUtils.setTopMargin(it.root,10,holder.layoutPosition)
        it.title.text = item.actionName
        it.date.text = "${TimeUtils.MillisToStr(item.createTime)}"
        it.from.text =
            if (item.source.isNullOrEmpty()) source else item.source

        if (isGrowUp) {
            if (item.growth < 0) {
                it.num.text = "${item.growth}"
                it.num.setTextColor(Color.parseColor("#1700f4"))
            } else {
                it.num.text = "+${item.growth}"
                it.num.setTextColor(Color.parseColor("#1700f4"))
            }
        } else {
            if (item.integral < 0) {
                it.num.setTextColor(Color.parseColor("#1700f4"))
                holder.dataBinding?.num?.text = "${item.integral}"
            } else {
                holder.dataBinding?.num?.text = "+${item.integral}"
                it.num.setTextColor(Color.parseColor("#1700f4"))
            }
        }
    }
}

