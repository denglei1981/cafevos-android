package com.changanford.home.acts.adapter

import androidx.core.content.ContextCompat
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.changanford.home.R
import com.changanford.home.data.EnumBean
import com.google.android.material.button.MaterialButton

class HomeActsTypeItemAdapter(list: MutableList<EnumBean>) : BaseQuickAdapter<EnumBean, BaseViewHolder>(R.layout.item_home_screen,list) {

    init {
        addChildClickViewIds(R.id.btn_check)
    }
    var chooseType: String = ""


    fun setChooseTypes(chooseType: String) {
        this.chooseType = chooseType
        notifyDataSetChanged()
    }

    override fun convert(holder: BaseViewHolder, item: EnumBean) {
        var btnCheck: MaterialButton = holder.getView(R.id.btn_check)
        btnCheck.text = item.message
        if (chooseType == item.code.toString()) { // 选中
            btnCheck.setBackgroundColor(
                ContextCompat.getColor(
                    context,
                    R.color.blue_tab
                )
            )
            btnCheck.setTextColor(ContextCompat.getColor(context, R.color.white))
        } else {
            btnCheck.setBackgroundColor(
                ContextCompat.getColor(
                    context,
                    R.color.color_gray_f2f4f9
                )
            )
            btnCheck.setTextColor(ContextCompat.getColor(context, R.color.blue_tab))
        }

    }


}