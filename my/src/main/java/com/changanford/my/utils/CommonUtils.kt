package com.changanford.my.utils

import android.content.Context
import android.graphics.Color
import android.text.SpannableString
import android.text.Spanned
import android.text.TextPaint
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.view.Gravity
import android.view.View
import android.widget.TextView
import androidx.appcompat.widget.AppCompatButton
import androidx.appcompat.widget.AppCompatTextView
import com.changanford.common.util.JumpUtils
import com.changanford.common.util.MConstant.H5_REGISTER_AGREEMENT
import com.changanford.common.util.MConstant.H5_USER_AGREEMENT
import com.changanford.my.R
import razerdp.basepopup.BasePopupWindow

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
fun TextView.signAgreement() {
    val title = "点击注册/登录，即表示已阅读并同意"
    var content = "《用户隐私条款》"
    var content1 = "《福域注册会员服务条款》"

    var spannable = SpannableString("$title$content、$content1")

    //设置颜色
    spannable.setSpan(
        object : ClickableSpan() {
            override fun onClick(widget: View) {
                JumpUtils.instans?.jump(1, H5_USER_AGREEMENT)
            }

            override fun updateDrawState(ds: TextPaint) {
                super.updateDrawState(ds)
                ds.color = Color.parseColor("#ffffff")
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
                ds.color = Color.parseColor("#ffffff")
                ds.isUnderlineText = false //去除超链接的下划线
            }
        }, title.length + content.length + 1,
        spannable.length,
        Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
    )

    this.text = spannable
    this.movementMethod = LinkMovementMethod.getInstance()
}


class ConfirmTwoBtnPop(context: Context?) : BasePopupWindow(context) {

    lateinit var contentText: AppCompatTextView
    lateinit var btnCancel: AppCompatButton
    lateinit var btnConfirm: AppCompatButton

    init {
        setContentView(R.layout.pop_two_btn)
        popupGravity = Gravity.CENTER
    }

    override fun onViewCreated(contentView: View) {
        super.onViewCreated(contentView)
        contentText = contentView.findViewById(R.id.text_content)
        btnCancel = contentView.findViewById(R.id.btn_cancel)
        btnConfirm = contentView.findViewById(R.id.btn_comfir)
    }
}
