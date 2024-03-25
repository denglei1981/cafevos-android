package com.changanford.circle.ui.ask.adapter

import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.databinding.DataBindingUtil
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.module.LoadMoreModule
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.changanford.circle.R
import com.changanford.circle.bean.TecnicianVo
import com.changanford.circle.databinding.ItemHotMechanicBinding
import com.changanford.common.util.MConstant
import com.changanford.common.util.request.followOrCancelFollow
import com.changanford.common.utilext.GlideUtils.loadCompress

/**
 *Author lcw
 *Time on 2021/9/22
 *Purpose 问答红人
 */
class HotMechanicAdapter :
    BaseQuickAdapter<TecnicianVo, BaseViewHolder>(R.layout.item_hot_mechanic), LoadMoreModule {


    override fun convert(holder: BaseViewHolder, item: TecnicianVo) {
        val binding = DataBindingUtil.bind<ItemHotMechanicBinding>(holder.itemView)
        binding?.let {
//            GlideUtils.loadBD(item.avater, it.ivHeader)
            it.ivHeader.loadCompress(item.avater)
            it.tvUserName.text = item.nickName
            it.tvUserZannum.text = item.huDongCount.toString()

            if (item.isFollow == 1) {
                it.tvUserFocus.text = "已关注"
                it.tvUserFocus.setBackgroundResource(R.drawable.bg_80a6_100)
                it.tvUserFocus.setTextColor(ContextCompat.getColor(context, R.color.color_4d16))
            } else {
                it.tvUserFocus.text = "关注"
                it.tvUserFocus.setBackgroundResource(R.drawable.shape_white_24dp)
                it.tvUserFocus.setTextColor(ContextCompat.getColor(context, R.color.color_1700F4))
            }

            it.tvUserFocus.isVisible = item.userId != MConstant.userId

            it.tvUserFocus.setOnClickListener {
                val type = if (item.isFollow == 1) 0 else 1
                followOrCancelFollow(context as AppCompatActivity, item.userId, type) {
                    item.isFollow = type
                    notifyItemChanged(holder.layoutPosition)
                }
            }

            when (holder.layoutPosition) {
                0 -> {
                    it.ivHot.visibility = View.VISIBLE
                    it.ivHot.setImageResource(R.mipmap.circle_hr_one)
                }

                1 -> {
                    it.ivHot.visibility = View.VISIBLE
                    it.ivHot.setImageResource(R.mipmap.circle_hr_two)
                }

                2 -> {
                    it.ivHot.visibility = View.VISIBLE
                    it.ivHot.setImageResource(R.mipmap.circle_hr_three)
                }

                else -> {
                    it.ivHot.visibility = View.INVISIBLE
                    it.ivHeader.strokeColor = null
                }
            }


        }
    }

}