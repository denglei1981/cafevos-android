package com.changanford.circle.utils

import android.content.Context
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.text.*
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.text.style.ForegroundColorSpan
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import android.view.ViewTreeObserver.OnGlobalLayoutListener
import android.widget.TextView
import androidx.annotation.DrawableRes
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import anet.channel.util.Utils.context
import com.bumptech.glide.Glide
import com.changanford.circle.R

import com.changanford.common.router.path.ARouterCirclePath
import com.changanford.common.router.startARouter
import com.changanford.common.utilext.toIntPx

object MUtils {

    fun postDetailsFrom(textView: TextView, content: String, circleId: String) {

        val content1 = "来自"

        val spannable = SpannableString(content1 + content)

        spannable.setSpan(
            object : ClickableSpan() {
                override fun onClick(widget: View) {
                    val bundle = Bundle()
                    bundle.putString("circleId", circleId)
                    startARouter(ARouterCirclePath.CircleDetailsActivity, bundle)
                }

                override fun updateDrawState(ds: TextPaint) {
                    super.updateDrawState(ds)
                    ds.color = ContextCompat.getColor(textView.context, R.color.circle_1B3B89)
                    ds.isUnderlineText = false //去除超链接的下划线
                }
            }, content1.length,
            spannable.length,
            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        textView.text = spannable
        textView.movementMethod = LinkMovementMethod.getInstance()
    }

    fun postDetailsFromVideo(textView: TextView, content: String, circleId: String) {

        val content1 = "来自"

        val spannable = SpannableString(content1 + content)

        spannable.setSpan(
            object : ClickableSpan() {
                override fun onClick(widget: View) {
                    val bundle = Bundle()
                    bundle.putString("circleId", circleId)
                    startARouter(ARouterCirclePath.CircleDetailsActivity, bundle)
                }

                override fun updateDrawState(ds: TextPaint) {
                    super.updateDrawState(ds)
                    ds.color = ContextCompat.getColor(textView.context, R.color.circle_9eaed8)
                    ds.isUnderlineText = false //去除超链接的下划线
                }
            }, content1.length,
            spannable.length,
            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        textView.text = spannable
        textView.movementMethod = LinkMovementMethod.getInstance()
    }

    fun setDrawableStar(textView: TextView, @DrawableRes resource: Int) {
        val drawable: Drawable = ContextCompat.getDrawable(textView.context, resource)!!
        drawable.setBounds(
            0, 0, drawable.minimumWidth,
            drawable.minimumHeight
        )
        textView.setCompoundDrawables(drawable, null, null, null)
    }

    /**
     * 设置textView结尾...后面显示的文字和颜色
     * @param context 上下文
     * @param textView textview
     * @param minLines 最少的行数
     * @param originText 原文本
     * @param endText 结尾文字
     * @param endColorID 结尾文字颜色id
     * @param isExpand 当前是否是展开状态
     */
    fun toggleEllipsize(
        context: Context,
        textView: TextView,
        minLines: Int,
        originText: String,
        endText: String,
        endColorID: Int,
        isExpand: Boolean
    ) {
        if (TextUtils.isEmpty(originText)) {
            return
        }
        textView.viewTreeObserver.addOnGlobalLayoutListener(object :
            OnGlobalLayoutListener {
            override fun onGlobalLayout() {
                if (isExpand) {
                    textView.text = originText
                } else {
                    val paddingLeft = textView.paddingLeft
                    val paddingRight = textView.paddingRight
                    val paint = textView.paint
                    val moreText = textView.textSize * endText.length
                    val availableTextWidth = (textView.width - paddingLeft - paddingRight) *
                            minLines - moreText
                    val ellipsizeStr: CharSequence = TextUtils.ellipsize(
                        originText, paint,
                        availableTextWidth, TextUtils.TruncateAt.END
                    )
                    if (ellipsizeStr.length < originText.length) {
                        val temp: CharSequence = ellipsizeStr.toString() + endText
                        val ssb = SpannableStringBuilder(temp)
                        ssb.setSpan(
                            ForegroundColorSpan(context.resources.getColor(endColorID)),
                            temp.length - endText.length,
                            temp.length,
                            Spannable.SPAN_INCLUSIVE_EXCLUSIVE
                        )
                        textView.text = ssb
                    } else {
                        textView.text = originText
                    }
                }
                textView.viewTreeObserver.removeOnGlobalLayoutListener(this)
            }
        })
    }

    /**
     * 列表第一个item追加margin
     */
    fun setTopMargin(view: View, margin: Int, position: Int) {
        val params = view.layoutParams as ViewGroup.MarginLayoutParams
        if (position == 0) {
            params.topMargin =
                margin.toIntPx()
        } else params.topMargin = 0
    }

    private var expand = "展开 ∨"
    private var collapse = "收起 ∧"

     fun expandText(contentTextView: TextView, msg: String) {
        val text: CharSequence = contentTextView.text
        val width: Int = contentTextView.width
        val paint: TextPaint = contentTextView.paint
        val layout: Layout = contentTextView.layout
        val line: Int = layout.lineCount
        if (line > 4) {
            val start: Int = layout.getLineStart(3)
            val end: Int = layout.getLineVisibleEnd(3)
            val lastLine = text.subSequence(start, end)
            val expandWidth = paint.measureText(expand)
            val remain = width - expandWidth
            val ellipsize: CharSequence = TextUtils.ellipsize(
                lastLine,
                paint,
                remain,
                TextUtils.TruncateAt.END
            )
            val clickableSpan: ClickableSpan = object : ClickableSpan() {
                override fun onClick(widget: View) {
                    collapseText(contentTextView, msg)
                }

                override fun updateDrawState(ds: TextPaint) {
                    super.updateDrawState(ds)
                    ds.isUnderlineText = false
                }
            }
            val ssb = SpannableStringBuilder()
            ssb.append(text.subSequence(0, start))
            ssb.append(ellipsize)
            ssb.append(expand)
            ssb.setSpan(
                clickableSpan,
                ssb.length - expand.length, ssb.length,
                Spanned.SPAN_INCLUSIVE_EXCLUSIVE
            )
            ssb.setSpan(
                ForegroundColorSpan(contentTextView.resources.getColor(R.color.color_8195C8)),
                ssb.length - expand.length, ssb.length,
                Spanned.SPAN_INCLUSIVE_EXCLUSIVE
            )
            contentTextView.movementMethod = LinkMovementMethod.getInstance()
            contentTextView.text = ssb
        }
    }

     fun collapseText(contentTextView: TextView, msg: String) {

        // 默认此时文本肯定超过行数了，直接在最后拼接文本
        val clickableSpan: ClickableSpan = object : ClickableSpan() {
            override fun onClick(widget: View) {
                expandText(contentTextView, msg)
            }

            override fun updateDrawState(ds: TextPaint) {
                super.updateDrawState(ds)
                ds.isUnderlineText = false
            }
        }
        val ssb = SpannableStringBuilder()
        ssb.append(msg)
        ssb.append(collapse)
        ssb.setSpan(
            clickableSpan,
            ssb.length - collapse.length, ssb.length,
            Spanned.SPAN_INCLUSIVE_EXCLUSIVE
        )
        ssb.setSpan(
            ForegroundColorSpan(contentTextView.resources.getColor(R.color.color_8195C8)),
            ssb.length - collapse.length, ssb.length,
            Spanned.SPAN_INCLUSIVE_EXCLUSIVE
        )
        contentTextView.text = ssb
    }

}