package com.changanford.common.utilext

import android.content.Context
import android.graphics.drawable.Drawable
import android.widget.ImageView
import androidx.annotation.DrawableRes
import com.bumptech.glide.Glide
import com.bumptech.glide.RequestBuilder
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.bitmap.BitmapTransformation
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.Target
import com.changanford.common.R
import com.changanford.common.util.CircleGlideTransform
import com.changanford.common.util.MConstant
import com.changanford.common.util.RoundGlideTransform


/**********************************************************************************
 * @Copyright (C), 2020-2021.
 * @FileName: com.changanford.common.utilext.GlideUtils
 * @Author:　 　
 * @Version : V1.0
 * @Date: 2021/6/30 11:31
 * @Description: 　
 * *********************************************************************************
 */

fun ImageView.load(url: String?, drawable: Int? = R.mipmap.image_h_one_default) {
    var string: String? = url
    if (url?.startsWith("http") == false) {
        string = MConstant.imgcdn + url
    }

    val requestOptions: RequestOptions = RequestOptions()
        .centerCrop()
    if (string?.endsWith(".gif") == true) {
        Glide
            .with(this)
            .asGif()
            .diskCacheStrategy(DiskCacheStrategy.ALL)
            .thumbnail(0.25f)
            .load(string)
            .into(this)
    } else {
        Glide
            .with(this)
            .load(string).apply {
                drawable?.let { it ->
                    this.placeholder(it)
                }
            }
            .diskCacheStrategy(DiskCacheStrategy.ALL)
            .thumbnail(0.25f)
            .apply(requestOptions)
            .fitCenter()
            .into(this)
    }
}

fun ImageView.load(string: Int?, drawable: Int? = null) {
    val requestOptions: RequestOptions = RequestOptions()
        .centerCrop()
    Glide
        .with(this)
        .load(string).apply {
            drawable?.let { it ->
                this.placeholder(it)
            }
        }
        .diskCacheStrategy(DiskCacheStrategy.ALL)
        .thumbnail(0.25f)
        .apply(requestOptions)
        .fitCenter()
        .into(this)
}


object GlideUtils {
    /**
     * 图片地址没有前缀时加上
     */
    fun handleImgUrl(preUrl: String?): String =
        if (!preUrl.isNullOrEmpty() && preUrl.startsWith("http")) preUrl else MConstant.imgcdn.plus(
            preUrl
        )

    fun defaultHandleImageUrl(preUrl: String?): String =
        if (!preUrl.isNullOrEmpty() && preUrl.startsWith("http")) preUrl else MConstant.imgcdn.plus(
            preUrl
        )

    /**
     * 加载圆角
     */
    @JvmOverloads
    fun loadRoundLocal(
        url: String?,
        imageView: ImageView,
        round: Float,
        @DrawableRes errorDefaultRes: Int? = null
    ) {
        loadTransformLocal(
            url,
            RoundGlideTransform(round, round, round, round, isSquare = false),
            imageView,
            errorDefaultRes
        )
    }

    @JvmOverloads
    fun loadTransformLocal(
        url: String?,
        loadTransform: BitmapTransformation,
        imageView: ImageView,
        @DrawableRes errorDefaultRes: Int? = null
    ) {
        Glide.with(imageView.context).load(url).transform(loadTransform).apply {
            if (errorDefaultRes != null) {
                placeholder(errorDefaultRes)
                    .fallback(errorDefaultRes)
                    .error(errorDefaultRes)
                    .thumbnail(getTransform(imageView.context, errorDefaultRes, loadTransform))
            }
        }
            .into(imageView)
    }

    @JvmOverloads
    private fun getTransform(
        context: Context,
        @DrawableRes url: Int?,
        loadTransform: BitmapTransformation
    ): RequestBuilder<Drawable> {
        return Glide.with(context).load(url).transform(loadTransform)
    }

    /**
     * 普通加载
     */
    @JvmOverloads
    fun loadBD(
        url: String?,
        imageView: ImageView,
        @DrawableRes errorDefaultRes: Int = R.mipmap.image_h_one_default
    ) {
        Glide.with(imageView.context).load(defaultHandleImageUrl(url)).apply {
            if (errorDefaultRes != null) {
                placeholder(errorDefaultRes)
                    .fallback(errorDefaultRes)
                    .error(errorDefaultRes)
            }
            override(Target.SIZE_ORIGINAL,Target.SIZE_ORIGINAL)
        }.into(imageView)
    }


