package com.changanford.common.util.ext

import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.changanford.common.adapter.DealMuchImageAdapter
import com.changanford.common.bean.ImageInfo
import com.changanford.common.utilext.toPx
import com.google.android.material.imageview.ShapeableImageView
import com.google.android.material.shape.CornerFamily
import com.google.android.material.shape.ShapeAppearanceModel
import com.squareup.picasso.Picasso

/**
 *Author lcw
 *Time on 2023/2/1
 *Purpose
 */
fun RecyclerView.scrollStopLoadImage() {
    this.addOnScrollListener(object : RecyclerView.OnScrollListener() {
        var IsScrolling = false
        override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
            super.onScrollStateChanged(recyclerView, newState)
            //recyclerView在滑动
            if (newState == RecyclerView.SCROLL_STATE_DRAGGING || newState == RecyclerView.SCROLL_STATE_SETTLING) {
                IsScrolling = true
                Glide.with(this@scrollStopLoadImage.context).pauseRequests()
                Picasso.get().pauseTag(this@scrollStopLoadImage.context)
            } else if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                if (IsScrolling) {
                    Glide.with(this@scrollStopLoadImage.context).resumeRequests()
                    Picasso.get().resumeTag(this@scrollStopLoadImage.context)
                }
                IsScrolling = false
            }
        }
    })
}

fun ShapeableImageView.setCircular(circular: Int) {
    this.shapeAppearanceModel =
        ShapeAppearanceModel.builder().setAllCorners(CornerFamily.ROUNDED, circular.toPx())
            .build()
}

fun RecyclerView.dealMuchImage(list: ArrayList<ImageInfo>?) {
    list?.let {
        val mList = if (list.size > 4) {
            list.subList(0, 4)
        } else {
            list
        }
        when (list.size) {
            2 and 4 -> {
                this.layoutManager = GridLayoutManager(this.context, 2)
            }
            3 -> {
                this.layoutManager = GridLayoutManager(this.context, 3)
            }
            else -> {
                this.layoutManager = GridLayoutManager(this.context, 2)
            }
        }
        setItemViewCacheSize(4)
        this.adapter = DealMuchImageAdapter().apply { setList(mList) }
    }
}