package com.changanford.common.util

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.drawable.Drawable
import android.icu.math.BigDecimal.ROUND_UP
import android.text.SpannableString
import android.text.Spanned
import android.text.TextUtils
import android.text.style.ImageSpan
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.content.ContextCompat
import com.changanford.common.R
import com.changanford.common.bean.OrderItemBean
import com.changanford.common.bean.PayShowBean
import com.changanford.common.wutil.WCommonUtil
import com.changanford.common.wutil.WCommonUtil.getHeatNum
import com.changanford.common.wutil.WCommonUtil.getRoundedNum
import java.math.BigDecimal

class CustomImageSpan(drawable: Drawable?) : ImageSpan(drawable!!) {
    override fun draw(
        canvas: Canvas,
        text: CharSequence,
        start: Int,
        end: Int,
        x: Float,
        top: Int,
        y: Int,
        bottom: Int,
        paint: Paint
    ) {
        val fm = paint.fontMetricsInt
        val drawable = drawable

        val transY = (y + fm.descent + y + fm.ascent) / 2 - drawable.bounds.bottom / 2 + top + 4
        canvas.save()
        canvas.translate(x, transY.toFloat())
        drawable.draw(canvas)
        canvas.restore()
    }
}

class CustomImageSpanV2(drawable: Drawable?) : ImageSpan(drawable!!) {
    override fun draw(
        canvas: Canvas,
        text: CharSequence,
        start: Int,
        end: Int,
        x: Float,
        top: Int,
        y: Int,
        bottom: Int,
        paint: Paint
    ) {
        val fm = paint.fontMetricsInt
        val drawable = drawable

        val transY = (y + fm.descent + y + fm.ascent) / 2 - drawable.bounds.bottom / 2 + top + 2
        canvas.save()
        canvas.translate(x, transY.toFloat())
        drawable.draw(canvas)
        canvas.restore()
    }
}

fun showTotalTag(
    context: Context,
    text: AppCompatTextView?,
    item: PayShowBean,
    isStart: Boolean = true,
    totalNum: Int = -1,
    nuwNum: Int = -1,
) {
    if (TextUtils.isEmpty(item.payFb)) {
        showZero(text, item, isStart, context = context)
        return
    }
    item.payFb?.let { // 福币为0
        if (it.toInt() <= 0) {
            showZero(text, item, isStart = isStart, context = context, totalNum, nuwNum)
            return
        }
    }
    val fbNumber = item.payFb
    var starStr = ""
    if (isStart) {
        starStr = "合计: "
    }
    var str = if (TextUtils.isEmpty(item.payRmb)) {
        "$starStr[icon] ${item.payFb}"
    } else {
        if (item.payRmb == "0") {
            "$starStr[icon] ${item.payFb}"
        } else {
            "$starStr[icon] ${item.payFb}+￥${item.payRmb}"
        }

    }
    if (totalNum != -1 && nuwNum != -1) {
        str = if (TextUtils.isEmpty(item.payRmb)) {
            "$starStr[icon] ${(item.payFb?.toInt() ?: 0) * nuwNum / totalNum}"
        } else {
            val onePayRbm =
                if (item.payRmb.isNullOrEmpty() || item.payRmb == "0") {
                    0f
                } else (item.payRmb!!.toFloat() / totalNum).toFloat()
            val onePayFb =
                if (item.payFb.isNullOrEmpty() || item.payFb == "0" || item.payFb == "0") 0f else item.payFb!!.toFloat() / totalNum
            if (item.payRmb == "0" || "${onePayRbm * nuwNum}" == "0.00"
            ) {
                if (nuwNum == totalNum) {//全部退了
                    "$starStr[icon] ${item.payFb}"
                } else {
                    "$starStr[icon] ${getHeatNum("${onePayFb * nuwNum}", 0)}"
                }
            } else {
                if (nuwNum == totalNum) {
                    "$starStr[icon] ${item.payFb}+￥${item.payRmb}"
                } else {
                    "$starStr[icon] ${getHeatNum("${onePayFb * nuwNum}", 0)}+￥${
                        getHeatNum("${onePayRbm * nuwNum}", 2)
                    }"
                }
            }

        }
    }
    //先设置原始文本
//    text?.text = str
    //使用post方法，在TextView完成绘制流程后在消息队列中被调用
    text?.post { //获取第一行的宽度
        val stringBuilder: StringBuilder = StringBuilder(str)
        //SpannableString的构建
        val spannableString = SpannableString("$stringBuilder ")
        val drawable = ContextCompat.getDrawable(context, R.mipmap.question_fb)
        drawable?.apply {
            val imageSpan = CustomImageSpanV2(this)
            setBounds(0, 0, intrinsicWidth, intrinsicHeight)
            val strLength = spannableString.length
            val numberLength = fbNumber?.length
//            val startIndex = strLength - numberLength!! - 1
            spannableString.setSpan(
                imageSpan, str.lastIndexOf("["), str.lastIndexOf("]") + 1,
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
            )
            text.text = spannableString
        }
    }
}

fun showZeroFb(context: Context, text: AppCompatTextView?) {
    val str = "[icon] ${0}"
    //先设置原始文本
    text?.text = str
    //使用post方法，在TextView完成绘制流程后在消息队列中被调用
    text?.post { //获取第一行的宽度
        val stringBuilder: StringBuilder = StringBuilder(str)
        //SpannableString的构建
        val spannableString = SpannableString("$stringBuilder ")
        val drawable = ContextCompat.getDrawable(context, R.mipmap.question_fb)
        drawable?.apply {
            val imageSpan = CustomImageSpanV2(this)
            setBounds(0, 0, intrinsicWidth, intrinsicHeight)
            spannableString.setSpan(
                imageSpan, str.lastIndexOf("["), str.lastIndexOf("]") + 1,
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
            )
            text.text = spannableString
        }
    }
}

@SuppressLint("SetTextI18n")
fun showZero(
    text: AppCompatTextView?,
    item: PayShowBean,
    isStart: Boolean,
    context: Context,
    totalNum: Int = -1,
    nuwNum: Int = -1,
) {
    val tagName = item.payRmb
    //先设置原始文本
//    if(isStart){
//        text?.text = "合计".plus("  ￥${tagName}")
//    }else{
//
//    }
    if (TextUtils.isEmpty(item.payRmb)) {
        showZeroFb(context, text)
    } else {
        if (item.payRmb == "0") {
            showZeroFb(context, text)
        } else {
            if (isStart) {
                text?.text = "合计".plus("  ￥${tagName}")
            } else {
                text?.post {
                    text.text = "￥${tagName}"
                    if (totalNum != -1 && nuwNum != -1) {
                        text.text = "¥${
                            getHeatNum(
                                "${
                                    getRoundedNum(
                                        item.payRmb,
                                        2
                                    ) * BigDecimal(nuwNum).divide(BigDecimal(totalNum))
                                }", 2
                            )
                        }"
                    }
                }
            }
        }

    }


}