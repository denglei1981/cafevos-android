package com.changanford.circle.utils

import android.widget.ImageView
import androidx.annotation.DrawableRes
import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView
import com.changanford.circle.R
import com.changanford.common.basic.adapter.BaseAdapterOneLayout
import com.changanford.common.util.ext.ImageOptions
import com.changanford.common.util.ext.loadBigImage
import com.changanford.common.util.ext.loadImage
import com.changanford.common.utilext.GlideUtils
import com.scwang.smart.refresh.layout.SmartRefreshLayout


/**
 * @Author: lcw
 * @Date: 2020/7/28
 * @Des:
 */

/**
 * 图片加载
 */
@BindingAdapter(value = ["bindingUrl", "circleCrop", "placeholder"], requireAll = false)
fun bindingUrl(
    imageView: ImageView,
    url: String,
    mCircleCrop: Boolean = false,
    @DrawableRes mPlaceHolder: Int
) {
    imageView.loadImage(
        GlideUtils.handleImgUrl(url),
        ImageOptions().apply {
            circleCrop = mCircleCrop
            error = R.mipmap.ic_def_square_img
            placeholder = mPlaceHolder
        })
//    imageView.loadCompress(url)
}

@BindingAdapter(value = ["bindingBigUrl", "circleCrop"], requireAll = false)
fun bindingBigUrl(imageView: ImageView, url: String, mCircleCrop: Boolean = false) {
    imageView.loadBigImage(
        url,
        ImageOptions().apply {
            circleCrop = mCircleCrop
//                error = R.mipmap.ic_launcher
//                placeholder = R.mipmap.ic_launcher
        })
}

@BindingAdapter(value = ["bindingLocal"])
fun bindingLocal(imageView: ImageView, url: Int) {
    imageView.loadImage(
        url,
        ImageOptions().apply {

//                error = R.mipmap.ic_launcher
//                placeholder = R.mipmap.ic_launcher
        })
}

/**
 * 滑动冲突
 */
@BindingAdapter("bindLoading")
fun bindingLoading(swipe: SmartRefreshLayout, isLoading: Boolean) {
    swipe.isEnabled = isLoading
}

/**
 * 列表滑动时停止加载图片
 */
@BindingAdapter("imageOptimization")
fun imageOptimization(recycler: RecyclerView, isStar: String) {
//        recycler.addOnScrollListener(object : RecyclerView.OnScrollListener() {
//            var IsScrolling = false
//
//            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
//                super.onScrollStateChanged(recyclerView, newState)
//                //recyclerView在滑动
//                if (newState == RecyclerView.SCROLL_STATE_DRAGGING || newState == RecyclerView.SCROLL_STATE_SETTLING) {
//                    IsScrolling = true
//                    Glide.with(recycler.context).pauseRequests()
//                } else if (newState == RecyclerView.SCROLL_STATE_IDLE) {
//                    if (IsScrolling) {
//                        Glide.with(recycler.context).resumeRequests()
//                    }
//                    IsScrolling = false
//                }
//            }
//        })
}

@BindingAdapter("bindBaseAdapterOneAdapter")
fun <T> bindOneAdapter(recyclerView: RecyclerView, adapter: BaseAdapterOneLayout<T>) {
    recyclerView.adapter = adapter
}

@BindingAdapter("bindBaseAdapterOneList")
fun <T> bindOneAdapterList(recyclerView: RecyclerView, data: ArrayList<T>?) {
    val adapter = recyclerView.adapter as? BaseAdapterOneLayout<T>
        ?: throw RuntimeException(" adapter is null")
    data?.let { adapter.setItems(data) }
}


