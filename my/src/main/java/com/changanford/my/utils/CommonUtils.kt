package com.changanford.my.utils

import android.graphics.Color
import android.text.SpannableString
import android.text.Spanned
import android.text.TextPaint
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.view.View
import android.widget.TextView
import com.changanford.common.util.JumpUtils
import com.changanford.common.util.MConstant.H5_REGISTER_AGREEMENT
import com.changanford.common.util.MConstant.H5_USER_AGREEMENT

/**
 *  文件名：CommonUtils
 *  创建者: zcy
 *  创建日期：2021/9/16 20:10
 *  描述: TODO
 *  修改描述：TODO
 */

/**
 * 登录协议
 */
fun signAgreement(textView: TextView?) {
    val title = "我已阅读并同意"
    var content = "《用户隐私协议》"
    var content1 = "《引力域注册服务条款》"

    var spannable = SpannableString(title + content + "和" + content1)

    //设置颜色
    spannable.setSpan(
        object : ClickableSpan() {
            override fun onClick(widget: View) {
                JumpUtils.instans?.jump(1, H5_USER_AGREEMENT)
            }

            override fun updateDrawState(ds: TextPaint) {
                super.updateDrawState(ds)
                ds.color = Color.parseColor("#1B3B89")
                ds.isUnderlineText = false //去除超链接的下划线
            }
        }, title.length,
        title.length + content.length,
        Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
    )

    spannable.setSpan(
        object : ClickableSpan() {
            override fun onClick(widget: View) {
                JumpUtils.instans?.jump(1, H5_REGISTER_AGREEMENT)
            }

            override fun updateDrawState(ds: TextPaint) {
                super.updateDrawState(ds)
                ds.color = Color.parseColor("#1B3B89")
                ds.isUnderlineText = false //去除超链接的下划线
            }
        }, title.length + content.length + 1,
        spannable.length,
        Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
    )

    textView!!.text = spannable
    textView.movementMethod = LinkMovementMethod.getInstance()
}
