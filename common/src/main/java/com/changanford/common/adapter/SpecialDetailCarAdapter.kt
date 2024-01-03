package com.changanford.common.adapter

import android.os.Build
import android.text.Html
import android.text.Html.FROM_HTML_OPTION_USE_CSS_COLORS
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.changanford.common.R
import com.changanford.common.bean.SpecialCarListBean
import com.changanford.common.databinding.ItemSpecialDetailCarBinding

/**
 *Author lcw
 *Time on 2023/12/15
 *Purpose
 */
class SpecialDetailCarAdapter :
    BaseQuickAdapter<SpecialCarListBean, BaseViewHolder>(R.layout.item_special_detail_car) {
    override fun convert(holder: BaseViewHolder, item: SpecialCarListBean) {
        val binding = DataBindingUtil.bind<ItemSpecialDetailCarBinding>(holder.itemView)
        binding?.let {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                binding.tvCarName.text =
                    Html.fromHtml(item.carModelName, FROM_HTML_OPTION_USE_CSS_COLORS)
            } else {
                binding.tvCarName.text = item.carModelName
            }
            if (!item.isCheck) {
                binding.tvCarName.background =
                    ContextCompat.getDrawable(context, R.drawable.bg_081700f4_100)
                binding.tvCarName.setTextColor(ContextCompat.getColor(context,R.color.color_1700f4))
            } else {
                binding.tvCarName.background =
                    ContextCompat.getDrawable(context, R.drawable.bg_081700f4_100_st_1a1700)
                binding.tvCarName.setTextColor(ContextCompat.getColor(context,R.color.white))
            }
        }
    }
}