package com.changanford.my.adapter

import android.content.res.ColorStateList
import android.view.View
import androidx.core.content.ContextCompat
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseDataBindingHolder
import com.changanford.common.bean.Topic
import com.changanford.common.router.path.ARouterCirclePath
import com.changanford.common.router.startARouter
import com.changanford.common.util.CountUtils
import com.changanford.common.utilext.GlideUtils
import com.changanford.evos.R
import com.changanford.evos.databinding.ItemMysJoinTopicMoreBinding


class MyJoinTopicMoreAdapter :
    BaseQuickAdapter<Topic, BaseDataBindingHolder<ItemMysJoinTopicMoreBinding>>(R.layout.item_mys_join_topic_more) {
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
            t.tvPeople.text = CountUtils.formatNum(item.heat, false).toString().plus("\t热度")

            //临时测试代码
            if (holder.layoutPosition == 1) {
                //审核中、通过背景color_33FFFFFF，文字颜色#00095B
                //已下架 背景dddddd，文字颜色ffffff
                //未通过 背景color_80F21C44，文字颜色ffffff
                val colorStateList =
                    ColorStateList.valueOf(ContextCompat.getColor(context, R.color.color_33FFFFFF))
                t.btnType.backgroundTintList = colorStateList
                t.btnType.setTextColor(ContextCompat.getColor(context, R.color.white))
                t.rlReason.visibility = View.VISIBLE
            } else {
                val colorStateList =
                    ColorStateList.valueOf(ContextCompat.getColor(context, R.color.color_80F21C44))
                t.btnType.backgroundTintList = colorStateList
                t.btnType.setTextColor(ContextCompat.getColor(context, R.color.white))
                t.rlReason.visibility = View.GONE
            }
            t.tvReReason.setOnClickListener {
                //此处还需要传bundle
                startARouter(ARouterCirclePath.CreateCircleTopicActivity)
            }
        }
    }


}