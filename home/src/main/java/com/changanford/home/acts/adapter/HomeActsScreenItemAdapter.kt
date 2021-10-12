package com.changanford.home.acts.adapter

import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.changanford.home.R
import com.changanford.home.data.EnumBean
import com.changanford.home.search.data.SearchData
import com.google.android.material.button.MaterialButton

class HomeActsScreenItemAdapter(list: MutableList<EnumBean>) : BaseQuickAdapter<EnumBean, BaseViewHolder>(R.layout.item_home_screen,list) {


    override fun convert(holder: BaseViewHolder, item: EnumBean) {
        var  btnCheck : MaterialButton = holder.getView(R.id.btn_check)
        btnCheck.text=item.message

    }


}