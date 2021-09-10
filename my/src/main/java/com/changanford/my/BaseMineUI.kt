package com.changanford.my

import android.graphics.Color
import android.os.Bundle
import androidx.lifecycle.ViewModel
import androidx.viewbinding.ViewBinding
import com.changanford.common.basic.BaseActivity
import com.changanford.common.utilext.StatusBarUtil

/**
 *  文件名：BaseMineUI
 *  创建者: zcy
 *  创建日期：2021/9/9 16:05
 *  描述: TODO
 *  修改描述：TODO
 */
abstract class BaseMineUI<VB : ViewBinding, VM : ViewModel> : BaseActivity<VB, VM>() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        StatusBarUtil.setColor(this, Color.RED)
    }
}