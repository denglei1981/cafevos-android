package com.changanford.circle.widget.assninegridview

import android.content.Context
import android.widget.ImageView
import com.changanford.common.bean.ImageInfo
import com.changanford.common.util.ext.setCircular
import java.io.Serializable

/**
 * @author assionhonty
 * Created on 2018/9/19 8:39.
 * Email：assionhonty@126.com
 * Function:适配器
 */
open class AssNineGridViewAdapter(protected var context: Context, var imageInfo: List<ImageInfo>) :
    Serializable {
    /**
     * 如果要实现图片点击的逻辑，重写此方法即可
     *
     * @param context      上下文
     * @param angv         九宫格控件
     * @param index        当前点击图片的的索引
     * @param imageInfo    图片地址的数据集合
     */
    open fun onImageItemClick(
        context: Context?,
        angv: AssNineGridView?,
        index: Int,
        imageInfo: List<ImageInfo?>?
    ) {
    }

    /**
     * 生成ImageView容器的方式，默认使用AssNineGridImageViewWrapper类，即点击图片后，图片会有蒙板效果
     * 如果需要自定义图片展示效果，重写此方法即可
     *
     * @param context 上下文
     * @return 生成的 ImageView
     */
    fun generateImageView(context: Context?): ImageView {
        val imageView = AssNineGridViewWrapper(context)
        imageView.scaleType = ImageView.ScaleType.CENTER_CROP
        imageView.setImageResource(com.changanford.common.R.mipmap.image_h_one_default)
        imageView.setCircular(5)
        return imageView
    }

    fun setImageInfoList(imageInfo: List<ImageInfo>) {
        this.imageInfo = imageInfo
    }
}