package com.changanford.circle.adapter

import android.widget.ImageView
import android.widget.TextView
import com.chad.library.adapter.base.BaseMultiItemQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.changanford.circle.R
import com.changanford.circle.bean.ChooseCircleBean
import com.changanford.common.utilext.GlideUtils


class ChoseCircleAdapter() : BaseMultiItemQuickAdapter<ChooseCircleBean, BaseViewHolder>() {
    init {
        addItemType(2, R.layout.circle_chose_item)
        addItemType(1, R.layout.circle_chose_head)
    }

    override fun convert(holder: BaseViewHolder, item: ChooseCircleBean) {
        when {
            getItemViewType(getItemPosition(item)) == 2 -> {
                var imageView = holder.getView<ImageView>(R.id.avatar_img)
                var name = holder.getView<TextView>(R.id.name_tv)
                name.text = item.name
                item.url?.let {
                    GlideUtils.loadBD(it,imageView)
                }
            }
            else -> {
                var title = holder.getView<TextView>(R.id.title)
                title.text= item.title
            }
        }
    }

}