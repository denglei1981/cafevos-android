package com.changanford.common.basic

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.viewbinding.ViewBinding
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
    companion object{
        lateinit var curActivity:Context
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = bindings
        setContentView(binding.root)
        initViewModel()
        curActivity = this
        initView()
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
}