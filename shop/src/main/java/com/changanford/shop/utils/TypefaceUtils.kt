package com.changanford.shop.utils

import android.content.Context
import android.graphics.Typeface

/**
 * @Author : wenke
 * @Time : 2021/9/27
 * @Description : TypefaceUtils
 */
object TypefaceUtils {
    private var typefaceTxt:Typeface?=null
    private var typefaceNumber:Typeface?=null
    fun getTypefaceTxt(context:Context):Typeface{
        if(null==typefaceTxt)typefaceTxt=Typeface.createFromAsset(context.assets, "MHeiPRC-Medium.OTF")
        return typefaceTxt!!
    }
    fun getTypefaceNumber(context:Context):Typeface{
        if(null==typefaceNumber)typefaceNumber=Typeface.createFromAsset(context.assets, "ford-antenna-light.woff.ttf")
        return typefaceNumber!!
    }
}