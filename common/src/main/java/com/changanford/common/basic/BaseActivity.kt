package com.changanford.common.basic

import android.app.ActivityManager
import android.app.ActivityManager.RunningAppProcessInfo
import android.content.Context
import android.content.pm.ActivityInfo
import android.content.res.Configuration
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.os.IBinder
import android.util.DisplayMetrics
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewModelScope
import androidx.viewbinding.ViewBinding
import com.changanford.common.R
import com.changanford.common.basic.BaseApplication.Companion.curActivity
import com.changanford.common.basic.BaseApplication.Companion.currentViewModelScope
import com.changanford.common.util.FastClickUtils
import com.changanford.common.util.MConstant
import com.changanford.common.util.SPUtils
import com.changanford.common.util.bus.LiveDataBus
import com.changanford.common.util.bus.LiveDataBusKey
import com.gyf.immersionbar.ImmersionBar
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.lang.reflect.ParameterizedType
import java.util.Timer
import java.util.TimerTask

/**********************************************************************************
 * @Copyright (C), 2018-2020.
 * @FileName: com.changanford.common.basic.BaseActivity
 * @Author:　 　
 * @Version : V1.0
 * @Date: 2021/6/21 11:30
 * @Description: Activity基类，传入layout的ViewBinding,ViewModel
 * *********************************************************************************
 */
abstract class BaseActivity<VB : ViewBinding, VM : ViewModel> : AppCompatActivity(), BaseInterface {
    lateinit var binding: VB
    lateinit var viewModel: VM
    var isDarkFont = true
    var isPortrait: Boolean = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //防止应用截屏
        //window.setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE)
        MConstant.classesMap[getClassName()] = System.currentTimeMillis()
        curActivity = this
        binding = bindings
        setContentView(binding.root)
        initViewModel()
        handleTextSize()
//        makeStateBarTransparent(true)
        initView(savedInstanceState)
        initView()
//        StatusBarUtil.setLightStatusBar(this, true)
        ImmersionBar.with(this).statusBarDarkFont(isDarkFont).navigationBarColor(R.color.white).init()
        if (savedInstanceState == null) {
            initData()
        } else {
            FastClickUtils.relaunchApp()
        }
        observe()
    }

    private fun handleTextSize() {
        // 加载系统默认设置，字体不随用户设置变化
        var res = super.getResources()
        var config = Configuration()
        config.setToDefaults()
        var metrics = DisplayMetrics()
        var manager = curActivity?.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        manager.defaultDisplay.getRealMetrics(metrics)
        metrics.scaledDensity = config.fontScale * metrics.density
        res.updateConfiguration(config, metrics)
    }

    private fun getClassName() = javaClass.simpleName
    override fun initView(savedInstanceState: Bundle?) {
        if (isPortrait) {
            requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        }

    }

    override fun onResume() {
        super.onResume()
        curActivity = this
        try {
            currentViewModelScope = (curActivity as BaseActivity<*, *>).viewModel.viewModelScope
        } catch (e: Exception) {
            e.printStackTrace()
        }
        if (SPUtils.getParam(this, "isPopAgreement", true) as Boolean){
            return
        }
        //全局监听回到前台
        try {
            isOnStop_b = false
            isOnResume_b = true
            if (!isForeground_b) {
                //由后台切换到前台
                isForeground_b = true
                LiveDataBus.get().with(LiveDataBusKey.HOME_UPDATE).postValue(true)
            }
            timer_b = Timer()
            timerTask_b = MyTimerTask()
            timer_b?.schedule(timerTask_b, 50, 5000)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    open fun observe() {}

    private val bindings: VB by lazy {
        val type = javaClass.genericSuperclass as ParameterizedType
        val aClass = type.actualTypeArguments[0] as Class<*>
        val method = aClass.getDeclaredMethod("inflate", LayoutInflater::class.java)
        method.invoke(null, layoutInflater) as VB
    }

    private fun initViewModel() {
        var vmClass =
            (this.javaClass.genericSuperclass as ParameterizedType).actualTypeArguments[1] as Class<VM>
        viewModel = ViewModelProvider(this).get(vmClass)
    }

    fun <T : ViewModel> createViewModel(claaz: Class<T>) =
        ViewModelProvider.AndroidViewModelFactory.getInstance(BaseApplication.INSTANT).create(claaz)

    /**
     * 设置状态栏透明 SDK_INT >= 21
     * @param isLightMode 是否是浅色模式，true= 状态栏文字为灰色，false = 状态栏文字白色 SDK_INT >= 23
     */
    fun makeStateBarTransparent(isLightMode: Boolean) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) {
            return
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            val option =
                window.decorView.systemUiVisibility or View.SYSTEM_UI_FLAG_LAYOUT_STABLE or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
            window.decorView.systemUiVisibility = option
            window.statusBarColor = Color.TRANSPARENT
        } else {
            window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
        }
        setLightMode(isLightMode)

    }

    private fun setLightMode(isLightMode: Boolean) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {//6.0设置状态栏字体颜色
            var option = window.decorView.systemUiVisibility
            option = if (isLightMode) {
                option or View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
            } else {
                option and View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR.inv()
            }
            window.decorView.systemUiVisibility = option
        }
    }

