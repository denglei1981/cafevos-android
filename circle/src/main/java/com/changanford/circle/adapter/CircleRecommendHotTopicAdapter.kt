package com.changanford.circle.adapter

import android.annotation.SuppressLint
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.view.View
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.changanford.circle.R
import com.changanford.circle.databinding.ItemCircleHotTopicBinding
import com.changanford.common.bean.Topic

import com.changanford.common.util.EmsUtil

class CircleRecommendHotTopicAdapter :
    BaseQuickAdapter<Topic, BaseViewHolder>(R.layout.item_circle_hot_topic) {

    @SuppressLint("SetTextI18n")
    override fun convert(holder: BaseViewHolder, item: Topic) {
        val binding = DataBindingUtil.bind<ItemCircleHotTopicBinding>(holder.itemView)
        binding?.let {
            binding.bean = item
            binding.label.text = EmsUtil.subStrByLen(item.name, 13)
            binding.ivIcon.setColorFilter(Color.parseColor("#1700f4"))
            val backGround: GradientDrawable= binding.lableKey.background as GradientDrawable
            if ("YES" == item.isNew) {
                binding.lableKey.visibility= View.VISIBLE
                binding.lableKey.text = "新"
//                binding.lableKey.background = (DrawableTintUtil.tintDrawable(drawableBg!!, R.color.color_66A1FA))

                backGround.setColor(ContextCompat.getColor(context,R.color.color_66A1FA))
            } else if (1 == item.isHot) {
                binding.lableKey.visibility= View.VISIBLE
                binding.lableKey.text = "热"
//                binding.lableKey.background = (DrawableTintUtil.tintDrawable(drawableBg!!, R.color.color_FB944F))
                backGround.setColor(ContextCompat.getColor(context,R.color.color_FB944F))
            }else{
                binding.lableKey.visibility= View.GONE
            }
        }
    }
}