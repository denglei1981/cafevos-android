package com.changanford.shop.utils

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.LinearGradient
import android.graphics.Shader
import android.graphics.Typeface
import android.net.Uri
import android.os.Build
import android.text.Editable
import android.text.TextWatcher
import android.util.TypedValue
import android.view.Gravity
import android.widget.EditText
import android.widget.TextView
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import com.changanford.shop.bean.EditTextBean
import com.google.android.material.tabs.TabLayout


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
        val tab= tabLayout.getTabAt(0)
        val textView = TextView(context)
        val selectedSize = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_PX,size,context.resources.displayMetrics)
        textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, selectedSize)
        textView.setTextColor(ContextCompat.getColor(context, colorID))
        textView.typeface =typeface
        textView.text = tab!!.text
        textView.gravity= Gravity.CENTER
        tab.customView = textView
        tabLayout.addOnTabSelectedListener(object : TabLayout.BaseOnTabSelectedListener<TabLayout.Tab> {
            override fun onTabReselected(tab: TabLayout.Tab?) {}
            override fun onTabUnselected(tab: TabLayout.Tab?) {
                tab?.customView = null
            }

            override fun onTabSelected(tab: TabLayout.Tab?) {
                textView.text = tab!!.text
                tab.customView = textView
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
}