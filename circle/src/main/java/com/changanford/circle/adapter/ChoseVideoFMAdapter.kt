package com.changanford.circle.adapter

import android.graphics.Bitmap
import android.widget.ImageView
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.changanford.circle.R


class ChoseVideoFMAdapter() : BaseQuickAdapter<Bitmap, BaseViewHolder>(R.layout.sel_cover_item) {
    private lateinit var mBitmapList: ArrayList<Bitmap>
    fun addBitmapList(bitmapList: ArrayList<Bitmap>) {
        mBitmapList = bitmapList
        setList(mBitmapList)
    }

    override fun convert(holder: BaseViewHolder, item: Bitmap) {
        if (holder.layoutPosition < mBitmapList.size) {
            holder.getView<ImageView>(R.id.sel_cover_iv)
                .setImageBitmap(item)
        }
    }
}