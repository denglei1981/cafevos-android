package com.changanford.common.util

import android.graphics.*
import com.bumptech.glide.load.Key
import com.bumptech.glide.load.engine.bitmap_recycle.BitmapPool
import com.bumptech.glide.load.resource.bitmap.BitmapTransformation
import java.security.MessageDigest
import kotlin.math.min


/**
 * @Author: hpb
 * @Date: 2020/5/6
 * @Des: Glide圆形
 */
class CircleGlideTransform : BitmapTransformation() {

    private val VERSION = 1
    private val ID = "com.changanford.common.util.CircleGlideTransform.$VERSION"
    private val ID_BYTES = ID.toByteArray(Key.CHARSET)

    override fun transform(
        pool: BitmapPool,
        toTransform: Bitmap,
        outWidth: Int,
        outHeight: Int
    ): Bitmap? {
        return circleCrop(pool, toTransform);
    }

    private fun circleCrop(pool: BitmapPool, source: Bitmap?): Bitmap? {
        if (source == null) return null
        val size = min(source.width, source.height)
        val x = (source.width - size) / 2
        val y = (source.height - size) / 2
        val squared = Bitmap.createBitmap(source, x, y, size, size)
        var result = pool[size, size, Bitmap.Config.ARGB_8888]
        if (result == null) {
            result = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888)
        }
        val canvas = Canvas(result)
        val paint = Paint()
        paint.shader = BitmapShader(
            squared,
            Shader.TileMode.CLAMP,
            Shader.TileMode.CLAMP
        )
        paint.isAntiAlias = true
        val r = size / 2f
        canvas.drawCircle(r, r, r, paint)
        return result
    }

    override fun equals(obj: Any?): Boolean {
        return obj is CircleGlideTransform
    }

    override fun hashCode(): Int {
        return ID.hashCode()
    }

    override fun updateDiskCacheKey(messageDigest: MessageDigest) {
        messageDigest.update(ID_BYTES);
    }

}