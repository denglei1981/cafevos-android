package com.changanford.home.adapter

import android.widget.TextView
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.changanford.home.R
import com.changanford.home.data.PublishData

class HomePublishAdapter(list: MutableList<PublishData>) : BaseQuickAdapter<PublishData, BaseViewHolder>(R.layout.item_home_publish,list) {


    override fun convert(holder: BaseViewHolder, item: PublishData) {
        var tvPublish = holder.getView<TextView>(R.id.tv_publish)
        tvPublish.text=item.msg
        tvPublish.setCompoundDrawablesRelativeWithIntrinsicBounds(null,null,null,null)
    }


}