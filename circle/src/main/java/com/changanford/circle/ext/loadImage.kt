package com.changanford.circle.ext

import android.annotation.SuppressLint
import android.net.Uri
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.SimpleTarget
import com.bumptech.glide.request.transition.DrawableCrossFadeFactory
import com.bumptech.glide.request.transition.Transition
import com.changanford.common.utilext.GlideUtils
import com.google.android.material.imageview.ShapeableImageView
import com.google.android.material.shape.CornerFamily
import com.google.android.material.shape.ShapeAppearanceModel
import jp.wasabeef.glide.transformations.RoundedCornersTransformation
import java.io.File

fun ImageView.loadImage(url: String?, imageOptions: ImageOptions? = null) {
    Glide.with(context)
        .load(GlideUtils.handleImgUrl(url))
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
        .load(GlideUtils.handleImgUrl(url))
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

fun ShapeableImageView.setCircular(circular: Int) {
    this.shapeAppearanceModel =
        ShapeAppearanceModel.builder().setAllCorners(CornerFamily.ROUNDED, circular.toPx())
            .build()
}

class ImageOptions {
    var placeholder = 0         // 加载占位图
    var error = 0               // 错误占位图
    var fallback = 0            // null占位图
    var cornersRadius = 0       // 圆角半径
    var circleCrop = false      // 是否裁剪为圆形
}