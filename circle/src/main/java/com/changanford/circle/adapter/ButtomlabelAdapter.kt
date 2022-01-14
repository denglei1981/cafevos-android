package com.changanford.circle.adapter

import android.widget.TextView
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.changanford.circle.R
import com.changanford.circle.bean.PostKeywordBean
import com.changanford.circle.utils.ShapeCreator
import com.changanford.common.MyApp


class ButtomlabelAdapter :BaseQuickAdapter<PostKeywordBean, BaseViewHolder>(R.layout.item_buttomlabel) {

    override fun convert(holder: BaseViewHolder, item: PostKeywordBean) {
        val text = holder.getView<TextView>(R.id.tv_content)
        text.text = item.tagName
        if (item.isselect){
            text.setTextColor( MyApp.mContext.resources.getColor(R.color.white))
            ShapeCreator.create()
                .setCornerRadius(12f)
                .setSolidColor(MyApp.mContext.resources.getColor(R.color.circle_00095))
                .into(text)
        }else{
            text.setTextColor( MyApp.mContext.resources.getColor(R.color.circle_8195C8))
            ShapeCreator.create()
                .setCornerRadius(12f)
                .setStrokeColor(MyApp.mContext.resources.getColor(R.color.circle_8195C8))
                .setStrokeWidth(1)
                .into(text)
        }
    }



}