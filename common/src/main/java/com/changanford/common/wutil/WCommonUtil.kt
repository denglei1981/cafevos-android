package com.changanford.common.wutil

import android.Manifest
import android.annotation.TargetApi
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.AssetManager
import android.graphics.*
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Build
import android.provider.Settings
import android.text.*
import android.text.method.LinkMovementMethod
import android.util.Log
import android.util.TypedValue
import android.view.Gravity
import android.view.View
import android.view.View.DRAWING_CACHE_QUALITY_HIGH
import android.view.ViewGroup.MarginLayoutParams
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.SimpleTarget
import com.bumptech.glide.request.transition.Transition
import com.changanford.common.BuildConfig
import com.changanford.common.R
import com.changanford.common.bean.EditTextBean
import com.changanford.common.listener.OnDownBitmapListener
import com.changanford.common.ui.dialog.AlertDialog
import com.changanford.common.util.AppUtils
import com.changanford.common.util.ConfigUtils
import com.changanford.common.util.LocationServiceUtil
import com.changanford.common.utilext.toast
import com.google.android.material.tabs.TabLayout
import com.qw.soul.permission.SoulPermission
import com.tencent.mm.opensdk.modelmsg.SendMessageToWX
import com.tencent.mm.opensdk.modelmsg.WXMediaMessage
import com.tencent.mm.opensdk.modelmsg.WXMiniProgramObject
import com.tencent.mm.opensdk.openapi.WXAPIFactory
import java.io.BufferedReader
import java.io.ByteArrayOutputStream
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
     * [label] 自定义的标签 如 myfont
     * */
    fun htmlToImgStr(mActivity: Activity, textView: TextView, str: String?,label:String?=null){
        textView.movementMethod = LinkMovementMethod.getInstance()//可点击
        textView.text= when {
            TextUtils.isEmpty(str) -> ""
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.N -> Html.fromHtml(str, Html.FROM_HTML_MODE_LEGACY, MImageGetter(textView, mActivity), MyTagHandler(mActivity,label))
            else -> Html.fromHtml(str, MImageGetter(textView, mActivity), MyTagHandler(mActivity,label))
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
     * 四舍五入
     * [newScale]几位小数
    * */
    fun getRoundedNum(str: String?,newScale:Int=0):BigDecimal {
        return BigDecimal(str?:"0").setScale(newScale,BigDecimal.ROUND_HALF_UP)
    }
    /**
     * 向下
     * [newScale]几位小数
     * */
    fun getHeatNum(number:String,newScale:Int=0): BigDecimal {
        return BigDecimal(number).setScale(newScale, BigDecimal.ROUND_DOWN)
    }
    /**
     * [newScale]几位小数 向上取
     * */
    fun getHeatNumUP(number:String,newScale:Int=0): BigDecimal {
        return BigDecimal(number).setScale(newScale, BigDecimal.ROUND_UP)
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
    /**
     * 设置控件的Margin
    * */
    fun setMargin(v: View, l: Int, t: Int, r: Int, b: Int) {
        if (v.layoutParams is MarginLayoutParams) {
            val p = v.layoutParams as MarginLayoutParams
            p.setMargins(l, t, r, b)
            v.requestLayout()
        }
    }
    /**
    *新建Bitmap，将View中内容绘制到Bitmap上
    * */
    fun createBitmapFromView(view: View): Bitmap? {
        //是ImageView直接获取
        if (view is ImageView) {
            val drawable: Drawable = view.drawable
            if (drawable is BitmapDrawable) {
                return drawable.bitmap
            }
        }
        view.clearFocus()
        val bitmap = Bitmap.createBitmap(view.width, view.height, Bitmap.Config.ARGB_8888)
        if (bitmap != null) {
            val canvas = Canvas(bitmap)
            view.draw(canvas)
            canvas.setBitmap(null)
        }
        return bitmap
    }
    /**
     * 该方式原理主要是：View组件显示的内容可以通过cache机制保存为bitmap
     */
    fun createBitmapFromViewFromCache(view: View): Bitmap? {
        var bitmap: Bitmap? = null
        //开启view缓存bitmap
        view.isDrawingCacheEnabled = true
        //设置view缓存Bitmap质量
        view.drawingCacheQuality = DRAWING_CACHE_QUALITY_HIGH
        //获取缓存的bitmap
        val cache = view.drawingCache
        if (cache != null && !cache.isRecycled) {
            bitmap = Bitmap.createBitmap(cache)
        }
        //销毁view缓存bitmap
        view.destroyDrawingCache()
        //关闭view缓存bitmap
        view.isDrawingCacheEnabled = false
        return bitmap
    }
    /**
     * 打开定位
    * */
    fun showLocationServicePermission(activity: Activity) {
        // 没有打开定位服务。
        LocationServiceUtil.openCurrentAppSystemSettingUI(activity)
        val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
        activity.startActivityForResult(intent, 0x436)
    }
    /**
     * 创建定位获取权限对话框
     * 去设置中心给应用设置定位权限
     * */
    fun setSettingLocation(context: Context) {
        AlertDialog(context).builder()
            .setTitle("提示")
            .setMsg("您已禁止了定位权限，请到设置中心去打开")
            .setNegativeButton("取消") { }.setPositiveButton("确定"
            ) { SoulPermission.getInstance().goApplicationSettings() }.show()
    }
    fun pathUrlToBitmap(context: Context,url:String,listener: OnDownBitmapListener){
        Glide.with(context).asBitmap().load(url).into(object : SimpleTarget<Bitmap?>() {
            override fun onResourceReady(bitmap: Bitmap, transition: Transition<in Bitmap?>?) {
                listener.onFinish(bitmap)
            }
        })
    }
    /**
    * 把Bitmap转Byte
    */
    fun bitmap2Bytes(bm: Bitmap): ByteArray {
        val byteArrayOutputStream = ByteArrayOutputStream()
        bm.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream)
        return byteArrayOutputStream.toByteArray()
    }
    /**
     * 小程序分享
     * [webpageUrl]兼容低版本的网页链接 限制长度不超过 10KB
     * [miniprogramType]正式版:0，测试版:1，体验版:2
     * [userName]小程序原始id（gh_d43f693ca31f）
     * [path]小程序页面路径；对于小游戏，可以只传入 query 部分，来实现传参效果，如：传入 "?foo=bar"
     * [title] 小程序消息title
     * [description]小程序消息desc
     * [thumbData] 小程序消息封面图片，小于128k ByteArray(byte[])
     */
    fun shareSmallProgram(context:Context,webpageUrl:String,miniprogramType:Int,userName:String,path:String,title:String,description:String,thumbData:ByteArray) {
        if(BuildConfig.DEBUG){
            Log.e("wenke","小程序分享：webpageUrl：$webpageUrl>>>miniprogramType:$miniprogramType>>>userName:$userName>>>path:$path")
            Log.e("wenke","小程序分享：title：$title>>>description:$description>>>thumbData:$thumbData")
        }
        if (!AppUtils.isWeixinAvilible(context)) {
            context.getString(R.string.str_pleaseInstallWechatBeforeUsingIt).toast()
            return
        }
        val api = WXAPIFactory.createWXAPI(context, ConfigUtils.WXAPPID)
        val miniProgramObj = WXMiniProgramObject().apply {
            this.webpageUrl = webpageUrl // 兼容低版本的网页链接
//                       this.miniprogramType =WXMiniProgramObject.MINIPTOGRAM_TYPE_RELEASE // 正式版:0，测试版:1，体验版:2
            this.miniprogramType =miniprogramType
            this.userName = userName // 小程序原始id
            this.path =path //小程序页面路径；对于小游戏，可以只传入 query 部分，来实现传参效果，如：传入 "?foo=bar"
        }
        val msg = WXMediaMessage(miniProgramObj)
        msg.title = title // 小程序消息title
        msg.description = description // 小程序消息desc
        msg.thumbData = thumbData // 小程序消息封面图片，小于128k
        val req = SendMessageToWX.Req()
//        req.transaction = buildTransaction("miniProgram")
        req.transaction =System.currentTimeMillis().toString()
        req.message = msg
        req.scene = SendMessageToWX.Req.WXSceneSession // 目前只支持会话
        api.sendReq(req)
    }
    @TargetApi(23)
    fun isGetLocation(activity: Activity?): Boolean {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            // 定位精确位置
            activity?.apply {
                if (checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) return false
                if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED)return false
            }
            return true
        }
        return false
    }
    /**
     * 将福币转换为人民币 1元=100福币
     * */
    fun getRMB(fb:String?,unit:String?="¥"):String{
        var rmbPrice="0"
        if(fb!=null){
            val fbToFloat=fb.toFloat()
            val remainder=fbToFloat%100
            rmbPrice = if(remainder>0) "${fbToFloat/100}"
            else "${fb.toInt()/100}"
        }
        return "${unit?:""}$rmbPrice"
    }
}
@Synchronized
fun String.wLogE(tag:String?="wenke",isShow:Boolean=BuildConfig.DEBUG) {
    if(isShow)Log.e(tag,this)
}