package com.changanford.circle.adapter

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.module.LoadMoreModule
import com.chad.library.adapter.base.viewholder.BaseDataBindingHolder
import com.changanford.circle.R
import com.changanford.circle.bean.CircleNoticeItem
import com.changanford.circle.databinding.ItemMyCircleNoticeBinding
import com.changanford.common.constant.IntentKey
import com.changanford.common.router.path.ARouterCirclePath
import com.changanford.common.router.startARouter
import com.changanford.common.util.MUtils
import com.changanford.common.util.ext.setDrawableColor

/**
 *Author lcw
 *Time on 2022/8/17
 *Purpose
 */
class MyCircleNoticeAdapter :
    BaseQuickAdapter<CircleNoticeItem, BaseDataBindingHolder<ItemMyCircleNoticeBinding>>(
        R.layout.item_my_circle_notice
    ), LoadMoreModule {
    @SuppressLint("SetTextI18n")
    override fun convert(
        holder: BaseDataBindingHolder<ItemMyCircleNoticeBinding>,
        item: CircleNoticeItem
    ) {
        holder.dataBinding?.run {
            MUtils.setTopMargin(this.root, 19, holder.layoutPosition)
            tvContent.text = item.detailHtml
            tvTime.text = item.noticeTimeStr
            tvContent.post {
                MUtils.expandText(tvContent, item.detailHtml)
            }
            when (item.checkStatus) {
                "WAIT_APPROVE" -> {//审核中
                    tvType.text = "审核中"
                    tvType.setDrawableColor(R.color.color_E67400)
                }

                "PASS" -> {//通过
                    if (item.onShelve == "UNDER_SHELVE") {
                        tvType.text = "已下架"
                        tvType.setDrawableColor(R.color.color_80a6)
                    } else {
                        tvType.text = "通过"
                        tvType.setDrawableColor(R.color.color_009987)
                    }
                }

                "REJECT" -> {//未通过
                    tvType.text = "未通过"
                    tvType.setDrawableColor(R.color.color_cc3333)
                }
            }

            val starStr = " ".repeat(tvType.length() * 3)

            tvTitle.text = "$starStr\t\t\t${item.noticeName}"

            if (item.checkStatus == "REJECT") {
                llReason.visibility = View.VISIBLE
                tvReason.text = "原因: ${item.checkNoReason}"
                tvReEdit.setOnClickListener {
                    val bundle = Bundle()
                    bundle.putString(IntentKey.CREATE_NOTICE_CIRCLE_ID, item.circleId.toString())
                    bundle.putSerializable(IntentKey.REASON_NOTICE, item)
                    startARouter(ARouterCirclePath.CreateNoticeActivity, bundle)
                }
            } else {
                llReason.visibility = View.GONE
            }
        }
    }
}