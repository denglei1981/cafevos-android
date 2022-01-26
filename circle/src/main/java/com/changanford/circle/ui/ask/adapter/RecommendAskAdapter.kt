package com.changanford.circle.ui.ask.adapter

import android.view.View
import androidx.databinding.DataBindingUtil
import com.chad.library.adapter.base.BaseMultiItemQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.changanford.circle.R
import com.changanford.circle.bean.MultiBean
import com.changanford.circle.databinding.ItemRecommendAskAnswerPicBinding
import com.changanford.circle.databinding.ItemRecommendAskNoAnswerBinding

/**
 *Author lcw
 *Time on 2021/9/22
 *Purpose
 */
class RecommendAskAdapter : BaseMultiItemQuickAdapter<MultiBean, BaseViewHolder>() {


    init {
        addItemType(0, R.layout.item_recommend_ask_answer_pic)  //默认选择模块
        addItemType(1, R.layout.item_recommend_ask_answer_pic)
        addItemType(2, R.layout.item_recommend_ask_no_answer)
    }

    override fun convert(holder: BaseViewHolder, item: MultiBean) {

        when (item.itemType) {
            1 -> {
                hasAnswer(holder.itemView)
            }
            2 -> {
                // 没答案
                val binding = DataBindingUtil.bind<ItemRecommendAskNoAnswerBinding>(holder.itemView)
            }
            else -> {
                hasAnswer(holder.itemView)
            }
        }

    }

    fun hasAnswer(view: View) { // 有答案
        val binding = DataBindingUtil.bind<ItemRecommendAskAnswerPicBinding>(view)
        binding?.let { vb->{
            vb.layoutAnswerInfo.tvContent.text="回答的很好下次不要回答了"
        } }


    }


}