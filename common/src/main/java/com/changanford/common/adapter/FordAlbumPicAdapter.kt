package com.changanford.common.adapter

import android.view.View
import android.view.ViewGroup
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseDataBindingHolder
import com.changanford.common.R
import com.changanford.common.databinding.ItemFordAlbumPicBinding
import com.changanford.common.utilext.GlideUtils
import com.changanford.common.utilext.toIntPx
import com.luck.picture.lib.tools.ScreenUtils

/**
 *Author lcw
 *Time on 2022/8/19
 *Purpose
 */
class FordAlbumPicAdapter :
    BaseQuickAdapter<String, BaseDataBindingHolder<ItemFordAlbumPicBinding>>(R.layout.item_ford_album_pic) {

    private val imgWidth by lazy {
        (ScreenUtils.getScreenWidth(context)) / 4
    }

    override fun convert(holder: BaseDataBindingHolder<ItemFordAlbumPicBinding>, item: String) {
        holder.dataBinding?.run {
            setMargin(this.root, holder.layoutPosition+1)
            ivPic.layoutParams?.height = imgWidth
            GlideUtils.loadBD(item, ivPic)
        }
    }

    /**
     * 列表第一个item追加margin
     */
    private fun setMargin(view: View, position: Int) {
        val params = view.layoutParams as ViewGroup.MarginLayoutParams
        if (position % 4 != 0) {
            params.rightMargin =
                1.toIntPx()
        } else params.rightMargin = 0
    }
}