package com.changanford.home.search.adapter

import androidx.appcompat.widget.AppCompatTextView
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.changanford.common.utilext.GlideUtils
import com.changanford.common.wutil.WCommonUtil
import com.changanford.home.bean.SearchShopBean
import com.changanford.home.R
import com.google.android.material.imageview.ShapeableImageView

class SearchShopResultAdapter :
    BaseQuickAdapter<SearchShopBean, BaseViewHolder>(R.layout.item_search_result_shop) {
    override fun convert(holder: BaseViewHolder, item: SearchShopBean) {

        val ivShopping = holder.getView<ShapeableImageView>(R.id.iv_shopping)
        GlideUtils.loadBD(item.spuImgs, ivShopping)
        holder.setText(R.id.tv_author_name, item.spuName)
        val tvSubTitle = holder.getView<AppCompatTextView>(R.id.tv_sub_title)

        tvSubTitle.text =WCommonUtil.getRMB(item.normalFb)


    }
}