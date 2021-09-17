package com.changanford.common.utilext

import android.widget.ImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.changanford.common.util.MConstant

/**********************************************************************************
 * @Copyright (C), 2020-2021.
 * @FileName: com.changanford.common.utilext.GlideUtils
 * @Author:　 　
 * @Version : V1.0
 * @Date: 2021/6/30 11:31
 * @Description: 　
 * *********************************************************************************
 */

fun ImageView.load(url: String?, drawable: Int? = null) {
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
}