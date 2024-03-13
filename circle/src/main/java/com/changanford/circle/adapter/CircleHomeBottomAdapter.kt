package com.changanford.circle.adapter

import android.view.View
import androidx.appcompat.widget.AppCompatTextView
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseDataBindingHolder
import com.changanford.circle.R
import com.changanford.circle.databinding.ItemHomeCircleBottomBinding
import com.changanford.circle.viewmodel.CircleDetailsViewModel
import com.changanford.common.bean.NewCircleBean
import com.changanford.common.buried.WBuriedUtil
import com.changanford.common.listener.OnPerformListener
import com.changanford.common.util.ext.setCircular
import com.changanford.common.util.gio.GIOUtils
import com.changanford.common.utilext.load
import com.changanford.common.utilext.toast

/**
 * @author: niubobo
 * @date: 2024/3/13
 * @description：
 */
class CircleHomeBottomAdapter :
    BaseQuickAdapter<NewCircleBean, BaseDataBindingHolder<ItemHomeCircleBottomBinding>>(
        R.layout.item_home_circle_bottom
    ) {

    private val viewModel by lazy { CircleDetailsViewModel() }
    override fun convert(
        holder: BaseDataBindingHolder<ItemHomeCircleBottomBinding>,
        item: NewCircleBean
    ) {
        holder.dataBinding?.run {
            ivCover.setCircular(12)
            ivCover.load(item.pic)
            tvName.text = item.name
            tvPersonalNum.text = "${item.userCount} 位车主在这里"
            isJoin(tvJoinType, item)
        }
    }

    private fun isJoin(btnJoin: AppCompatTextView, item: NewCircleBean) {
        btnJoin.apply {
            visibility = View.VISIBLE
            when (item.isJoin) {
                //未加入
                "TOJOIN" -> {
                    setText(R.string.str_join)
//                    btnJoin.setDrawableColor(R.color.color_1700F4)
//                    btnJoin.setTextColor(ContextCompat.getColor(context, R.color.white))
//                    setBackgroundResource(R.drawable.shadow_00095b_12dp)
                    isEnabled = true
                    setOnClickListener {
                        WBuriedUtil.clickCircleJoin(item.name)
                        val joinFun = {
                            //申请加入圈子
                            viewModel.joinCircle(item.circleId, object : OnPerformListener {
                                override fun onFinish(code: Int) {
                                    when (code) {
                                        1 -> {//状态更新为审核中
                                            item.isJoin = "PENDING"
                                            context.getString(R.string.str_appliedForMembership)
                                                .toast()
                                        }

                                        2 -> {//已加入
                                            item.isJoin = "JOINED"
                                            context.getString(R.string.str_successfullyJoined)
                                                .toast()
                                        }

                                        else -> {
                                            item.isJoin = "TOJOIN"
                                        }
                                    }
                                    isJoin(btnJoin, item)
                                }
                            })
                        }
                        viewModel.checkJoinFun(item.circleId, joinFun)
                        GIOUtils.joinCircleClick(
                            "全部圈子",
                            item.circleId,
                            item.name
                        )
                    }
                }
                //审核中
                "PENDING" -> {
                    isEnabled = false
                    setText(R.string.str_underReview)
//                    setBackgroundResource(R.drawable.shadow_dd_12dp)
//                    btnJoin.setDrawableColor(R.color.color_E67400)
//                    btnJoin.setTextColor(ContextCompat.getColor(context, R.color.white))
                }
                //已加入
                "JOINED" -> {
                    isEnabled = false
                    setText(R.string.str_hasJoined)
//                    setBackgroundResource(R.drawable.shadow_dd_12dp)
//                    btnJoin.setDrawableColor(R.color.color_80a6)
//                    btnJoin.setTextColor(ContextCompat.getColor(context, R.color.color_4d16))
                }

//                else -> visibility = View.GONE
            }
        }

    }
}