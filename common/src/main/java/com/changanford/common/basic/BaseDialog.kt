package com.changanford.common.basic

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import androidx.appcompat.app.AlertDialog
import androidx.viewbinding.ViewBinding
import com.changanford.common.R
import java.lang.reflect.ParameterizedType

/**********************************************************************************
 * @Copyright (C), 2020-2021.
 * @FileName: com.changanford.common.basic.BaseDialog
 * @Author:　 　
 * @Version : V1.0
 * @Date: 2021/7/6 09:06
 * @Description: 　
 * *********************************************************************************
 */
abstract class BaseDialog<VB : ViewBinding>(context: Context) :
    AlertDialog(context, R.style.DialogStyle),
    BaseInterface {

    override fun initView(savedInstanceState: Bundle?) {

    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        initView()
        initData()
    }

    val binding: VB by lazy {
        val type = javaClass.genericSuperclass as ParameterizedType
        val clazz = type.actualTypeArguments[0] as Class<*>
        val method = clazz.getDeclaredMethod("inflate", LayoutInflater::class.java)
        method.invoke(null, layoutInflater) as VB
    }
}