package com.changanford.common.util

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.PixelFormat
import android.graphics.Typeface
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.CountDownTimer
import android.text.InputFilter
import android.text.Layout
import android.text.SpannableString
import android.text.SpannableStringBuilder
import android.text.Spanned
import android.text.TextPaint
import android.text.TextUtils
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.text.style.ForegroundColorSpan
import android.text.style.RelativeSizeSpan
import android.text.style.StyleSpan
import android.view.View
import android.widget.CheckBox
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.changanford.common.MyApp
import com.changanford.common.basic.BaseApplication
import com.changanford.common.bean.DialogBottomBean
import com.changanford.common.bean.STSBean
import com.changanford.common.bean.WeekBean
import com.changanford.common.util.MConstant.H5_CANCEL_ACCOUNT
import com.changanford.common.util.MConstant.H5_REGISTER_AGREEMENT
import com.changanford.common.util.MConstant.H5_USER_AGREEMENT
import com.changanford.common.util.MConstant.H5_privacy
import com.changanford.common.util.MConstant.H5_regTerms
import com.changanford.common.util.bus.LiveDataBusKey
import com.changanford.common.util.room.UserDatabase
import com.changanford.common.utilext.toast
import com.changanford.common.web.AgentWebActivity
import com.changanford.common.widget.CallPhoneDialog
import com.luck.picture.lib.entity.LocalMedia
import java.text.DecimalFormat
import java.util.Calendar
import java.util.regex.Pattern


/**
 *  文件名：Utils
 *  创建者: zcy
 *  创建日期：2020/5/9 18:23
 *  描述: TODO
 *  修改描述：TODO
 */
object MineUtils {

    /**
     * 底部弹框 选项
     */
    var listPhoto = arrayListOf(
        DialogBottomBean(1, "直接拍照", 0, 1),
        DialogBottomBean(2, "从相册选择", 0, 1)
    )


    var releasePhoto = arrayListOf(
        DialogBottomBean(1, "线下活动", 0, 1),
        DialogBottomBean(2, "调查问卷", 0, 1)
    )

    /**
     * 车主认证 底部弹框
     */
    var listCarAuth = arrayListOf(
        DialogBottomBean(1, "我是车主本人", 0, 1),
        DialogBottomBean(2, "我不是车主", 0, 1)
    )


    /**
     * 证件照 底部弹框
     */
    var listCarAuthCard = arrayListOf(
        DialogBottomBean(1, "身份证", 0, 1),
        DialogBottomBean(2, "警官证", 0, 1),
        DialogBottomBean(3, "军官证", 0, 1)
    )


    /**
     * 性别选择
     */
    var listSex = arrayListOf(
        DialogBottomBean(1, "男", 0, 1),
        DialogBottomBean(2, "女", 0, 1)
    )


    /**
     * 帖子长按操作
     */
    var postBottomList = arrayListOf(
        DialogBottomBean(1, "申请加精", 0, 1),
        DialogBottomBean(2, "编辑", 0, 1),
        DialogBottomBean(3, "删除", 0, 1)
    )


    /**
     * 订单筛选
     */
    var listOrder = arrayListOf(
        "全部", "上门试乘试驾", "上门交车", "上门取送车", "订车订单", "商城订单"
    )


    var listWeek = arrayListOf(
        WeekBean("周一", false),
        WeekBean("周二", false),
        WeekBean("周三", false),
        WeekBean("周四", false),
        WeekBean("周五", false),
        WeekBean("周六", false),
        WeekBean("周日", false)
    )

    /**
     * F币弹框
     */
    var listIntegral = arrayListOf(
        DialogBottomBean(1, "福币规则"),
        DialogBottomBean(1, "获取福币")
    )

    /**
     * 勋章弹框
     */
    var listMedal = arrayListOf(
        DialogBottomBean(1, "勋章等级"),
        DialogBottomBean(1, "勋章设置")
    )


    /**
     * dealer地图名称
     */
    var listMap = arrayListOf(
        DialogBottomBean(1, "高德地图"),
        DialogBottomBean(2, "百度地图")
//        DialogBottomBean(3, "百度地图")
    )


    fun isNotNull(content: String): Boolean {
        if (content.isNotEmpty() && "null" != content && content.length > 0) {
            return true
        }
        return false
    }

