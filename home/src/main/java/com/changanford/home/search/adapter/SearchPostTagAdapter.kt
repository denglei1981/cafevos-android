package com.changanford.home.search.adapter

import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.changanford.common.bean.PostKeywordBean
import com.changanford.home.R
import com.google.android.material.button.MaterialButton

class SearchPostTagAdapter :
    BaseQuickAdapter<PostKeywordBean, BaseViewHolder>(R.layout.item_search_post_tag) {

    override fun convert(holder: BaseViewHolder, item: PostKeywordBean) {
        val tags = holder.getView<MaterialButton>(R.id.btn_check)
        tags.text = item.tagName
    }


}