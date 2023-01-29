package com.changanford.common.adapter

import android.view.View
import androidx.core.view.isVisible
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.module.LoadMoreModule
import com.chad.library.adapter.base.viewholder.BaseDataBindingHolder
import com.changanford.common.R
import com.changanford.common.basic.BaseApplication
import com.changanford.common.bean.AcBean
import com.changanford.common.bean.ActBean
import com.changanford.common.databinding.ItemHomeActsBinding
import com.changanford.common.ui.dialog.AlertThreeFilletDialog
import com.changanford.common.util.JumpUtils
import com.changanford.common.util.actTypeText
import com.changanford.common.utilext.GlideUtils
import com.changanford.common.utilext.GlideUtils.loadCompress

/**
 * 活动列表。
 */
class SearchActsResultAdapter :
    BaseQuickAdapter<ActBean, BaseDataBindingHolder<ItemHomeActsBinding>>(R.layout.item_home_acts),
    LoadMoreModule {

    init {
        loadMoreModule.preLoadNumber = 20
    }

    var toFinish: (wonderfulId: Int) -> Unit = {}
    fun toFinishActivity(toFinish: (wonderfulId: Int) -> Unit) {
        this.toFinish = toFinish
    }

    var reEditCall: (bean: ActBean) -> Unit = {}
    fun reEdit(reEditCall: (bean: ActBean) -> Unit) {
        this.reEditCall = reEditCall
    }

    var logHistory: (Int) -> Unit = {}
    fun sSetLogHistory(logHistory: (Int) -> Unit = {}) {
        this.logHistory = logHistory
    }

    override fun convert(holder: BaseDataBindingHolder<ItemHomeActsBinding>, item: ActBean) {
        holder.dataBinding?.let {
            it.ivActs.loadCompress(item.coverImg)
            it.root.setOnClickListener {
                JumpUtils.instans?.jump(item.jumpDto.jumpCode, item.jumpDto.jumpVal)
                if (item.outChain == "YES") {
                    logHistory(item.wonderfulId)
                }
            }
            it.tvTips.text = item.title

            it.tvHomeActTimes.text = item.getActTimeS()
/*
            try {
                when {
                    System.currentTimeMillis()<item.deadLineTime ->{
                        it.btnState.text = "进行中"
                    }
                    else -> {
                        it.btnState.text = "已结束"
                    }
//                    item.serverTime < item.beginTime -> {
//                        it.btnState.text = "未开始"
//                    }
//                    item.deadLineTime <= item.serverTime -> {
//                        it.btnState.text = "已截止"
//                    }
//                    else -> {
//                        it.btnState.text = "进行中"
//                    }
                }
                if (item.wonderfulType != 2) {// 不是问卷活动
                    if (item.jumpType == 3) { // 是常规活动 及报名活动
                        if (item.needSignUp == "NO") { // 是否显示截止时间。
                            it.tvHomeSignUpTime.visibility = View.GONE
                        } else {
                            it.tvHomeSignUpTime.visibility = View.VISIBLE
                        }
                        it.tvHomeSignUpTime.text = item.getSignTimes()
                    } else {
                        it.tvHomeSignUpTime.visibility = View.GONE
                    }
                }

            } catch (e: Exception) {
                e.printStackTrace()
            }*/
            //new--

            it.btnState.isVisible = !item.activityTag.isNullOrEmpty()
            it.btnState.text = item.showTag()
            it.tvHomeActAddress.isVisible = !item.activityAddr.isNullOrEmpty()
            it.tvHomeActAddress.text = item.getAddress()
            it.tvSignpeople.isVisible = item.showJoinNum()
            it.tvSignpeopleImg.isVisible = item.showJoinNum()
            it.tvSignpeople.text = "${item.activityJoinCount}人参与"
            it.bt.isVisible = item.showButton()
            if (item.showButton()) {
                it.bt.text = item.showButtonText()
            }
            if (item.buttonBgEnable()) {
                it.bt.background =
                    BaseApplication.curActivity.resources.getDrawable(R.drawable.bg_f2f4f9_cor14)
                it.bt.setTextColor(BaseApplication.curActivity.resources.getColor(R.color.color_95b))
            } else {
                it.bt.background =
                    BaseApplication.curActivity.resources.getDrawable(R.drawable.bg_dd_cor14)
                it.bt.setTextColor(BaseApplication.curActivity.resources.getColor(R.color.white))
            }
            it.bt.setOnClickListener {
                if (item.isFinish()) {
                    AlertThreeFilletDialog(BaseApplication.curActivity).builder()
                        .setMsg(
                            "一旦结束将无法恢复，确定结束吗？"
                        )
                        .setCancelable(true)
                        .setPositiveButton("确定", R.color.color_01025C) {
                            toFinish(item.wonderfulId)
                        }
                        .setNegativeButton("取消", R.color.color_99) {

                        }.show()

                } else {
                    JumpUtils.instans?.jump(item.jumpDto.jumpCode, item.jumpDto.jumpVal)
                }
            }
            it.butongguo.isVisible = !item.reason.isNullOrEmpty()
            it.reason.text = "原因:${item.reason ?: ""}"
            it.reedit.setOnClickListener {
                reEditCall(item)
            }
            it.reedit.isVisible = item.showReedit()


//            it.tvTagTwo.actTypeText(item.wonderfulType)
//
//            when (item.wonderfulType) {
//                0 -> {
//                    it.tvTagTwo.text = "线上活动"
//                    it.tvHomeActAddress.visibility = View.GONE
//                }
//                1 -> {
//                    it.tvTagTwo.text = "线下活动"
//                    it.tvHomeActAddress.text = item.getAddress()
//                    it.tvHomeActAddress.visibility = View.VISIBLE
//                }
//                2 -> {
//                    it.tvTagTwo.text = "调查问卷"
//                    it.tvHomeActAddress.visibility = View.GONE
//                    it.tvHomeActTimes.text = item.getEndTimeTips()
//                }
//                3 -> {
//                    it.tvTagTwo.text = "福域活动"
//                    it.tvHomeActAddress.visibility = View.GONE
//                }
//            }
//            when (item.official) {
//                0 -> {
//                    it.tvTagOne.text = context.getString(R.string.platform_acts)
//                    it.tvTagOne.visibility = View.VISIBLE
//                }
//                2 -> {
//                    it.tvTagOne.text = "经销商"
//                    it.tvTagOne.visibility = View.VISIBLE
//                }
//                else -> {
//                    it.tvTagOne.visibility = View.VISIBLE
//                    it.tvTagOne.text = "个人"
//                }
//            }

        }

    }
}