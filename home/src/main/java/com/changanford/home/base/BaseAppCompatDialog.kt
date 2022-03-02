package com.changanford.home.base

import android.content.Context
import android.graphics.Point
import android.os.Build
import android.view.Gravity
import android.view.WindowManager
import androidx.appcompat.app.AppCompatDialog
import com.changanford.common.MyApp


abstract class BaseAppCompatDialog(context: Context,var gravity: Int=Gravity.BOTTOM) : AppCompatDialog(context) {


    abstract fun initAd()


    override fun onStart() {
        super.onStart()
        val wl: WindowManager.LayoutParams? = window?.attributes
        window?.setBackgroundDrawableResource(android.R.color.transparent)
        wl?.width = getScreenWidth()
        wl?.height = WindowManager.LayoutParams.WRAP_CONTENT
        wl?.alpha = 1f // 设置对话框的透明度,1f不透明
        wl?.gravity = gravity //设置显示在中间
        window?.attributes = wl
        initAd()
    }

    override fun dismiss() {
        super.dismiss()

    }

    private fun getScreenWidth(): Int {
        val wm = MyApp.mContext.getSystemService(Context.WINDOW_SERVICE) as WindowManager
            ?: return -1
        val point = Point()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            wm.defaultDisplay.getRealSize(point)
        } else {
            wm.defaultDisplay.getSize(point)
        }
        return point.x
    }


}