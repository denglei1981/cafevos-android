package com.changanford.my.adapter

import android.annotation.SuppressLint
import android.content.res.ColorStateList
import android.graphics.Paint
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.text.TextPaint
import android.view.View
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.core.view.doOnPreDraw
import anet.channel.util.Utils.context
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseDataBindingHolder
import com.changanford.common.bean.Topic
import com.changanford.common.constant.IntentKey
import com.changanford.common.router.path.ARouterCirclePath
import com.changanford.common.router.startARouter
import com.changanford.common.util.CountUtils
import com.changanford.common.util.MConstant
import com.changanford.common.utilext.GlideUtils
import com.changanford.common.utilext.toIntDp
import com.changanford.common.utilext.toIntPx
import com.changanford.evos.R
import com.changanford.evos.databinding.ItemMysJoinTopicMoreBinding



class MyJoinTopicMoreAdapter :
    BaseQuickAdapter<Topic, BaseDataBindingHolder<ItemMysJoinTopicMoreBinding>>(R.layout.item_mys_join_topic_more) {

    var isMyPost = false
    var userId = ""

    @SuppressLint("SetTextI18n")
    override fun convert(
        holder: BaseDataBindingHolder<ItemMysJoinTopicMoreBinding>,
        item: Topic
    ) {
        holder.dataBinding?.let { t ->
            GlideUtils.loadBD(item.pic, t.ivCircle)
            t.tvCircleTitle.text = item.name
            t.tvCircleDesc.text = item.description

            t.tvPostCount.text =
                CountUtils.formatNum(item.postsCount.toString(), false).toString().plus("\t帖子")
            t.tvPeople.text =
                CountUtils.formatNum(item.viewsCount.toString(), false).toString().plus("\t浏览量")

            if (isMyPost && userId == MConstant.userId) {
                t.btnType.visibility = View.VISIBLE
                val vState = t.btnType.background as GradientDrawable
                //CircleNoticeCheckStatusEnum.WAIT_APPROVE(code=WAIT_APPROVE, dbCode=0, message=审核中),
                // CircleNoticeCheckStatusEnum.PASS(code=PASS, dbCode=1, message=通过)
                // CircleNoticeCheckStatusEnum.REJECT(code=REJECT, dbCode=2, message=未通过)
                when (item.checkStatus) {
                    "WAIT_APPROVE" -> {
                        t.btnType.text = "审核中"
                        vState.setColor(
                            ContextCompat.getColor(
                                context,
                                R.color.color_ccFFFFFF
                            )
                        )
                        t.btnType.setTextColor(
                            ContextCompat.getColor(
                                context,
                                R.color.color_1700f4
                            )
                        )
                        t.rlReason.visibility = View.GONE
                    }
                    "PASS" -> {
                        if (item.isGrounding == 1) {
                            t.btnType.text = "已下架"

                            vState.setColor(
                                ContextCompat.getColor(
                                    context,
                                    R.color.color_DD
                                )
                            )

                            t.btnType.setTextColor(
                                ContextCompat.getColor(
                                    context,
                                    R.color.white
                                )
                            )
                        } else {
                            t.btnType.text = "通过"

                            vState.setColor(
                                ContextCompat.getColor(
                                    context,
                                    R.color.color_ccFFFFFF
                                )
                            )

                            t.btnType.setTextColor(
                                ContextCompat.getColor(
                                    context,
                                    R.color.color_1700f4
                                )
                            )
                            t.rlReason.visibility = View.GONE
                        }
                    }
                    "REJECT" -> {
                        t.btnType.text = "未通过"

                        vState.setColor(
                            ContextCompat.getColor(
                                context,
                                R.color.color_80F21C44
                            )
                        )

                        t.btnType.setTextColor(ContextCompat.getColor(context, R.color.white))
                        t.rlReason.visibility = View.VISIBLE
                        t.tvReason.text = "原因：${item.reason}"
                        t.tvReReason.setOnClickListener {
                            val bundle = Bundle()
                            bundle.putSerializable(IntentKey.POST_TOPIC_ITEM, item)
                            startARouter(ARouterCirclePath.CreateCircleTopicActivity, bundle)
                        }
                    }
                    else -> {
                        t.btnType.visibility = View.GONE
                        t.rlReason.visibility = View.GONE
                    }
                }
            } else {
                t.btnType.visibility = View.GONE
                t.rlReason.visibility = View.GONE
            }
        }
    }


}