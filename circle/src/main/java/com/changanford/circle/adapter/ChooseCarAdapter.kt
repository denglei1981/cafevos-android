package com.changanford.circle.adapter

import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.changanford.circle.R
import com.changanford.circle.databinding.ItemChooseCarPostBinding
import com.changanford.common.bean.SpecialCarListBean
import com.changanford.common.utilext.GlideUtils.loadCompress2
import com.changanford.common.utilext.toIntPx

/**
 *Author lcw
 *Time on 2024/1/3
 *Purpose
 */
class ChooseCarAdapter :
    BaseQuickAdapter<SpecialCarListBean, BaseViewHolder>(R.layout.item_choose_car_post) {

    var checkPosition = -1

    override fun convert(holder: BaseViewHolder, item: SpecialCarListBean) {
        val binding: ItemChooseCarPostBinding = DataBindingUtil.bind(holder.itemView)!!
        binding.tvName.text = item.carModelName
        binding.ivIcon.loadCompress2(item.carModelPic)
        if (holder.layoutPosition == checkPosition) {
            binding.llContent.background =
                ContextCompat.getDrawable(context, R.drawable.bg_bord_1700f4_8)
        } else {
            binding.llContent.background =
                ContextCompat.getDrawable(context, R.drawable.bg_bord_0d16_8)
        }
        setTopMargin(holder.itemView, holder.layoutPosition)
    }

    private fun setTopMargin(view: View?, position: Int) {
        view?.let {
            val params = view.layoutParams as ViewGroup.MarginLayoutParams
            if (position == 0 || position == 1) {
                params.topMargin =
                    8.toIntPx()
            } else params.topMargin = 0
        }

    }
}