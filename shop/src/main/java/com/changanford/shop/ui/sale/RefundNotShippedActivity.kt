package com.changanford.shop.ui.sale

import android.text.SpannableString
import android.text.Spanned
import android.text.TextUtils
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.content.ContextCompat
import com.alibaba.android.arouter.facade.annotation.Route
import com.changanford.common.basic.BaseActivity
import com.changanford.common.bean.BackEnumBean
import com.changanford.common.router.path.ARouterShopPath
import com.changanford.common.util.CustomImageSpanV2
import com.changanford.common.utilext.toast
import com.changanford.shop.R
import com.changanford.shop.bean.RefundBean
import com.changanford.shop.databinding.ActivityRefundNoShippedBinding
import com.changanford.shop.ui.sale.request.RefundViewModel
import com.changanford.shop.ui.shoppingcart.dialog.RefundResonDialog
import com.google.gson.Gson

/***
 *    申请退款-未发货-退整单
 * */
@Route(path = ARouterShopPath.RefundNotShippedActivity)
class RefundNotShippedActivity : BaseActivity<ActivityRefundNoShippedBinding, RefundViewModel>() {
    var backEnumBean:BackEnumBean?=null
    override fun initView() {
        binding.layoutTop.tvTitle.text = "退款"

        binding.layoutTop.imgBack.setOnClickListener {
            onBackPressed()
        }
        binding.tvReason.setOnClickListener {
            RefundResonDialog(this, object : RefundResonDialog.CallMessage {
                override fun message(reson: BackEnumBean) {
                    binding.tvReason.text = reson.message
                    backEnumBean=reson
                }
            }).show()
        }
        binding.tvHandle.setOnClickListener {

            if (canRefund()) {
                refundBean?.let {
                    viewModel.getRefund("ALL_ORDER", it.orderNo, backEnumBean!!.code)
                }

            }
        }
    }

    var reson: String = ""
    fun canRefund(): Boolean {
        reson = binding.tvReason.text.toString()
        if (TextUtils.isEmpty(reson)) {
            "请选择退款原因".toast()
            return false
        }
        return true
    }

    var refundBean: RefundBean? = null
    override fun initData() {
        val refunInfoStr = intent.getStringExtra("value")
        val gson = Gson()
        refundBean = gson.fromJson<RefundBean>(refunInfoStr, RefundBean::class.java)
        showTotalTag(binding.tvRefundMoney, refundBean!!)
    }

    fun showZero(text: AppCompatTextView?, item: RefundBean) {
        val tagName = item.payRmb
        //先设置原始文本
        text?.text = "合计".plus("  ￥${tagName}")
    }

    fun showTotalTag(text: AppCompatTextView?, item: RefundBean) {
        if (TextUtils.isEmpty(item.payFb)) {
            showZero(text, item)
            return
        }
        item.payFb?.let { // 福币为0
            if (it.toInt() <= 0) {
                showZero(text, item)
                return
            }
        }
        val fbNumber = item.payFb

        val starStr = "合计: "

        val str = if (TextUtils.isEmpty(item.payRmb)) {
            "$starStr[icon] ${item.payFb}"
        } else {
            "$starStr[icon] ${item.payFb}+￥${item.payRmb}"
        }

        //先设置原始文本
        text?.text = str
        //使用post方法，在TextView完成绘制流程后在消息队列中被调用
        text?.post { //获取第一行的宽度
            val stringBuilder: StringBuilder = StringBuilder(str)
            //SpannableString的构建
            val spannableString = SpannableString("$stringBuilder ")
            val drawable = ContextCompat.getDrawable(this, R.mipmap.question_fb)
            drawable?.apply {
                val imageSpan = CustomImageSpanV2(this)
                setBounds(0, 0, intrinsicWidth, intrinsicHeight)
                val strLength = spannableString.length
                val numberLength = fbNumber?.length
                val startIndex = strLength - numberLength!! - 1
                spannableString.setSpan(
                    imageSpan, str.lastIndexOf("["), str.lastIndexOf("]") + 1,
                    Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
                )
                text.text = spannableString
            }
        }
    }
}