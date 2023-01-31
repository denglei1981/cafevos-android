package com.changanford.circle.utils

import android.content.Context
import com.changanford.circle.widget.assninegridview.AssNineGridView
import com.changanford.common.utilext.GlideUtils
import android.graphics.Bitmap
import android.widget.ImageView
import com.changanford.common.utilext.GlideUtils.loadCompress
import com.changanford.common.utilext.GlideUtils.loadCompress2

/**
 * @author assionhonty
 * Created on 2018/9/19 10:29.
 * Email：assionhonty@126.com
 * Function:
 */
class GlideImageLoader : AssNineGridView.ImageLoader {
    override fun onDisplayImage(context: Context, imageView: ImageView, url: String) {
//        Glide.with(context).load(url).into(imageView);
        imageView.loadCompress2(url)
    }

    override fun getCacheImage(url: String): Bitmap? {
        return null
    }
}