    /**
     * 普通加载
     */
    @JvmOverloads
    fun loadFilePath(
        url: String?,
        imageView: ImageView,
        @DrawableRes errorDefaultRes: Int = R.mipmap.ic_launcher
    ) {
        Glide.with(imageView.context).load(url).apply {
            if (errorDefaultRes != null) {
                placeholder(errorDefaultRes)
                    .fallback(errorDefaultRes)
                    .error(errorDefaultRes)
            }
        }.into(imageView)
    }

    fun loadCover(imageView: ImageView, url: String?, time: Long) {
        imageView.scaleType = ImageView.ScaleType.CENTER_CROP
        Glide.with(imageView.context)
            .setDefaultRequestOptions(
                RequestOptions()
                    .frame(time)
                    .centerCrop()
            )
            .load(url)
            .into(imageView)
    }

    /**
     * 加载圆形
     */
    @JvmOverloads
    fun loadCircle(url: String?, imageView: ImageView, @DrawableRes errorDefaultRes: Int? = null) {
        loadTransform(url, CircleGlideTransform(), imageView, errorDefaultRes)
    }

    @JvmOverloads
    fun loadTransform(
        url: String?,
        loadTransform: BitmapTransformation,
        imageView: ImageView,
        @DrawableRes errorDefaultRes: Int? = null
    ) {
        Glide.with(imageView.context).load(handleImgUrl(url)).transform(loadTransform).apply {
            if (errorDefaultRes != null) {
                placeholder(errorDefaultRes)
                    .fallback(errorDefaultRes)
                    .error(errorDefaultRes)
                    .thumbnail(getTransform(imageView.context, errorDefaultRes, loadTransform))
            }
        }
            .into(imageView)
    }

    fun loadCircleFilePath(filePath: String?, imageView: ImageView) {
        Glide.with(imageView.context).load(filePath).transform(CircleGlideTransform())
            .into(imageView)
    }

    /**
     * 加载圆角
     */
    @JvmOverloads
    fun loadRound(url: String?, imageView: ImageView, @DrawableRes errorDefaultRes: Int? = null) {
        loadTransform(
            handleImgUrl(url),
            RoundGlideTransform(isSquare = false),
            imageView,
            errorDefaultRes
        )
    }

    /**
     * 加载圆角
     */
    @JvmOverloads
    fun loadRoundFilePath(
        filePath: String?,
        imageView: ImageView,
        @DrawableRes errorDefaultRes: Int? = null
    ) {
        Glide.with(imageView.context).load(filePath)
            .transform(RoundGlideTransform(isSquare = false)).apply {
                if (errorDefaultRes != null) {
                    placeholder(errorDefaultRes)
                        .fallback(errorDefaultRes)
                        .error(errorDefaultRes)
                        .thumbnail(
                            getTransform(
                                imageView.context,
                                errorDefaultRes,
                                RoundGlideTransform(isSquare = false)
                            )
                        )
                }
            }
            .into(imageView)
    }

    fun loadGif(
        url: String?,
        imageView: ImageView,
        @DrawableRes errorDefaultRes: Int = R.drawable.image_ad_bg
    ) {
        Glide
            .with(imageView.context)
            .asGif()
            .error(errorDefaultRes)
            .load(defaultHandleImageUrl(url))
            .placeholder(errorDefaultRes)
            .fitCenter()
            .diskCacheStrategy(DiskCacheStrategy.DATA)
            .fallback(errorDefaultRes)
            .into(imageView)
//        GlideApp.with(imageView.context)
//            .asGif2()
//            .placeholder(errorDefaultRes)
//            .error(errorDefaultRes)
//            .load(defaultHandleImageUrl(url))
//            .diskCacheStrategy(DiskCacheStrategy.DATA)
//            .into(imageView)
    }

    @JvmOverloads
    fun loadBDCenter(
        url: String?,
        imageView: ImageView,
        @DrawableRes errorDefaultRes: Int = R.mipmap.image_h_one_default
    ) {
        Glide.with(imageView.context).load(defaultHandleImageUrl(url)).apply {
            if (errorDefaultRes != null) {
                placeholder(errorDefaultRes)
                    .fallback(errorDefaultRes)
                    .error(errorDefaultRes)
            }
        }.fitCenter().into(imageView)
    }
}