    /**
     * 设置我的粉丝 收藏等样式
     */
    fun setTextNum(textView: TextView?, content: String?, num: Int) {
        var numB: String = num.toString()
        val decimalFormat = DecimalFormat(".00") //构造方法的字符格式这里如果小数不足2位,会以0补足.

        //大于1k的单位k
        if (num in 1000..9999) {
            numB = decimalFormat.format(num.toFloat() / 1000).toString() + "K"
        } else if (num >= 10000) {
            numB = decimalFormat.format(num.toFloat() / 10000).toString() + "万"
        }
        var spannable = SpannableString(numB + "\n" + content)

        //设置颜色
        var colorSpannable = ForegroundColorSpan(Color.parseColor("#081725"))
        spannable.setSpan(
            colorSpannable,
            0,
            numB.length,
            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        )

        //设置字体大小为1.3
        var sizeSpannable = RelativeSizeSpan(1.3f)
        spannable.setSpan(
            sizeSpannable, 0, numB.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        )

        //字体加粗
        var styleSpan = StyleSpan(Typeface.BOLD)
        spannable.setSpan(styleSpan, 0, numB.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        textView!!.text = spannable
    }

    fun num(num: Long): String {
        var numB: String = num.toString()
        val decimalFormat = DecimalFormat(".00") //构造方法的字符格式这里如果小数不足2位,会以0补足.

        //大于1k的单位k
        if (num in 1000..9999) {
            numB = decimalFormat.format(num.toFloat() / 1000).toString() + "K"
        } else if (num >= 10000) {
            numB = decimalFormat.format(num.toFloat() / 10000).toString() + "W"
        }
        return numB
    }

    /**
     * 上万截取
     */
    fun countW(num: Long): String {
        var numB: String = num.toString()
        val decimalFormat = DecimalFormat(".0") //构造方法的字符格式这里如果小数不足2位,会以0补足.

        if (num >= 10000) {
            numB = decimalFormat.format(num.toFloat() / 10000).toString() + "万"
        }
        return numB;
    }

    /**
     * 认证权益 认证规则
     */
    fun setUniAuthTextNum(textView: TextView?, content: String?, title: String) {
        var spannable = SpannableString(title + "\n" + content)

        //设置颜色
        var colorSpannable = ForegroundColorSpan(Color.parseColor("#141C26"))
        spannable.setSpan(
            colorSpannable,
            0,
            title.length,
            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        )

        //设置字体大小为1.3
        var sizeSpannable = RelativeSizeSpan(1.3f)
        spannable.setSpan(
            sizeSpannable, 0, title.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        )

        //字体加粗
        var styleSpan = StyleSpan(Typeface.BOLD)
        spannable.setSpan(styleSpan, 0, title.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        textView!!.text = spannable
    }


    /**
     * 车主认证  我不是车主  提示
     */
    fun setUniAuthCarType(textView: TextView?, content: String, title: String) {
        var spannable = SpannableString(content + "\n" + title)

        //设置颜色
        var colorSpannable = ForegroundColorSpan(Color.parseColor("#71747B"))
        spannable.setSpan(
            colorSpannable,
            content.length,
            spannable.length,
            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        )

        //设置字体大小为1.3
        var sizeSpannable = RelativeSizeSpan(0.8f)
        spannable.setSpan(
            sizeSpannable, content.length, spannable.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        )

        textView!!.text = spannable
    }


    /**
     * 登录协议
     */
    fun signAgreement(activity: AppCompatActivity? = null, textView: TextView?) {

        val title = "我已阅读并同意"
        var content = "《用户隐私协议》"
        var content1 = "《福域注册服务条款》"

        var spannable = SpannableString(title + content + "和" + content1)

        //设置颜色
        spannable.setSpan(
            object : ClickableSpan() {
                override fun onClick(widget: View) {
                    JumpUtils.instans?.jump(1, H5_USER_AGREEMENT)
                }

                override fun updateDrawState(ds: TextPaint) {
                    super.updateDrawState(ds)
                    ds.color = Color.parseColor("#FC883B")
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
                    ds.color = Color.parseColor("#FC883B")
                    ds.isUnderlineText = false //去除超链接的下划线
                }
            }, title.length + content.length + 1,
            spannable.length,
            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        )

        textView!!.text = spannable
        textView.movementMethod = LinkMovementMethod.getInstance()
    }


    /**
     * 登录协议
     */
    fun fansAgreement(textView: TextView?) {

        val title = "我已阅读并同意"
        var content = "《用户隐私协议》"

        var spannable = SpannableString(title + content)

        //设置颜色
        spannable.setSpan(
            object : ClickableSpan() {
                override fun onClick(widget: View) {
                    JumpUtils.instans?.jump(1, H5_USER_AGREEMENT)
                }

                override fun updateDrawState(ds: TextPaint) {
                    super.updateDrawState(ds)
                    ds.color = Color.parseColor("#FC883B")
                    ds.isUnderlineText = false //去除超链接的下划线
                }
            }, title.length,
            title.length + content.length,
            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        )

        textView!!.text = spannable
        textView.movementMethod = LinkMovementMethod.getInstance()
    }

    /**
     * 登录协议
     */
    fun cancelAgreement(textView: TextView?) {

        val title = "我已阅读并同意"
        var content = "《注销账号协议》"

        var spannable = SpannableString(title + content)

        //设置颜色
        spannable.setSpan(
            object : ClickableSpan() {
                override fun onClick(widget: View) {
                    JumpUtils.instans?.jump(1, H5_CANCEL_ACCOUNT)
                }

                override fun updateDrawState(ds: TextPaint) {
                    super.updateDrawState(ds)
                    ds.color = Color.parseColor("#FC883B")
                    ds.isUnderlineText = false //去除超链接的下划线
                }
            }, title.length,
            title.length + content.length,
            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        )

        textView!!.text = spannable
        textView.movementMethod = LinkMovementMethod.getInstance()
    }


    /**
     * 用户隐私协议
     */
    fun hideAgreement(textView: TextView?) {

        val title = "我已阅读并同意"
        var content = "《用户隐私协议》"

        var spannable = SpannableString(title + content)

        //设置颜色
        spannable.setSpan(
            object : ClickableSpan() {
                override fun onClick(widget: View) {
                    JumpUtils.instans?.jump(1, H5_USER_AGREEMENT)
                }

                override fun updateDrawState(ds: TextPaint) {
                    super.updateDrawState(ds)
                    ds.color = Color.parseColor("#FC883B")
                    ds.isUnderlineText = false //去除超链接的下划线
                }
            }, title.length,
            title.length + content.length,
            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        )

        textView!!.text = spannable
        textView.movementMethod = LinkMovementMethod.getInstance()
    }

    /**
     * 《U享卡服务协议》
     */
    fun uniCardAgreement(textView: CheckBox?) {

        val title = "我已阅读并同意"
        var content = "《U享卡服务协议》"

        var spannable = SpannableString(title + content)

        //设置颜色
        spannable.setSpan(
            object : ClickableSpan() {
                override fun onClick(widget: View) {
//                    JumpUtils.instans?.jump(1, H5_UNI_CARD_AGREE)
                }

                override fun updateDrawState(ds: TextPaint) {
                    super.updateDrawState(ds)
                    ds.color = Color.parseColor("#FB873B")
                    ds.isUnderlineText = false //去除超链接的下划线
                }
            }, title.length,
            title.length + content.length,
            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        )

        textView!!.text = spannable
        textView.movementMethod = LinkMovementMethod.getInstance()
    }

    fun popUpdateAgree(textView: TextView, content: String, code: String) {
        if (code.contains(",")) {//有2个协议
            val s = code.split(",")
            if (s.size == 2) {
                val agree1 =
                    if (s[0] == MConstant.agreementPrivacy) "《福域APP/小程序个人信息保护政策》" else "《福域APP会员服务协议》"
                val agree2 =
                    if (s[1] == MConstant.agreementPrivacy) "《福域APP/小程序个人信息保护政策》" else "《福域APP会员服务协议》"

                val lookAll = "\n\n查看完整版"

                val spannable = SpannableString(content + lookAll + agree1 + "和" + agree2)
                val sizeSpannable = RelativeSizeSpan(1f)
                spannable.setSpan(
                    sizeSpannable,
                    0,
                    content.length,
                    Spanned.SPAN_INCLUSIVE_EXCLUSIVE
                )

                spannable.setSpan(
                    object : ClickableSpan() {
                        override fun onClick(widget: View) {
                            val intent =
                                Intent(BaseApplication.curActivity, AgentWebActivity::class.java)
                            if (s[0] == MConstant.agreementPrivacy) {
                                intent.putExtra("value", H5_privacy)
                            } else {
                                intent.putExtra("value", H5_regTerms)
                            }
                            BaseApplication.curActivity.startActivity(intent)
                        }

                        override fun updateDrawState(ds: TextPaint) {
                            super.updateDrawState(ds)
                            ds.color = Color.parseColor("#1700f4")
                            ds.isUnderlineText = false //去除超链接的下划线
                        }
                    }, content.length + lookAll.length,
                    content.length + lookAll.length + agree1.length,
                    Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
                )

                spannable.setSpan(
                    object : ClickableSpan() {
                        override fun onClick(widget: View) {
                            val intent =
                                Intent(BaseApplication.curActivity, AgentWebActivity::class.java)
                            if (s[1] == MConstant.agreementPrivacy) {
                                intent.putExtra("value", H5_privacy)
                            } else {
                                intent.putExtra("value", H5_regTerms)
                            }
                            BaseApplication.curActivity.startActivity(intent)
                        }

                        override fun updateDrawState(ds: TextPaint) {
                            super.updateDrawState(ds)
                            ds.color = Color.parseColor("#1700f4")
                            ds.isUnderlineText = false //去除超链接的下划线
                        }
                    }, content.length + lookAll.length + agree1.length + 1,
                    spannable.length,
                    Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
                )
                textView.text = spannable
                textView.movementMethod = LinkMovementMethod.getInstance()
            }
        } else {
            val agree =
                if (code == MConstant.agreementPrivacy) "《福域APP/小程序个人信息保护政策》" else "《福域APP会员服务协议》"
            val lookAll = "\n\n查看完整版"

            val spannable = SpannableString(content + lookAll + agree)
            val sizeSpannable = RelativeSizeSpan(1f)
            spannable.setSpan(
                sizeSpannable,
                0,
                content.length,
                Spanned.SPAN_INCLUSIVE_EXCLUSIVE
            )

            spannable.setSpan(
                object : ClickableSpan() {
                    override fun onClick(widget: View) {
                        val intent =
                            Intent(BaseApplication.curActivity, AgentWebActivity::class.java)
                        if (code == MConstant.agreementPrivacy) {
                            intent.putExtra("value", H5_privacy)
                        } else {
                            intent.putExtra("value", H5_regTerms)
                        }
                        BaseApplication.curActivity.startActivity(intent)
                    }

                    override fun updateDrawState(ds: TextPaint) {
                        super.updateDrawState(ds)
                        ds.color = Color.parseColor("#1700f4")
                        ds.isUnderlineText = false //去除超链接的下划线
                    }
                }, content.length + lookAll.length,
                content.length + lookAll.length + agree.length,
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
            )

            textView.text = spannable
            textView.movementMethod = LinkMovementMethod.getInstance()
        }
    }

    /**
     * 首次今日App提示协议
     */
    fun popAgreement(textView: TextView?) {
        //温馨提示
        //感谢您信任并使用福域！在使用前，请您务必仔细阅读并充分理解“用户服务协议”和“隐私政策”相关条款内容，在确认充分理解并同意后使用福域相关产品或服务。点击同意即代表您已阅读并同意《用户服务协议》和《隐私政策》，如果您不同意，将可能影响使用福域的产品和服务。
        //我们将按法律法规要求，采取相应安全保护措施，尽力保护您的个人信息安全可控。

        val title = ""
        val titleC =
            "感谢您信任并使用福域！在使用前，请您务必仔细阅读并充分理解“福域APP会员服务协议”和“福域APP/小程序个人信息保护政策”相关条款内容，在确认充分理解并同意后使用福域相关产品或服务。点击同意即代表您已阅读并同意"
        val content = "《福域APP会员服务协议》"
        val content1 = "《福域APP/小程序个人信息保护政策》"

        val spannable =
            SpannableString("${title}${titleC}${content}和${content1}，如果您不同意，将可能影响使用福域的产品和服务。我们将按法律法规要求，采取相应安全保护措施，尽力保护您的个人信息安全可控。")

        val sizeSpannable = RelativeSizeSpan(1.2f)
        spannable.setSpan(sizeSpannable, 0, title.length, Spanned.SPAN_INCLUSIVE_EXCLUSIVE)
        //设置颜色
        spannable.setSpan(
            object : ClickableSpan() {
                override fun onClick(widget: View) {
//                    JumpUtils.instans?.jump(1, H5_regTerms)
                    val intent = Intent(BaseApplication.curActivity, AgentWebActivity::class.java)
                    intent.putExtra("value", H5_regTerms)
                    BaseApplication.curActivity.startActivity(intent)
                }

                override fun updateDrawState(ds: TextPaint) {
                    super.updateDrawState(ds)
                    ds.color = Color.parseColor("#FC883B")
                    ds.isUnderlineText = false //去除超链接的下划线
                }
            }, title.length + titleC.length,
            title.length + titleC.length + content.length,
            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        )

        spannable.setSpan(
            object : ClickableSpan() {
                override fun onClick(widget: View) {
//                    JumpUtils.instans?.jump(1, H5_privacy)
                    val intent = Intent(BaseApplication.curActivity, AgentWebActivity::class.java)
                    intent.putExtra("value", H5_privacy)
                    BaseApplication.curActivity.startActivity(intent)
                }

                override fun updateDrawState(ds: TextPaint) {
                    super.updateDrawState(ds)
                    ds.color = Color.parseColor("#FC883B")
                    ds.isUnderlineText = false //去除超链接的下划线
                }
            }, title.length + titleC.length + content.length + 1,
            title.length + titleC.length + content.length + 1 + content1.length,
            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        )

        textView!!.text = spannable
        textView.setMovementMethod(LinkMovementMethod.getInstance())
    }

    /**
     * 设置页面 电话的样式
     */
    fun settingPhone(textView: TextView?, content: String, title: String) {
        if (content.isNullOrEmpty() || title.isNullOrEmpty()) {
            return
        }
        var spannable = SpannableString(content + "\n" + title)

        //设置颜色
        var colorSpannable = ForegroundColorSpan(Color.parseColor("#71747B"))
        spannable.setSpan(
            colorSpannable,
            content.length,
            spannable.length,
            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        )

        //设置字体大小为1.3
        var sizeSpannable = RelativeSizeSpan(0.8f)
        spannable.setSpan(
            sizeSpannable, content.length, spannable.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        )

        textView!!.text = spannable
    }


    /**
     * 设置页面 电话的样式
     */
    fun settingPhone1(textView: TextView?, content: String, title: String) {
        if (content.isNullOrEmpty() || title.isNullOrEmpty()) {
            return
        }
        var spannable = SpannableString(content + "\n" + title)

        //设置颜色
        var colorSpannable = ForegroundColorSpan(Color.parseColor("#71747B"))
        spannable.setSpan(
            colorSpannable,
            0,
            content.length,
            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        )

        //设置字体大小为1.3
        var sizeSpannable = RelativeSizeSpan(0.7f)
        spannable.setSpan(
            sizeSpannable, 0, content.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        )

        textView!!.text = spannable
    }


    /**
     * 每日签到 F币  成长值
     */
    fun signJf(textView: TextView?, num: String, title: String) {
        var spannable = SpannableString(num + "\n" + title)

        //设置颜色
        var colorSpannable = ForegroundColorSpan(Color.parseColor("#071726"))
        spannable.setSpan(
            colorSpannable,
            num.length,
            spannable.length,
            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        )

        //设置字体大小为1.3
        var sizeSpannable = RelativeSizeSpan(0.8f)
        spannable.setSpan(
            sizeSpannable, num.length, spannable.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        )

        textView!!.text = spannable
    }

    /**
     * 每日签到 F币  成长值
     */
    fun signAcc(textView: TextView?, title1: String, num: String, title2: String) {
        var spannable = SpannableString(title1 + num + title2)

        //设置颜色
        var colorSpannable = ForegroundColorSpan(Color.parseColor("#01025C"))
        spannable.setSpan(
            colorSpannable,
            title1.length,
            title1.length + num.length,
            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        textView!!.text = spannable
    }

    /**
     * 每日签到 U币  成长值
     */
    fun signJfMonth(textView: TextView?, num: String, title: String) {
        var spannable = SpannableString(num + "\n" + title)

        //设置颜色
        var colorSpannable = ForegroundColorSpan(Color.parseColor("#FFDE00"))
        spannable.setSpan(
            colorSpannable,
            num.length,
            spannable.length,
            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        )

        //设置字体大小为1.3
        var sizeSpannable = RelativeSizeSpan(0.8f)
        spannable.setSpan(
            sizeSpannable, num.length, spannable.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        )

        textView!!.text = spannable
    }

    /**
     * 每日签到 U币  成长值
     */
    fun signAccMonth(textView: TextView?, title1: String, num: String, title2: String) {
        var spannable = SpannableString(title1 + num + title2)

        //设置颜色
        var colorSpannable = ForegroundColorSpan(Color.parseColor("#FFDE00"))
        spannable.setSpan(
            colorSpannable,
            title1.length,
            title1.length + num.length,
            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        textView!!.text = spannable
    }


    /**
     * 点赞 统计
     */
    fun growUp(textView: TextView?, num: String, title: String) {
        var spannable = SpannableString(title + num)

        //设置颜色
        var colorSpannable = ForegroundColorSpan(Color.parseColor("#071726"))
        spannable.setSpan(
            colorSpannable,
            title.length,
            spannable.length,
            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        )

        //设置字体大小为1.3
        var sizeSpannable = RelativeSizeSpan(0.8f)
        spannable.setSpan(
            sizeSpannable, title.length, spannable.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        )

        textView!!.text = spannable
    }


    /**
     * 确认订单 底部描述
     */
    fun orderDes(textView: TextView?, total: String, date: String) {
        var spannable = SpannableString("总计：￥${total}\n有效期截止：${date}")

        //设置颜色
        var colorSpannable = ForegroundColorSpan(Color.parseColor("#FC883B"))
        spannable.setSpan(
            colorSpannable,
            3,
            total.length + 4,
            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        )

        //设置字体大小为1.3
        var sizeSpannable = RelativeSizeSpan(1.2f)
        spannable.setSpan(
            sizeSpannable, 4, total.length + 4, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        )

        textView!!.text = spannable
    }


    /**
     * 确认订单 底部设置U币
     */
    fun orderU(textView: TextView?, total: String, date: String) {
        var spannable = SpannableString("总计：${total}福币\n有效期截止：${date}")

        //设置颜色
        var colorSpannable = ForegroundColorSpan(Color.parseColor("#FC883B"))
        spannable.setSpan(
            colorSpannable,
            3,
            total.length + 5,
            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        )

        //设置字体大小为1.3
        var sizeSpannable = RelativeSizeSpan(1.2f)
        spannable.setSpan(
            sizeSpannable, 3, total.length + 3, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        )

        textView!!.text = spannable
    }

    /**
     * 确认订单 底部描述
     */
    fun payOrderPrice(textView: TextView?, total: String) {
        var spannable = SpannableString("￥${total}")

        //设置字体大小为1.3
        var sizeSpannable = RelativeSizeSpan(0.8f)
        spannable.setSpan(
            sizeSpannable, 0, 1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        )

        textView!!.text = spannable
    }

    /**
     * 确认订单 U币不足
     */
    fun payOrderU(textView: CheckBox?, integralDecimal: String) {
        var spannable = SpannableString("福币兑换   剩余${integralDecimal}币(福币不足)")

        //设置字体大小为1.3
        var colorSpannable = ForegroundColorSpan(Color.parseColor("#FF7635"))
        spannable.setSpan(
            colorSpannable, spannable.length - 6, spannable.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        textView!!.text = spannable
    }


    /**
     * 车辆认证状态
     */
    fun carAuthStatus(textView: TextView?, num: String, title: String) {
        var spannable = SpannableString(title + "\n" + num)
        //设置字体大小为0.7
        var sizeSpannable = RelativeSizeSpan(0.7f)
        spannable.setSpan(
            sizeSpannable, title.length, spannable.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        textView!!.text = spannable
    }

    /**
     * uni卡使用记录总计次数
     */
    fun uniUseTotalNum(textView: TextView?, num: String) {
        var spannable = SpannableString("总计：${num}次")
        //设置字体大小为0.7
        var sizeSpannable = RelativeSizeSpan(1.2f)
        spannable.setSpan(Typeface.BOLD, 3, spannable.length - 1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        spannable.setSpan(
            sizeSpannable, 3, spannable.length - 1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        textView!!.text = spannable
    }


    /**
     * uni卡使用记录节约金额
     */
    fun uniUseTotalPrice(textView: TextView?, num: String) {
        var spannable = SpannableString("节省：￥${num}")
        var colorSpannable = ForegroundColorSpan(Color.parseColor("#FC883B"))
        spannable.setSpan(
            colorSpannable, 3, spannable.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        textView!!.text = spannable
    }

    /**
     * 倒计时
     */
    fun date(dateBtn: TextView, t: Long = 60 * 1000): CountDownTimer {
        var countDownTimer = object : CountDownTimer(t, 300) {
            override fun onFinish() {
                dateBtn.text = "获取验证码"
                dateBtn.isEnabled = true
            }

            override fun onTick(millisUntilFinished: Long) {
                var hour = millisUntilFinished / 1000 / 60 / 60
                var minute = millisUntilFinished / 1000 / 60 % 60
                var second = millisUntilFinished / 1000 % 60
                dateBtn.text = second.toString() + "s"
                dateBtn.isEnabled = false
            }
        }

        return countDownTimer
    }


    /**
     * 粉丝挖掘倒计时
     */
    fun fansTime(dateBtn: TextView, t: Long = 60 * 1000): CountDownTimer {
        var countDownTimer = object : CountDownTimer(t, 300) {
            override fun onFinish() {
                dateBtn.text = "跳过"
                dateBtn.isEnabled = true
            }

            override fun onTick(millisUntilFinished: Long) {
                var hour = millisUntilFinished / 1000 / 60 / 60
                var minute = millisUntilFinished / 1000 / 60 % 60
                var second = millisUntilFinished / 1000 % 60
                dateBtn.text = "跳过 $second"
                dateBtn.isEnabled = false
            }
        }

        return countDownTimer
    }


    /**
     * 支付倒计时
     */
    fun orderPayTime(dateBtn: TextView, t: Long = 60 * 1000): CountDownTimer {
        var countDownTimer = object : CountDownTimer(t, 300) {
            override fun onFinish() {
                dateBtn.isEnabled = false
            }

            override fun onTick(millisUntilFinished: Long) {
                var hour = millisUntilFinished / 1000 / 60 / 60
                var minute = millisUntilFinished / 1000 / 60 % 60
                var second = millisUntilFinished / 1000 % 60
                dateBtn.text =
                    "${if (hour == 0L) "00" else hour}:${if (minute == 0L) "00" else minute}:" +
                            "${if (second == 0L) "00" else if (second < 10L) "0${second}" else second}"
            }
        }
        return countDownTimer
    }


    fun callPhone(activity: Activity, phone: String?) {
        phone?.let {
            // 拨号：激活系统的拨号组件
            CallPhoneDialog(activity, "您确定拨打:${phone}").setOnClickItemListener(
                object : CallPhoneDialog.OnClickItemListener {
                    override fun onClickItem(position: Int, str: String) {
                        val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:${phone}"))
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                        activity.startActivity(intent)
                    }
                }).show()
        }
    }


    /**
     * 验证手机号
     */
    fun isMobileNO(mobiles: String?): Boolean {
        val p = Pattern.compile("^[1]\\d{10}$")
        val m = p.matcher(mobiles)
        return m.matches()
    }

    /**
     * 验证邮箱
     */

    fun isEmail(email: String): Boolean {
        var p = Pattern.compile("\\w+([-+.]\\w+)*@\\w+([-.]\\w+)*\\.\\w+([-.]\\w+)*")
        var m = p.matcher(email)
        return m.matches()
    }

    fun compileExChar(str: String): Boolean {
        var limitEx: String = "[`~!@#$%^&*()+=|{}':;',\\[\\].<>/?~！@#￥%……&*（）——+|{}【】‘；：”“’。，、？]"
        var pattern = Pattern.compile(limitEx)
        var m = pattern.matcher(str)
        return m.find()
    }

    fun isIdcardCode(idCard: String): Boolean {
        val id_18 =
            "^[1-9][0-9]{5}(18|19|20)[0-9]{2}((0[1-9])|(10|11|12))(([0-2][1-9])|10|20|30|31)[0-9]{3}([0-9]|(X|x))"
        val id_15 =
            "^[1-9][0-9]{5}[0-9]{2}((0[1-9])|(10|11|12))(([0-2][1-9])|10|20|30|31)[0-9]{2}[0-9]"
        val id_valid = "($id_18)|($id_15)"
        var pattern = Pattern.compile(id_valid)
        var matcher = pattern.matcher(idCard)
        return matcher.matches()
    }

    fun dip2px(context: Context, dpValue: Double): Int {
        val density: Float = context.getResources().getDisplayMetrics().density
        return (dpValue * density + 0.5).toInt()
    }

    class EmojiInputFilter : InputFilter {
        override fun filter(
            source: CharSequence,
            start: Int,
            end: Int,
            dest: Spanned,
            dstart: Int,
            dend: Int
        ): CharSequence? {
            for (i in start until end) {
                val type = Character.getType(source[i])
                if (type == Character.SURROGATE.toInt() || type == Character.OTHER_SYMBOL.toInt()) {
                    "不能输入特殊字符".toast()
                    return ""
                }
            }
            return null
        }
    }


    /**
     * 计算两个时间是否相差五分钟
     */
    fun timeFiveS(startTime: Long, endTime: Long): Boolean {
        if (endTime - startTime > 5 * 60 * 1000) {
            return true
        }
        return false
    }

    fun hideStart(content: String): String {
        if (content.isNullOrEmpty() || content.length < 5) {
            return content
        }
        var startStr = content.substring(0, 2)
        var endStr = content.substring(content.length - 2, content.length)

        return "${startStr}****${endStr}"
    }

    /**
     * 上次图片地址拼接宽高
     */
    fun imgWandH(stsBean: STSBean, media: LocalMedia): String {

        var ytPath = PictureUtil.getFinallyPath(media)
        var type = ytPath.substring(ytPath.lastIndexOf(".") + 1, ytPath.length)

        var path =
            stsBean.tempFilePath + System.currentTimeMillis() + "androidios${
                if (media.width == 0) {
                    500
                } else {
                    media.width
                }
            }_${
                if (media.height == 0) {
                    500
                } else {
                    media.height
                }
            }." + type
        return path
    }


    /**
     *
     * @param background 背景图
     * @param foreground 前景图
     * @return
     */
    fun combineBitmap(background: Bitmap?, foreground: Bitmap): Bitmap? {
        if (background == null) {
            return null
        }
        val bgWidth = background.width
        val bgHeight = background.height
        val fgWidth = foreground.width
        val fgHeight = foreground.height
        val newmap = Bitmap.createBitmap(bgWidth, bgHeight, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(newmap)
        canvas.drawBitmap(background, 0F, 0F, null)
        canvas.drawBitmap(foreground, 450F, 580F, null) //设置二维码所在的位置 这个可以写死
        canvas.save()
        canvas.restore()
        return newmap
    }

    /**
     * Drawable转换成一个Bitmap
     *
     * @param drawable drawable对象
     * @return
     */
    fun drawableToBitmap(drawable: Drawable): Bitmap {
        val bitmap = Bitmap.createBitmap(
            drawable.intrinsicWidth, drawable.intrinsicHeight,
            if (drawable.opacity !== PixelFormat.OPAQUE) Bitmap.Config.ARGB_8888 else Bitmap.Config.RGB_565
        )
        val canvas = Canvas(bitmap)
        drawable.setBounds(0, 0, drawable.intrinsicWidth, drawable.intrinsicHeight)
        drawable.draw(canvas)
        return bitmap
    }


//    fun openMap(bean: DealerInfoItemBean, activity: Activity) {
//        SelectDialog(
//            activity,
//            com.changan.my.R.style.transparentFrameWindowStyle,
//            listMap,
//            "",
//            1,
//            SelectDialog.SelectDialogListener() { view: View, i: Int, dialogBottomBean: DialogBottomBean ->
//                when (dialogBottomBean.id) {
//                    1 -> JumpMap.openGaoDeMap(
//                        activity,
//                        bean.latY.toDouble(),
//                        bean.lngX.toDouble(),
//                        bean.dealerName
//                    )
//                    2 -> JumpMap.openBaiduMap(
//                        activity,
//                        bean.latY.toDouble(),
//                        bean.lngX.toDouble(),
//                        bean.dealerName
//                    )
//                }
//            }
//        ).show()
//    }


    /**
     * 注销账号后，删除内容
     */
    fun cancelAccountDeleteContent(textView: TextView) {

        var title: String = "以下数据将被清除："
        var content: String = "帖子数据、福币数据、圈子相关数据、经验等级数据、活动数据、勋章头像框数据"

        var spannableString = SpannableString(title + content)

        spannableString.setSpan(
            StyleSpan(Typeface.BOLD),
            title.length,
            spannableString.length,
            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        )

        textView.text = spannableString
    }


    /**
     * 注销账号，发送验证码提示语样式
     */
    fun cancelAccountHintContent(textView: TextView, phoneCode: String) {

        if (phoneCode.isNullOrEmpty()) {
            return
        }
        var title: String = "请用尾号"
        var content: String = "登录的手机号\n获取验证码进行注销"
        var p = phoneCode.substring(phoneCode.length - 4, phoneCode.length)

        var spannableString = SpannableString(title + p + content)

        var colorSpan = ForegroundColorSpan(Color.parseColor("#757593"))

        spannableString.setSpan(
            colorSpan,
            title.length,
            title.length + p.length,
            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        )

        textView.text = spannableString
    }

    /**
     * 车牌转码
     */
//    fun urlPlateNumEncode(plateNum: String?): String = SPUtils.urlPlateNumEncode(plateNum)

    /**
     * 获取绑定手机jumpDataType true跳转 false 不跳转
     * isSkipBindModel true:直接跳转绑定手机页面,默认不跳转
     */
    fun getBindMobileJumpDataType(isSkipBindMobile: Boolean = false): Boolean {
        UserDatabase.getUniUserDatabase(MyApp.mContext).getUniUserInfoDao().getNoLiveDataUser()
            ?.let {
                if (it.bindMobileJumpType == LiveDataBusKey.MINE_SIGN_OTHER_CODE) {
                    if (isSkipBindMobile) {
//                        "请先绑定手机号".toast()
                        JumpUtils.instans?.jump(18)
                    }
                    return true
                }
            }
        return false
    }

    fun getTodayTime(): String {
        val calendar: Calendar = Calendar.getInstance()
        //年
        val year = calendar[Calendar.YEAR]
        //月
        val month = calendar[Calendar.MONTH] + 1
        //日
        val day = calendar[Calendar.DAY_OF_MONTH]

        return "$year$month$day"
    }

    private var expand = "更多"
    private var collapse = "收起"

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
                ForegroundColorSpan(contentTextView.resources.getColor(com.changanford.common.R.color.color_1700f4)),
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
            ForegroundColorSpan(contentTextView.resources.getColor(com.changanford.common.R.color.color_1700f4)),
            ssb.length - collapse.length, ssb.length,
            Spanned.SPAN_INCLUSIVE_EXCLUSIVE
        )

        contentTextView.text = ssb
    }
}