package com.changanford.home.search.adapter

import android.view.View
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.module.LoadMoreModule
import com.chad.library.adapter.base.viewholder.BaseDataBindingHolder
import com.changanford.common.util.TimeUtils
import com.changanford.common.util.actTypeText
import com.changanford.common.utilext.GlideUtils
import com.changanford.home.R
import com.changanford.home.data.ActBean
import com.changanford.home.databinding.ItemHomeActsBinding

/**
 * 活动列表。
 */
class SearchActsResultAdapter :
    BaseQuickAdapter<ActBean, BaseDataBindingHolder<ItemHomeActsBinding>>(R.layout.item_home_acts) ,LoadMoreModule{
    override fun convert(holder: BaseDataBindingHolder<ItemHomeActsBinding>, item: ActBean) {
        holder.dataBinding?.let {
            GlideUtils.loadBD(item.coverImg, it.ivActs)
            it.tvTips.text = item.title

            it.tvHomeActAddress.text = "地点：".plus(item.getAddress())
            it.tvHomeActTimes.text = "活动截止时间:".plus(TimeUtils.formateActTime(item.endTime))
            if (item.deadLineTime <= item.serverTime) {
                it.btnState.text = "已截止"
            } else {
                it.btnState.text = "进行中"
            }
            it.tvTagTwo.actTypeText(item.wonderfulType)

            when (item.wonderfulType) {
                0 -> {
                    it.tvTagTwo.text = "线上活动"
                    it.tvHomeActTimes.text =
                        "活动截止时间:".plus(TimeUtils.formateActTime(item.deadLineTime))
                    it.tvHomeActAddress.visibility = View.GONE
                }
                1 -> {
                    it.tvTagTwo.text = "线下活动"
                    it.tvHomeActTimes.text =
                        "报名截止时间: ".plus(TimeUtils.MillisTo_M_H(item.deadLineTime))

                    it.tvHomeActAddress.text = "地点：".plus(item.getAddress())
                    it.tvHomeActAddress.visibility = View.VISIBLE
                }
                2 -> {
                    it.tvTagTwo.text = "调查问卷"
                    it.tvHomeActTimes.text = ("截止时间: " + TimeUtils.MillisTo_M_H(item.deadLineTime))
                    it.tvHomeActAddress.visibility = View.GONE
                }
                3 -> {
                    it.tvTagTwo.text = "福域活动"
                    it.tvHomeActTimes.text =
                        "报名截止时间: ".plus(TimeUtils.MillisTo_M_H(item.deadLineTime))
                    it.tvHomeActAddress.visibility = View.GONE
                }
            }
            when (item.official) {
                0 -> {
                    it.tvTagOne.text = context.getString(R.string.platform_acts)
                    it.tvTagOne.visibility = View.VISIBLE
                }
                2 -> {
                    it.tvTagOne.text = "经销商"
                    it.tvTagOne.visibility = View.VISIBLE
                }
                else -> {
                    it.tvTagOne.visibility = View.VISIBLE
                    it.tvTagOne.text = "个人"
                }
            }

        }

    }
}