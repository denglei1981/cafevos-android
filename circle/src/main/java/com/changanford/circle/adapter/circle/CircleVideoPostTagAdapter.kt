package com.changanford.circle.adapter.circle

import android.widget.TextView
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.changanford.circle.R
import com.changanford.circle.bean.PostKeywordBean
import com.changanford.circle.utils.ShapeCreator
import com.changanford.common.MyApp
import com.google.android.material.button.MaterialButton


class CircleVideoPostTagAdapter :
    BaseQuickAdapter<PostKeywordBean, BaseViewHolder>(R.layout.item_circle_video_post_details_tag) {

    override fun convert(holder: BaseViewHolder, item: PostKeywordBean) {
        val tags = holder.getView<MaterialButton>(R.id.btn_check)
        tags.text = item.tagName

    }


}