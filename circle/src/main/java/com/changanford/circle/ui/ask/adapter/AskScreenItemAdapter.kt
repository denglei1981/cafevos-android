package com.changanford.circle.ui.ask.adapter

import androidx.core.content.ContextCompat
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.changanford.circle.R
import com.changanford.common.bean.QuestionData

import com.google.android.material.button.MaterialButton

class AskScreenItemAdapter(list: MutableList<QuestionData>) :
    BaseQuickAdapter<QuestionData, BaseViewHolder>(R.layout.item_dialog_ask_screen, list) {


    init {
        addChildClickViewIds(R.id.btn_check)
    }
    var chooseType: QuestionData?=null


    fun setChooseTypes(chooseType: QuestionData) {
        this.chooseType = chooseType
        notifyDataSetChanged()
    }
    override fun convert(holder: BaseViewHolder, item: QuestionData) {
        val btnCheck: MaterialButton = holder.getView(R.id.btn_check)
        btnCheck.text = item.dictLabel
        if (chooseType?.dictLabel == item.dictLabel) { // 选中
            btnCheck.setBackgroundColor(
                ContextCompat.getColor(
                    context,
                    R.color.color_gray_f2f4f9
                )
            )
            btnCheck.setTextColor(ContextCompat.getColor(context, R.color.circle_74889D))
        } else {
            btnCheck.setBackgroundColor(
                ContextCompat.getColor(
                    context,
                    R.color.white
                )
            )
            btnCheck.setTextColor(ContextCompat.getColor(context, R.color.text_colorv6))
        }

    }


}