//    override fun dispatchTouchEvent(ev: MotionEvent): Boolean {
//        if (ev.action == MotionEvent.ACTION_DOWN) {
//            val v = currentFocus
//            if (isShouldHideKeyboard(v, ev)) {
//                hideKeyboard(v!!.windowToken)
//            }
//        }
//        return super.dispatchTouchEvent(ev)
//    }

    private fun isShouldHideKeyboard(v: View?, event: MotionEvent): Boolean {
        if (v != null && v is EditText) {
            val l = intArrayOf(0, 0)
            v.getLocationInWindow(l)
            val left = l[0]
            val top = l[1]
            val bottom = top + v.getHeight()
            val right = left + v.getWidth()
            return !(event.x > left && event.x < right && event.y > top && event.y < bottom)
        }
        // 如果焦点不是EditText则忽略，这个发生在视图刚绘制完，第一个焦点不在EditText上，和用户用轨迹球选择其他的焦点
        return false
    }

    /**
     * 获取InputMethodManager，隐藏软键盘
     *
     * @param token
     */
    protected fun hideKeyboard(token: IBinder?) {
        if (token != null) {
            val im = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
            im.hideSoftInputFromWindow(token, InputMethodManager.HIDE_NOT_ALWAYS)
        }
    }

    /**
     * 判断app是否处于前台
     *
     * @return
     */
    open fun isAppOnForeground(): Boolean {
        val activityManager = applicationContext
            .getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        val packageName = applicationContext.packageName

        /**
         * 获取Android设备中所有正在运行的App
         */
        val appProcesses = activityManager
            .runningAppProcesses ?: return false
        for (appProcess in appProcesses) {
            // The name of the process that this object is associated with.
            if (appProcess.processName == packageName && appProcess.importance == RunningAppProcessInfo.IMPORTANCE_FOREGROUND) {
                return true
            }
        }
        return false
    }

    companion object{
        //全局监听后台运行
        var isForeground_b = true
        var timer_b: Timer? = null
        var isOnResume_b = false
        var isOnStop_b = false
        var timerTask_b: MyTimerTask? = null
        open fun isAppOnForeground(): Boolean {
            val activityManager = curActivity.applicationContext
                .getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
            val packageName = curActivity.applicationContext.packageName

            /**
             * 获取Android设备中所有正在运行的App
             */
            val appProcesses = activityManager
                .runningAppProcesses ?: return false
            for (appProcess in appProcesses) {
                // The name of the process that this object is associated with.
                if (appProcess.processName == packageName && appProcess.importance == RunningAppProcessInfo.IMPORTANCE_FOREGROUND) {
                    return true
                }
            }
            return false
        }
        class MyTimerTask : TimerTask() {
            override fun run() {
                if (!isAppOnForeground()) {
                    //由前台切换到后台
                    isForeground_b = false
                    curActivity.lifecycleScope.launch(Dispatchers.Main) {
//                        "${curActivity.resources.getString(R.string.app_name)}App已经进入后台".toast()
                    }
                    timer_b?.cancel()
                    timer_b = null
                    timerTask_b?.cancel()
                    timerTask_b = null
                }
            }
        }
    }


}