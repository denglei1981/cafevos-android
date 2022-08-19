package com.changanford.common.adapter

import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseDataBindingHolder
import com.changanford.common.R
import com.changanford.common.databinding.ItemFordAlbumDetailsBinding
import com.changanford.common.utilext.GlideUtils

/**
 *Author lcw
 *Time on 2022/8/19
 *Purpose
 */
class FordAlbumDetailsAdapter :
    BaseQuickAdapter<String, BaseDataBindingHolder<ItemFordAlbumDetailsBinding>>(
        R.layout.item_ford_album_details
    ) {
    override fun convert(holder: BaseDataBindingHolder<ItemFordAlbumDetailsBinding>, item: String) {
        holder.dataBinding?.run {
            GlideUtils.loadBD(item,ivPic)
        }
    }
}