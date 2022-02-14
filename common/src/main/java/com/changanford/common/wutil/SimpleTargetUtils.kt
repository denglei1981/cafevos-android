package com.changanford.common.wutil

import android.content.Context
import android.graphics.drawable.Drawable
import android.view.View
import android.widget.ImageView
import com.bumptech.glide.request.target.SimpleTarget
import com.bumptech.glide.request.transition.Transition

/**
 * @Author : wenke
 * @Time : 2022/2/14 0014
 * @Description : SimpleTargetUtils
 */


/**
 * Author:wenke
 * Email:3158817509@qq.com
 * Create Time:2020/3/25
 * Update Time:
 * Note:
 */
class SimpleTargetUtils : SimpleTarget<Drawable?> {
    private var imageView: ImageView
    private var context: Context
    private var view: View? = null
    private var width = 0

    constructor(context: Context, imageView: ImageView) {
        this.imageView = imageView
        this.context = context
        width = ScreenUtils.getScreenWidth(context)
    }

    constructor(context: Context, imageView: ImageView, view: View?) {
        this.imageView = imageView
        this.context = context
        this.view = view
        //        width=view.getLayoutParams().width;
        width = ScreenUtils.getScreenWidth(context)
    }

    constructor(context: Context, imageView: ImageView, width: Int) {
        this.imageView = imageView
        this.context = context
        this.width = width
    }

    constructor(context: Context, imageView: ImageView, view: View?, width: Int) {
        this.imageView = imageView
        this.context = context
        this.view = view
        this.width = width
    }
    override fun onResourceReady(resource: Drawable, transition: Transition<in Drawable?>?) {
        val imageWidth = resource.intrinsicWidth
        val imageHeight = resource.intrinsicHeight
        val height =if(width!=0){
            //宽度固定,然后根据原始宽高比得到此固定宽度需要的高度
             width * imageHeight / imageWidth
        }else{//默认使用图片的原始宽高
            width=imageWidth
            imageHeight
        }
        val para = imageView.layoutParams
        if (para != null) {
            para.height = height
            para.width = width
        }
        imageView.setImageDrawable(resource)
        if (view != null) {
//            view.getLayoutParams().width=width;
            view!!.layoutParams.height = height
        }
    }
}