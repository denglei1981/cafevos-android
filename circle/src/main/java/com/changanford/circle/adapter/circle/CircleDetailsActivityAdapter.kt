package com.changanford.circle.adapter.circle

import android.annotation.SuppressLint
import android.graphics.Color
import android.graphics.ColorFilter
import android.graphics.LightingColorFilter
import android.graphics.drawable.Drawable
import android.graphics.drawable.GradientDrawable
import androidx.core.content.ContextCompat
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseDataBindingHolder
import com.changanford.circle.R
import com.changanford.circle.bean.WonderfulControlsBean
import com.changanford.circle.databinding.ItemCircleDetailsActivityBinding
import com.changanford.common.util.TimeUtils
import com.changanford.common.util.ext.setCircular
import com.changanford.common.utilext.GlideUtils.loadCompress


/**
 *Author lcw
 *Time on 2022/8/17
 *Purpose
 */
class CircleDetailsActivityAdapter :
    BaseQuickAdapter<WonderfulControlsBean, BaseDataBindingHolder<ItemCircleDetailsActivityBinding>>(
        R.layout.item_circle_details_activity
    ) {
    @SuppressLint("UseCompatLoadingForDrawables", "SetTextI18n")
    override fun convert(
        holder: BaseDataBindingHolder<ItemCircleDetailsActivityBinding>,
        item: WonderfulControlsBean
    ) {
        holder.dataBinding?.apply {
            ivBg.setCircular(12)
            ivBg.loadCompress(item.coverImg)
            tvContent.text = item.title
            tvTime.text="${TimeUtils.MillisTo_YMDHM2(item.beginTime)} - ${TimeUtils.MillisTo_YMDHM2(item.endTime)}"

            when (holder.layoutPosition) {
                0 -> {
                    tvActivityType.text = "未开始"
                    val sd = tvActivityType.background.mutate() as GradientDrawable
                    sd.setColor(ContextCompat.getColor(context, R.color.color_E67400))
                    sd.invalidateSelf()
                }

                1 -> {
                    tvActivityType.text = "进行中"
                    val sd = tvActivityType.background.mutate() as GradientDrawable
                    sd.setColor(ContextCompat.getColor(context, R.color.color_009987))
                    sd.invalidateSelf()
                }

                2 -> {
                    tvActivityType.text = "已结束"
                    val sd = tvActivityType.background.mutate() as GradientDrawable
                    sd.setColor(ContextCompat.getColor(context, R.color.color_00_a50))
                    sd.invalidateSelf()
                }
            }
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