package com.changanford.common.utilext

import android.annotation.SuppressLint
import android.annotation.TargetApi
import android.app.Activity
import android.content.Context
import android.graphics.drawable.Drawable
import android.os.Build
import android.text.TextUtils
import android.util.Log
import android.widget.ImageView
import androidx.annotation.DrawableRes
import com.bumptech.glide.Glide
import com.bumptech.glide.RequestBuilder
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.load.resource.bitmap.BitmapTransformation
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.Target
import com.changanford.common.BuildConfig
import com.changanford.common.R
import com.changanford.common.util.CircleGlideTransform
import com.changanford.common.util.MConstant
import com.changanford.common.util.RoundGlideTransform
import com.changanford.common.utilext.GlideUtils.loadCompress2
import com.changanford.common.wutil.ScreenUtils
import com.changanford.common.wutil.SimpleTargetUtils


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
    if (BuildConfig.DEBUG) string?.logE()
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
        if (!preUrl.isNullOrEmpty() && preUrl.startsWith("http")) preUrl
        else if (!TextUtils.isEmpty(MConstant.imgcdn)) MConstant.imgcdn.plus(preUrl)
        else MConstant.defaultImgCdn.plus(preUrl)

    fun dealMP4Url(url: String?):String{
        return if(url?.endsWith(".mp4") == true){
            "${handleImgUrl(url)}?x-oss-process=video/snapshot,t_1000,f_jpg,w_1200,h_800,m_fast"
        }else{
            handleImgUrl(url).toString()
        }
    }

    fun handleNullableUrl(preUrl: String?): String? =
        if (preUrl.isNullOrEmpty()) null else {
            if (!preUrl.isNullOrEmpty() && preUrl.startsWith("http")) preUrl else MConstant.imgcdn.plus(
                preUrl
            )
        }

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
        if (url?.endsWith(".mp4") == true) {
            Glide.with(imageView)
                .load("${defaultHandleImageUrl(url)}?x-oss-process=video/snapshot,t_1000,f_jpg,w_1200,h_800,m_fast")
                .apply {
                    if (errorDefaultRes != null) {
                        placeholder(errorDefaultRes)
                        fallback(errorDefaultRes)
                        error(errorDefaultRes)
                    }
                }
                .into(imageView)
        } else {
            Glide.with(imageView.context).load(defaultHandleImageUrl(url)).apply {
                if (errorDefaultRes != null) {
                    placeholder(errorDefaultRes)
                        .fallback(errorDefaultRes)
                        .error(errorDefaultRes)
                }
            }.into(imageView)
        }
    }

    /**
     * 加载原始大小
     */
    @JvmOverloads
    fun loadFullSize(
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
            override(Target.SIZE_ORIGINAL, Target.SIZE_ORIGINAL)
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

    @SuppressLint("CheckResult")
    @JvmOverloads
    fun loadBigTransform(
        url: String?,
        loadTransform: BitmapTransformation,
        imageView: ImageView,
        @DrawableRes errorDefaultRes: Int? = null
    ) {
        if (url?.endsWith(".mp4") == true) {
            Log.e("mp4===", url)
            Glide.with(imageView)
                .load("$url?x-oss-process=video/snapshot,t_1000,f_jpg,w_1200,h_800,m_fast")
                .apply {
                    if (errorDefaultRes != null) {
                        placeholder(errorDefaultRes)
                        error(errorDefaultRes)
                    }
                }
                .into(imageView)
        } else {
            errorDefaultRes?.let {

                Glide.with(imageView).load(url?.let { it1 -> dealWithMuchImage(it1) })
                   .preload()

                Glide.with(imageView)
                    .load(url?.let { it1 -> dealWithMuchImage(it1) })
                    .placeholder(errorDefaultRes)
                    .error(errorDefaultRes)
//                    .override(400.toIntPx(), 400.toIntPx())
                    .into(imageView)
            }

        }
    }

    fun loadCircleFilePath(filePath: String?, imageView: ImageView) {
        Glide.with(imageView.context).load(filePath).transform(CircleGlideTransform())
            .into(imageView)
    }

    /**
     * 加载圆角
     */
    @JvmOverloads
    fun loadRound(
        url: String?,
        imageView: ImageView,
        @DrawableRes errorDefaultRes: Int = R.mipmap.image_h_one_default
    ) {
        loadTransform(
            handleImgUrl(url),
            RoundGlideTransform(isSquare = false),
            imageView,
            errorDefaultRes
        )
    }


    fun ImageView.loadCompress(
        url: String?,
        @DrawableRes errorDefaultRes: Int = R.mipmap.image_h_one_default
    ) {
        loadBigTransform(
            handleImgUrl(url),
            RoundGlideTransform(isSquare = false),
            this,
            errorDefaultRes
        )
    }

    fun ImageView.loadCompress2(
        url: String?,
        @DrawableRes errorDefaultRes: Int = R.mipmap.image_h_one_default
    ) {
        Glide.with(this).load(dealWithNineMuchImage(handleImgUrl(url)))
            .diskCacheStrategy(DiskCacheStrategy.RESOURCE).preload()

        Glide.with(this)
            .load(dealWithNineMuchImage(handleImgUrl(url)))
            .placeholder(errorDefaultRes)
            .fallback(errorDefaultRes)
            .error(errorDefaultRes)
            .override(350.toIntPx(), 350.toIntPx())
            .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
            .into(this)


        Log.e("compressImageview", dealWithNineMuchImage(handleImgUrl(url)))
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

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    fun glideLoadWidth(
        activity: Activity,
        url: String?,
        imageView: ImageView,
        width: Int,
        default_image: Int = R.mipmap.image_h_one_default
    ) {
        url?.apply {
            if (!activity.isDestroyed) {
                var imgUrl = this
                if (!imgUrl.startsWith("http")) {
                    imgUrl = MConstant.imgcdn + url
                }
                if (!imgUrl.contains(".gif")) {
                    val options =
                        RequestOptions.bitmapTransform(RoundedCorners(1)).placeholder(default_image)
                            .error(default_image).skipMemoryCache(false)
                    Glide.with(activity).load(imgUrl).apply(options)
                        .into(SimpleTargetUtils(activity, imageView, width))
                } else imageView.load(imgUrl, default_image)
            } else {
                Log.i("TAG", "Picture loading failed,activity is Destroyed")
            }
        }
    }

    fun dealWithMuchImage(
        oriPath: String
    ): String {
        if (oriPath.contains("?") || oriPath.contains(".gif") || oriPath.contains(".mp4")) {
            return oriPath
        }
        return if (oriPath.contains("androidios") && oriPath.contains("_")) {
            val s = oriPath.substringAfter("androidios").substringBefore(".")
            val array = s.split("_")
            if (array.size != 2) {
//                "$oriPath?x-oss-process=image/resize,p_80"
                "$oriPath?x-oss-process=image/resize,l_600"
            } else {
//                val screenWidth = ScreenUtils.getScreenWidth(imageView.context)
//                if (array[0].toInt() > screenWidth / 2) {
                "$oriPath?x-oss-process=image/resize,l_600"
//                } else {
//                    oriPath
//                }
            }
        } else {
            "$oriPath?x-oss-process=image/resize,l_600"
            //            return "$oriPath?x-oss-process=image/resize,w_${width},m_lfit"
        }

    }

    private fun dealWithNineMuchImage(
        oriPath: String
    ): String {
        if (oriPath.contains("?") || oriPath.contains(".gif") || oriPath.contains(".mp4")) {
            return oriPath
        }
        return if (oriPath.contains("androidios") && oriPath.contains("_")) {
            val s = oriPath.substringAfter("androidios").substringBefore(".")
            val array = s.split("_")
            if (array.size != 2) {
//                "$oriPath?x-oss-process=image/resize,p_80"
                "$oriPath?x-oss-process=image/resize,l_350"
            } else {
//                val screenWidth = ScreenUtils.getScreenWidth(imageView.context)
//                if (array[0].toInt() > screenWidth / 2) {
                "$oriPath?x-oss-process=image/resize,l_350"
//                } else {
//                    oriPath
//                }
            }
        } else {
            "$oriPath?x-oss-process=image/resize,l_350"
            //            return "$oriPath?x-oss-process=image/resize,w_${width},m_lfit"
        }

    }

    fun composeDealWithMuchImage(
        context: Context,
        width: Int,
        oriPath: String?
    ): String? {
        if (oriPath.isNullOrEmpty()) {
            return null
        }
        if (oriPath.contains("?") || oriPath.contains(".gif") || oriPath.contains(".mp4")) {
            return oriPath
        }
        return if (oriPath.contains("androidios") && oriPath.contains("_")) {
            val s = oriPath.substringAfter("androidios").substringBefore(".")
            val array = s.split("_")
            if (array.size != 2) {
//                "$oriPath?x-oss-process=image/resize,p_90"
                "$oriPath?x-oss-process=image/resize,l_550"
            } else {
                val screenWidth = ScreenUtils.getScreenWidth(context)
                if (array[0].toInt() > screenWidth * 2) {
                    "$oriPath?x-oss-process=image/resize,l_550"
                } else {
                    oriPath
                }
            }
        } else {
            oriPath
            //            return "$oriPath?x-oss-process=image/resize,w_${width},m_lfit"
        }

    }
}