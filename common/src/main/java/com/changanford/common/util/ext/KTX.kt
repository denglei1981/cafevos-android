package com.changanford.common.util.ext

import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.SimpleItemAnimator
import com.bumptech.glide.Glide
import com.changanford.common.adapter.DealMuchImageAdapter
import com.changanford.common.bean.ImageInfo
import com.changanford.common.utilext.toPx
import com.google.android.material.imageview.ShapeableImageView
import com.google.android.material.shape.CornerFamily
import com.google.android.material.shape.ShapeAppearanceModel
import java.util.Objects

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
            } else if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                if (IsScrolling) {
                    Glide.with(this@scrollStopLoadImage.context).resumeRequests()
                }
                IsScrolling = false
            }
        }
    })
}

fun RecyclerView.noAnima() {
    // 解决调用 notifyItemChanged 闪烁问题,取消默认动画
    (Objects.requireNonNull<RecyclerView.ItemAnimator>(this.itemAnimator) as SimpleItemAnimator).supportsChangeAnimations =
        false
    this.itemAnimator = null
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
        setHasFixedSize(true)
        this.adapter = DealMuchImageAdapter().apply { setList(mList) }
    }
}