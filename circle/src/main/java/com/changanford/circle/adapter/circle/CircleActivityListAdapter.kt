package com.changanford.circle.adapter.circle

import android.annotation.SuppressLint
import android.view.View
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.module.LoadMoreModule
import com.chad.library.adapter.base.viewholder.BaseDataBindingHolder
import com.changanford.circle.R
import com.changanford.circle.bean.CircleActivityItemBean
import com.changanford.circle.databinding.ItemCircleActivityListBinding
import com.changanford.circle.ext.loadColLImage
import com.changanford.circle.ext.setCircular
import com.changanford.circle.utils.MUtils
import java.text.SimpleDateFormat
import java.util.*

/**
 *Author lcw
 *Time on 2022/8/18
 *Purpose
 */
class CircleActivityListAdapter :
    BaseQuickAdapter<CircleActivityItemBean, BaseDataBindingHolder<ItemCircleActivityListBinding>>(
        R.layout.item_circle_activity_list
    ), LoadMoreModule {

    //,ActivityTagEnum.NOT_BEGIN(code=NOT_BEGIN, message=未开始),
    // ActivityTagEnum.ON_GOING(code=ON_GOING, message=进行中),
    // ActivityTagEnum.ENDED(code=ENDED, message=已结束),
    // ActivityTagEnum.CHECKING(code=CHECKING, message=审核中),
    // ActivityTagEnum.OFF_SHELF(code=OFF_SHELF, message=已下架),
    // ActivityTagEnum.NOT_PASS(code=NOT_PASS, message=未通过)

    @SuppressLint("SimpleDateFormat")
    private var sdf = SimpleDateFormat("yyyy-MM-dd HH:mm")

    @SuppressLint("SetTextI18n")
    override fun convert(
        holder: BaseDataBindingHolder<ItemCircleActivityListBinding>,
        item: CircleActivityItemBean
    ) {
        holder.dataBinding?.apply {
            MUtils.setTopMargin(this.root, 12, holder.layoutPosition)
            ivCover.setCircular(5)
            ivCover.loadColLImage(item.coverImg)

            when (item.activityTag) {
                "NOT_BEGIN" -> {
                    tvType.text = "未开始"
                }
                "ON_GOING" -> {
                    tvType.text = "进行中"
                }
                "ENDED" -> {
                    tvType.text = "已结束"
                }
                "CHECKING" -> {
                    tvType.text = "审核中"
                }
                "OFF_SHELF" -> {
                    tvType.text = "已下架"
                }
                "NOT_PASS" -> {
                    tvType.text = "未通过"
                }
                else -> {
                    tvType.visibility = View.INVISIBLE
                }
            }

            tvTitle.text = item.title
            val starTime = sdf.format(Date(item.beginTime))
            val endTime = sdf.format(Date(item.endTime))
            tvTime.text = "活动时间：${starTime} - $endTime"
            tvAddress.visibility =
                if (item.activityAddr.isNullOrEmpty()) View.GONE else View.VISIBLE
            tvAddress.text = item.activityAddr
            tvNumJoin.text = "${item.activityJoinCount}人参与>"


            //ActivityButtonEnum.SIGN_NOT_BEGIN(code=SIGN_NOT_BEGIN, dbCode=0, message=报名未开始),
            // ActivityButtonEnum.SIGN_NOW(code=SIGN_NOW, dbCode=1, message=立即报名),
            // ActivityButtonEnum.SIGN_FULL(code=SIGN_FULL, dbCode=2, message=报名已满),
            // ActivityButtonEnum.SIGNED(code=SIGNED, dbCode=3, message=已报名),
            // ActivityButtonEnum.SIGN_ENDED(code=SIGN_END, dbCode=4, message=报名结束),
            // ActivityButtonEnum.MUNUAL_END(code=MUNUAL_END, dbCode=5, message=结束),
            // ActivityButtonEnum.VIEW_RESULT(code=VIEW_RESULT, dbCode=6, message=查看结果)
            btnType.isVisible = true
            when (item.activityButton) {
                "SIGN_NOT_BEGIN" -> {
                    btnType.text = "报名未开始"
                }
                "SIGN_NOW" -> {
                    btnType.text = "立即报名"
                }
                "SIGN_FULL" -> {
                    btnType.text = "报名已满"
                }
                "SIGNED" -> {
                    btnType.text = "已报名"
                }
                "SIGN_END" -> {
                    btnType.text = "报名结束"
                }
                "MUNUAL_END" -> {
                    btnType.text = "结束"
                }
                "VIEW_RESULT" -> {
                    btnType.text = "查看结果"
                }
                else -> {
                    btnType.isVisible = false
                }
            }

            if (btnType.text == "立即报名"||btnType.text == "查看结果") {
                btnType.setBackgroundResource(R.drawable.bg_f2f4_14)
                btnType.setTextColor(ContextCompat.getColor(context, R.color.color_00095B))
            } else {
                btnType.setBackgroundResource(R.drawable.bg_dd_14)
                btnType.setTextColor(ContextCompat.getColor(context, R.color.white))
            }
        }
    }
}