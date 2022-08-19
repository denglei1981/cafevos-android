package com.changanford.common.adapter

import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseDataBindingHolder
import com.changanford.common.R
import com.changanford.common.bean.CategoryOfPhoto
import com.changanford.common.constant.IntentKey
import com.changanford.common.databinding.ItemFordAlbumBinding
import com.changanford.common.router.path.ARouterCommonPath
import com.changanford.common.router.startARouter
import com.changanford.common.utilext.toIntPx

/**
 *Author lcw
 *Time on 2022/8/19
 *Purpose
 */
class FordAlbumAdapter :
    BaseQuickAdapter<CategoryOfPhoto, BaseDataBindingHolder<ItemFordAlbumBinding>>(
        R.layout.item_ford_album
    ) {
    override fun convert(
        holder: BaseDataBindingHolder<ItemFordAlbumBinding>,
        item: CategoryOfPhoto
    ) {
        holder.dataBinding?.run {
            setTopMargin(this.root, 12, holder.layoutPosition)
            tvTitle.text = item.categoryName

            val picAdapter = FordAlbumPicAdapter()
            ryPic.adapter = picAdapter
            picAdapter.setList(item.imgUrls)

            picAdapter.setOnItemClickListener { adapter, view, position ->
                val bundle = Bundle()
                bundle.putInt(IntentKey.FORD_ALBUM_POSITION, position)
                bundle.putParcelable(IntentKey.FORD_ALBUM_ITEM, item)
                startARouter( ARouterCommonPath.FordAlbumDetailsActivity,bundle)
            }
        }
    }

    /**
     * 列表第一个item追加margin
     */
    private fun setTopMargin(view: View, margin: Int, position: Int) {
        val params = view.layoutParams as ViewGroup.MarginLayoutParams
        if (position == 0) {
            params.topMargin =
                margin.toIntPx()
        } else params.topMargin = 0
    }
}