package com.changanford.home.base
import android.content.Context
import android.view.Gravity
import android.view.WindowManager
import androidx.appcompat.app.AppCompatDialog
import com.luck.picture.lib.tools.ScreenUtils


abstract class BaseAppCompatDialog(context: Context) : AppCompatDialog(context) {






    abstract fun initAd()


    override fun onStart() {
        super.onStart()
        val wl: WindowManager.LayoutParams? = window?.attributes
        window?.setBackgroundDrawableResource(android.R.color.transparent)
        wl?.width = WindowManager.LayoutParams.MATCH_PARENT
        wl?.height = WindowManager.LayoutParams.MATCH_PARENT
        wl?.alpha = 1f // 设置对话框的透明度,1f不透明
        wl?.gravity = Gravity.BOTTOM //设置显示在中间
        window?.attributes = wl
        initAd()
    }

    override fun dismiss() {
        super.dismiss()

    }




}