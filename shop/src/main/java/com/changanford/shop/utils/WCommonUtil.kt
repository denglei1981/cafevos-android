package com.changanford.shop.utils

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.res.AssetManager
import android.graphics.Color
import android.graphics.LinearGradient
import android.graphics.Shader
import android.graphics.Typeface
import android.net.Uri
import android.os.Build
import android.text.*
import android.text.method.LinkMovementMethod
import android.util.TypedValue
import android.view.Gravity
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.TextView
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import com.changanford.shop.bean.EditTextBean
import com.google.android.material.tabs.TabLayout
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import java.math.BigDecimal
import java.math.RoundingMode
import java.text.DecimalFormat
import java.util.regex.Matcher
import java.util.regex.Pattern


/**
 * @Author : wenke
 * @Time : 2021/9/8 0008
 * @Description : WCommonUtils
 */
object WCommonUtil {
    /**
     * 设置tabLayout选择样式
     * [size]被选择的字体大小
     * [typeface]字体样式 Typeface.DEFAULT_BOLD
     * [colorID]被选择的字体的颜色值
     * */
    fun setTabSelectStyle(context: Context, tabLayout: TabLayout, size: Float, typeface: Typeface, colorID: Int) {
        val textView = TextView(context)
        val selectedSize = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_PX,size,context.resources.displayMetrics)
        textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, selectedSize)
        textView.setTextColor(ContextCompat.getColor(context, colorID))
        textView.typeface =typeface
        textView.gravity= Gravity.CENTER
        tabLayout.getTabAt(0)?.apply {
            customView=null
            textView.text = text
            customView = textView
        }
        tabLayout.addOnTabSelectedListener(object : TabLayout.BaseOnTabSelectedListener<TabLayout.Tab> {
            override fun onTabReselected(tab: TabLayout.Tab?) {}
            override fun onTabUnselected(tab: TabLayout.Tab?) {
                tab?.customView = null
            }

            override fun onTabSelected(tab: TabLayout.Tab?) {
                tab?.apply {
                    customView=null
                    textView.text = text
                    customView = textView
                }
            }
        })
    }
    /**
     * 是否开启通知
    * */
    fun isNotificationEnabled(context: Context): Boolean {
        return try {
            NotificationManagerCompat.from(context).areNotificationsEnabled()
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }
    /**
     * 去系统开启通知
    * */
    fun toSetNotice(context:Context) {
        val intent = Intent()
        when {
            Build.VERSION.SDK_INT >= 26 -> {
                // android 8.0引导
                intent.action = "android.settings.APP_NOTIFICATION_SETTINGS"
                intent.putExtra("android.provider.extra.APP_PACKAGE", context.packageName)
            }
            Build.VERSION.SDK_INT >= 21 -> {
                // android 5.0-7.0
                intent.action = "android.settings.APP_NOTIFICATION_SETTINGS"
                intent.putExtra("app_package", context.packageName)
                intent.putExtra("app_uid", context.applicationInfo.uid)
            }
            else -> {
                // 其他
                intent.action = "android.settings.APPLICATION_DETAILS_SETTINGS"
                intent.data = Uri.fromParts("package", context.packageName, null)
            }
        }
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        context.startActivity(intent)
    }
    /**
     * 扩展函数简化了将afterTextChanged操作设置为EditText组件。
     */
    fun EditText.onTextChanged(onTextChanged: (EditTextBean) -> Unit) {
        this.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(editable: Editable?) {}
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                onTextChanged.invoke(EditTextBean(s,start,before,count))
            }
        })
    }
    /**
     * 设置textview 的颜色渐变
     * @param text
     */
    fun setTextViewStyles(text: TextView,startColor:String,endColor:String){
        val mLinearGradient = LinearGradient(0f, 0f, 0f, text.paint.textSize, Color.parseColor(startColor), Color.parseColor(endColor),
            Shader.TileMode.CLAMP)
        text.paint.shader = mLinearGradient
        text.invalidate()
    }
    /**
     * html空格字符处理
     * */
    fun htmlToStr(str:String):String{
        return "${htmlToString(str).trimEnd()}".replace("\n\n","\n")
    }
    /**
     * 将html转为str
     * */
    private fun htmlToString(str: String): Spanned {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            Html.fromHtml(str, Html.FROM_HTML_MODE_LEGACY)
        } else {
            Html.fromHtml(str)
        }
    }
    /**
     * 将html转为str
     * */
    fun htmlToString(textView: TextView,str: String) {
        textView.text= if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            Html.fromHtml(str, Html.FROM_HTML_MODE_LEGACY)
        } else {
            Html.fromHtml(str)
        }
    }
    /**
     * 将图文混合html转为str
     * */
    fun htmlToImgStr(mActivity: Activity, textView: TextView, str: String?){
        textView.movementMethod = LinkMovementMethod.getInstance()//可点击
        textView.text= when {
            TextUtils.isEmpty(str) -> ""
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.N -> Html.fromHtml(str, Html.FROM_HTML_MODE_LEGACY, MImageGetter(textView, mActivity), MyTagHandler(mActivity))
            else -> Html.fromHtml(str, MImageGetter(textView, mActivity), MyTagHandler(mActivity))
        }
    }
    /**
     * 读取 assets json文件
     * */
    fun getAssetsJson(fileName: String, context: Context): String {
        //将json数据变成字符串
        val stringBuilder = StringBuilder()
        try {
            //获取assets资源管理器
            val assetManager: AssetManager = context.assets
            //通过管理器打开文件并读取
            val bf = BufferedReader(
                InputStreamReader(
                    assetManager.open(fileName)
                )
            )
            var line: String?
            while (bf.readLine().also { line = it } != null) {
                stringBuilder.append(line)
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return stringBuilder.toString()
    }
    /**
     * 隐藏软键盘
     * @param context :上下文
     * @param view    :一般为EditText
     */
    fun hideKeyboard(view: View) {
        val manager = view.context
            .getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        manager.hideSoftInputFromWindow(view.windowToken, 0)
    }
    /**
     * 显示软键盘
     * @param context :上下文
     * @param view    :一般为EditText
     */
    fun showKeyboard(v: View) {
        v.isFocusable = true
        v.isFocusableInTouchMode = true
        v.requestFocus()
        val imm =
            v.context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.showSoftInput(v, InputMethodManager.SHOW_FORCED)
    }
    /**
     * 格式化数字（.00表示保留两位小数 不四舍五入）
     * */
    fun getHeatNum(heat: Double): String {
        val df = DecimalFormat("0.00")
        df.roundingMode = RoundingMode.DOWN

        return df.format(heat)
    }
    /**
     * [newScale]几位小数
     * */
    fun getHeatNum(number:String,newScale:Int): BigDecimal {
        return BigDecimal(number).setScale(newScale, BigDecimal.ROUND_DOWN)
    }
    /**
     *以百分比方式计数 并保留两位小数
     * */
    fun getPercentage(number:Double):String {
       return DecimalFormat("0.00%").format(number)
    }
    /**
     *以百分比方式计数
     * s1分子
     * s2分母
     * */
    fun getPercentage(s1:Double,s2:Double):String {
        return if(s2>0) DecimalFormat("0%").format(s1/s2)
        else "0%"
    }
    /**
     *以百分比方式计数
     * s1分子
     * s2分母
     * 向下取整
     * */
    fun getPercentage(s1:Double,s2:Double,newScale:Int):String {
        return if(s2>0) "${getHeatNum("${s1/s2*100}",newScale)}%"
        else "0%"
    }
    /**
     * 禁止EditText输入特殊字符([`~!@#$%^&*()+=|{}':;',\[\].<>/?~！@#￥%……&*（）——+|{}【】‘；：”“’。，、？])
     * @param editText
     */
    fun setEditTextInhibitInputSpeChat(editText: EditText,speChat:String?="[`~!@#$%^&*()+=|{}':;',\\[\\].<>/?~！@#￥%……&*（）——+|{}【】‘；：”“’。，、？]") {
        val filter = InputFilter { source, _, _, _, _, _ ->
            val pattern: Pattern = Pattern.compile(speChat)
            val matcher: Matcher = pattern.matcher(source.toString())
            if (matcher.find()) "" else null
        }
        editText.filters = arrayOf(filter)
    }
    /**
     * 格式化手机号 中间四位加*
    * */
    fun formatMobilePhone(mobile:String?):String{
        mobile?.apply {
          if(length>=7){
              return "${substring(0,3)}****${substring(length-4,length)}"
          }
        }
        return mobile?:""
    }
}