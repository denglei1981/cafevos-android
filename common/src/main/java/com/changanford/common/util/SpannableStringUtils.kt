package com.changanford.common.util

import android.graphics.Color
import android.graphics.Typeface
import android.text.Spannable
import android.text.SpannableString
import android.text.SpannableStringBuilder
import android.text.Spanned
import android.text.style.AbsoluteSizeSpan
import android.text.style.ClickableSpan
import android.text.style.ForegroundColorSpan
import android.text.style.StyleSpan
import androidx.annotation.ColorRes
import com.changanford.common.basic.BaseApplication
import java.util.regex.Pattern


/**
 * @Author: hpb
 * @Date: 2020/5/23
 * @Des: 自由拼接各种效果text
 */
object SpannableStringUtils {

    fun getSpannable(
        str: String,
        @ColorRes colorRes: Int,
        click: ClickableSpan? = null
    ): SpannableString {
        val sp = SpannableString(str)
        sp.setSpan(
            ForegroundColorSpan(BaseApplication.INSTANT.resources.getColor(colorRes)),
            0,
            str.length,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        if (click != null) {
            sp.setSpan(
                click, 0, str.length,
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
            )
        }
        return sp
    }

    /**
     * 设置文字大小
     * @return
     */
    fun textSizeSpan(str: String, start: Int, end: Int, size: Int): SpannableString {
        val spannableString = SpannableString(str)
        val sizeSpan =
            AbsoluteSizeSpan(size,true)
        spannableString.setSpan(sizeSpan, start, end, Spanned.SPAN_INCLUSIVE_EXCLUSIVE)
        return spannableString
    }


     fun getSpanNOBoldString(
        hinStr: String,
        start: Int,
        end: Int,
        colorRes: Int
    ): SpannableStringBuilder? {
        val sb = SpannableStringBuilder()
        sb.append(hinStr)
        val colorSpan = ForegroundColorSpan(BaseApplication.INSTANT.resources.getColor(colorRes))
        sb.setSpan(colorSpan, start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        sb.setSpan(StyleSpan(Typeface.NORMAL), 0, hinStr.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        return sb
    }
    /**
     * 设置文字不同颜色
     * @return
     */
    fun colorSpan(str: String, start: Int, end: Int, colorRes: Int): SpannableString {
        var start = start
        var end = end
        if (end == -1) {
            end = str.length
            start = end - start
        }
        val spannableString = SpannableString(str)
        val colorSpan =
            ForegroundColorSpan(BaseApplication.INSTANT.resources.getColor(colorRes))
        spannableString.setSpan(colorSpan, start, end, Spanned.SPAN_INCLUSIVE_EXCLUSIVE)
        return spannableString
    }
    fun getSizeColor(
        hinStr: String,
        color: String?,
        size: Int,
        start: Int,
        end: Int
    ): SpannableStringBuilder {
        val spannableStringBuilder = SpannableStringBuilder(hinStr)
        val colorSpan = ForegroundColorSpan(Color.parseColor(color))
        spannableStringBuilder.setSpan(colorSpan, start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        spannableStringBuilder.setSpan(
            StyleSpan(Typeface.NORMAL),
            0,
            hinStr.length,
            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        val ass = AbsoluteSizeSpan(size, true)
        spannableStringBuilder.setSpan(ass, start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        spannableStringBuilder.setSpan(
            StyleSpan(Typeface.NORMAL),
            start,
            end,
            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        return spannableStringBuilder
    }

    /**
     * 多个关键字高亮变色
     *
     * @param color   变化的色值
     * @param text    文字
     * @param keyword 文字中的关键字数组
     * @return
     */
    fun findSearch(color:Int,text:String,strlist:ArrayList<String>):SpannableString{

        var s = SpannableString(text)
        for ( ss in strlist){
            var p = Pattern.compile(ss)
            var m =p.matcher(s)
            while (m.find()){
                var start = m.start()
                var end = m.end()
                s.setSpan(ForegroundColorSpan(color),start,end,Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
            }
        }
        return s
    }


    /**
     * 设置大小和颜色
     */
    fun sizeAndColorSpan(
        str: String,
        start: Int,
        end: Int,
        size: Int,
        colorRes: Int
    ): SpannableString {
        var start = start
        var end = end
        if (end == -1) {
            end = str.length
            start = end - start
        }
        val spannableString = SpannableString(str)
        val colorSpan =
            ForegroundColorSpan(BaseApplication.INSTANT.resources.getColor(colorRes))
        val sizeSpan =
            AbsoluteSizeSpan(size,true)
        spannableString.setSpan(sizeSpan, start, end, Spanned.SPAN_INCLUSIVE_EXCLUSIVE)
        spannableString.setSpan(colorSpan, start, end, Spanned.SPAN_INCLUSIVE_EXCLUSIVE)
        return spannableString
    }
}