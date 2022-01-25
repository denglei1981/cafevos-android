package com.changanford.circle.adapter.question

import android.annotation.SuppressLint
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseDataBindingHolder
import com.changanford.circle.R
import com.changanford.circle.databinding.ItemQuestionBinding
import com.changanford.common.bean.QuestionInfoBean

class QuestionListAdapter: BaseQuickAdapter<QuestionInfoBean, BaseDataBindingHolder<ItemQuestionBinding>>(R.layout.item_question){
    @SuppressLint("SetTextI18n")
    override fun convert(holder: BaseDataBindingHolder<ItemQuestionBinding>, item: QuestionInfoBean) {
        holder.dataBinding?.apply {

        }
    }
}