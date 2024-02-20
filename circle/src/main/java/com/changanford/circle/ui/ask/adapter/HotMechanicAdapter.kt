package com.changanford.circle.ui.ask.adapter

import android.content.res.ColorStateList
import android.view.View
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.module.LoadMoreModule
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.changanford.circle.R
import com.changanford.circle.bean.CircleMemberBean
import com.changanford.circle.bean.TecnicianVo
import com.changanford.circle.databinding.ItemHotMechanicBinding
import com.changanford.common.utilext.GlideUtils
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


            when (item.anserRankNum) {
                1 -> {
                    it.ivHot.visibility = View.VISIBLE
                    it.ivHot.setImageResource(R.mipmap.circle_hr_one)
                    it.imgBg.setImageResource(R.mipmap.circle_hr_bg1)
                    it.ivHeader.strokeColor = ColorStateList.valueOf(
                        ContextCompat.getColor(
                            context,
                            R.color.color_yellow_F9D24B
                        )
                    )
                }
                2 -> {
                    it.ivHot.visibility = View.VISIBLE
                    it.ivHot.setImageResource(R.mipmap.circle_hr_two)
                    it.imgBg.setImageResource(R.mipmap.circle_hr_bg2)
                    it.ivHeader.strokeColor = ColorStateList.valueOf(
                        ContextCompat.getColor(
                            context,
                            R.color.color_yellow_F9D24B
                        )
                    )
                }
                3 -> {
                    it.ivHot.visibility = View.VISIBLE
                    it.ivHot.setImageResource(R.mipmap.circle_hr_three)
                    it.imgBg.setImageResource(R.mipmap.circle_hr_bg3)
                    it.ivHeader.strokeColor = ColorStateList.valueOf(
                        ContextCompat.getColor(
                            context,
                            R.color.color_yellow_F9D24B
                        )
                    )
                }
                else-> {
                    it.ivHot.visibility = View.INVISIBLE
                    it.ivHeader.strokeColor = null
                }
            }


        }
    }

}