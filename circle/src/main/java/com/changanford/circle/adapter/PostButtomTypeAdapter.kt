package com.changanford.circle.adapter

import android.widget.TextView
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.changanford.circle.R
import com.changanford.circle.bean.PostButtomBean


class PostButtomTypeAdapter :BaseQuickAdapter<PostButtomBean,BaseViewHolder>(R.layout.postbuttom_item) {
    override fun convert(holder: BaseViewHolder, item: PostButtomBean) {
        holder.getView<TextView>(R.id.tvcontent).text = item.content
    }
}