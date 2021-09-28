package com.changanford.home.acts.adapter

import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.changanford.home.R
import com.changanford.home.search.data.SearchData

class HomeActsScreenItemAdapter(list: MutableList<SearchData>) : BaseQuickAdapter<SearchData, BaseViewHolder>(R.layout.item_home_screen,list) {


    override fun convert(holder: BaseViewHolder, item: SearchData) {

    }


}