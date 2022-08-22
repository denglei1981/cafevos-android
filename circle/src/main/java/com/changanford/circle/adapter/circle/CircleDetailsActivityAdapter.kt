package com.changanford.circle.adapter.circle

import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseDataBindingHolder
import com.changanford.circle.R
import com.changanford.circle.bean.WonderfulControlsBean
import com.changanford.circle.databinding.ItemCircleDetailsActivityBinding
import com.changanford.circle.ext.loadColLImage
import com.changanford.circle.ext.setCircular
import com.changanford.common.bean.TestBean
import com.changanford.common.constant.TestImageUrl

/**
 *Author lcw
 *Time on 2022/8/17
 *Purpose
 */
class CircleDetailsActivityAdapter :
    BaseQuickAdapter<WonderfulControlsBean, BaseDataBindingHolder<ItemCircleDetailsActivityBinding>>(
        R.layout.item_circle_details_activity
    ) {
    override fun convert(
        holder: BaseDataBindingHolder<ItemCircleDetailsActivityBinding>,
        item: WonderfulControlsBean
    ) {
        holder.dataBinding?.apply {
            ivBg.setCircular(5)
            ivBg.loadColLImage(item.coverImg)
            tvContent.text = item.title

            when (item.wonderfulType) {
                //ONLINE_ACTIVITY(0, "线上活动"),
                //OFFLINE_ACTIVITY(1, "线下活动"),
                //UNOFFICIAL(2, "问卷调研"),
                //CAR_FAC_ACTIVITY(3,"福域活动"),
                //VOTE(4,"投票活动"),
                //QUES(5,"问卷中心(0815)");
                "0" -> {
                    tvType.text = "线上活动"
                }
                "1" -> {
                    tvType.text = "线下活动"
                }
                "2" -> {
                    tvType.text = "问卷调研"
                }
                "3" -> {
                    tvType.text = "福域活动"
                }
                "4" -> {
                    tvType.text = "投票活动"
                }
                "5" -> {
                    tvType.text = "问卷中心"
                }
            }
        }
    }
}