package com.changanford.common.util

import android.graphics.*
import android.util.Log
import com.bumptech.glide.load.Key
import com.bumptech.glide.load.engine.bitmap_recycle.BitmapPool
import com.bumptech.glide.load.resource.bitmap.BitmapTransformation
import com.bumptech.glide.load.resource.bitmap.TransformationUtils
import java.security.MessageDigest
import kotlin.math.min


/**
 * @Author: hpb
 * @Date: 2020/5/6
 * @Des: 圆角Glide
 * 默认圆角大小dp，截取中间，正方形
 */
class RoundGlideTransform constructor(
    private var roundLeftTop: Float = 5F,
    private var roundRightTop: Float = 5F,
    private var roundRightBottom: Float = 5F,
    private var roundLeftBottom: Float = 5F,
    private val isSquare: Boolean = true,
    private val isCenterCrop: Boolean = true
) : BitmapTransformation() {

    private var roundArray: FloatArray
    private val VERSION = 1
    private val ID = "com.hpb.mvvm.ccc.utils.RoundGlideTransform.$VERSION"
    private val ID_BYTES = ID.toByteArray(Key.CHARSET)

    init {
        roundLeftTop = DensityUtils.dip2px(roundLeftTop).toFloat()
        roundRightTop = DensityUtils.dip2px(roundRightTop).toFloat()
        roundRightBottom = DensityUtils.dip2px(roundRightBottom).toFloat()
        roundLeftBottom = DensityUtils.dip2px(roundLeftBottom).toFloat()
        roundArray = floatArrayOf(
            roundLeftTop, roundLeftTop,
            roundRightTop, roundRightTop,
            roundRightBottom, roundRightBottom,
            roundLeftBottom, roundLeftBottom
        )
    }

    override fun transform(
        pool: BitmapPool,
        toTransform: Bitmap,
        outWidth: Int,
        outHeight: Int
    ): Bitmap? {
        val bitmap: Bitmap = if (isCenterCrop) TransformationUtils.centerCrop(
            pool,
            toTransform,
            outWidth,
            outHeight
        ) //调用居中剪切显示centerCrop
        else toTransform
        return roundCrop(pool, bitmap);
    }

    private fun roundCrop(pool: BitmapPool, source: Bitmap?): Bitmap? {
        if (source == null) return null
        var width = source.width
        var height = source.height
        if (isSquare) { //正方形
            width = min(width, height)
            height = width
        }
        val x = (source.width - width) / 2
        val y = (source.height - height) / 2

        val squared = Bitmap.createBitmap(source, x, y, width, height)

        var result: Bitmap? = pool[width, height, Bitmap.Config.ARGB_8888]
        if (result == null) {
            result = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        }
        val canvas = Canvas(result!!)
        val paint = Paint()
        paint.shader = BitmapShader(
            squared,
            Shader.TileMode.CLAMP,
            Shader.TileMode.CLAMP
        )
        paint.isAntiAlias = true
        val rectF = RectF(0f, 0f, width.toFloat(), height.toFloat())
        val path = Path()
        path.addRoundRect(
            rectF,
            roundArray,
            Path.Direction.CW
        )
        canvas.drawPath(path, paint);
        return result
    }

    override fun equals(obj: Any?): Boolean {
        return obj is RoundGlideTransform
    }

    override fun hashCode(): Int {
        return ID.hashCode()
    }

    override fun updateDiskCacheKey(messageDigest: MessageDigest) {
        messageDigest.update(ID_BYTES);
    }

}