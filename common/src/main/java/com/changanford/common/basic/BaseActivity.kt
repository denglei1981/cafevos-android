package com.changanford.common.basic

import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.viewbinding.ViewBinding
import com.changanford.common.basic.BaseApplication.Companion.curActivity
import com.gyf.immersionbar.ImmersionBar
import java.lang.reflect.ParameterizedType

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

    var isDarkFont=true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = bindings
        setContentView(binding.root)
        initViewModel()
        curActivity = this
        makeStateBarTransparent(true)
        initView()
        ImmersionBar.with(this).statusBarDarkFont(isDarkFont).init()
        initData()
    }

    override fun onResume() {
        super.onResume()
        curActivity = this
    }

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

    fun <T:ViewModel> createViewModel(claaz:Class<T>) =
        ViewModelProvider.AndroidViewModelFactory.getInstance(BaseApplication.INSTANT).create(claaz)


    /**
     * 设置状态栏透明 SDK_INT >= 21
     * @param isLightMode 是否是浅色模式，true= 状态栏文字为灰色，false = 状态栏文字白色 SDK_INT >= 23
     */
    fun makeStateBarTransparent(isLightMode: Boolean){
        if (Build.VERSION.SDK_INT< Build.VERSION_CODES.KITKAT){
            return
        }
        if(Build.VERSION.SDK_INT>= Build.VERSION_CODES.LOLLIPOP){
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            val option =
                window.decorView.systemUiVisibility or View.SYSTEM_UI_FLAG_LAYOUT_STABLE or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
            window.decorView.systemUiVisibility = option
            window.statusBarColor = Color.TRANSPARENT
        }else{
            window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
        }
        setLightMode(isLightMode)

    }

    private fun setLightMode(isLightMode:Boolean){
        if (Build.VERSION.SDK_INT>= Build.VERSION_CODES.M){//6.0设置状态栏字体颜色
            var option = window.decorView.systemUiVisibility
            option = if (isLightMode){
                option or View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
            }else{
                option and View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR.inv()
            }
            window.decorView.systemUiVisibility = option
        }
    }
}