package com.changanford.common.util.ext

import android.annotation.SuppressLint
import android.net.Uri
import android.widget.ImageView
import coil.load
import coil.transform.CircleCropTransformation
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.SimpleTarget
import com.bumptech.glide.request.transition.DrawableCrossFadeFactory
import com.bumptech.glide.request.transition.Transition
import com.changanford.common.R
import com.changanford.common.utilext.GlideUtils
import jp.wasabeef.glide.transformations.RoundedCornersTransformation
import java.io.File

fun ImageView.loadImage(url: String?, imageOptions: ImageOptions? = null) {
    Glide.with(context)
        .load(GlideUtils.dealMP4Url(url))
        .error(R.mipmap.ic_def_square_img)
        .placeholder(R.mipmap.ic_def_square_img)
        .apply(requestOptions(imageOptions))
        .transition(
            DrawableTransitionOptions.with(
                DrawableCrossFadeFactory
                    .Builder(300)
                    .setCrossFadeEnabled(true)
                    .build()
            )
        )
        .into(this)
}

fun ImageView.loadImageNoOther(url: String?, imageOptions: ImageOptions? = null) {
    Glide.with(context)
        .load(url)
        .error(R.mipmap.ic_def_square_img)
        .apply(requestOptions(imageOptions))
        .transition(
            DrawableTransitionOptions.with(
                DrawableCrossFadeFactory
                    .Builder(300)
                    .setCrossFadeEnabled(true)
                    .build()
            )
        )
        .into(this)
}

fun ImageView.loadImage(url: Int?, imageOptions: ImageOptions? = null) {
    Glide.with(context)
        .load(url)
        .error(R.mipmap.ic_def_square_img)
        .apply(requestOptions(imageOptions))
        .transition(
            DrawableTransitionOptions.with(
                DrawableCrossFadeFactory
                    .Builder(300)
                    .setCrossFadeEnabled(true)
                    .build()
            )
        )
        .into(this)
}

@SuppressLint("CheckResult")
fun ImageView.loadBigImage(url: String?, imageOptions: ImageOptions? = null) {
    Glide.with(context)
        .load(GlideUtils.dealMP4Url(url))
        .error(R.mipmap.ic_def_square_img)
        .apply(requestOptions(imageOptions))
        .transition(
            DrawableTransitionOptions.with(
                DrawableCrossFadeFactory
                    .Builder(300)
                    .setCrossFadeEnabled(true)
                    .build()
            )
        )
        .downloadOnly(object : SimpleTarget<File>() {
            override fun onResourceReady(resource: File, transition: Transition<in File>?) {
                val uri = Uri.fromFile(resource)
                this@loadBigImage.setImageURI(uri)
            }

        })
}

fun ImageView.loadCircleImage(url: String?) {
    this.load(GlideUtils.dealMP4Url(url)) {
        transformations(CircleCropTransformation())
        placeholder(R.mipmap.ic_def_square_img)
        error(R.mipmap.ic_def_square_img)
    }
}

fun ImageView.loadColLImage(url: String?) {
    this.load(GlideUtils.dealMP4Url(url)) {
        placeholder(R.mipmap.ic_def_square_img)
        error(R.mipmap.ic_def_square_img)
    }
}

private fun requestOptions(imageOptions: ImageOptions?) = RequestOptions().apply {
    imageOptions?.let {
        transform(RoundedCornersTransformation(it.cornersRadius, 0))
        placeholder(it.placeholder)
        error(it.error)
        fallback(it.fallback)
        if (it.circleCrop) {
            circleCrop()
        }
    }
}

class ImageOptions {
    var placeholder = R.mipmap.ic_def_square_img      // 加载占位图
    var error = R.mipmap.ic_def_square_img               // 错误占位图
    var fallback = R.mipmap.ic_def_square_img            // null占位图
    var cornersRadius = 0       // 圆角半径
    var circleCrop = false      // 是否裁剪为圆形
}