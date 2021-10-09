package com.changanford.common.util

import android.content.Context
import android.os.IBinder
import android.view.MotionEvent
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import com.changanford.common.MyApp

/**
 * @Author: lcw
 * @Date: 2020/8/18
 * @Des:
 */
object HideKeyboardUtil {
    /**
     * 根据EditText所在坐标和用户点击的坐标相对比，来判断是否隐藏键盘，因为当用户点击EditText时则不能隐藏
     *
     * @param v
     * @param event
     * @return
     */
    fun isShouldHideKeyboard(
        v: View?,
        event: MotionEvent
    ): Boolean {
        if (v != null && v is EditText) {  //判断得到的焦点控件是否包含EditText
            val l = intArrayOf(0, 0)
            v.getLocationInWindow(l)
            val left = l[0]
            //得到输入框在屏幕中上下左右的位置
            val top = l[1]
            val bottom = top + v.getHeight()
            val right = left + v.getWidth()
            return !(event.x > left && event.x < right && event.y > top && event.y < bottom)
        }
        // 如果焦点不是EditText则忽略
        return false
    }

    /**
     * 获取InputMethodManager，隐藏软键盘
     * @param token
     */
    fun hideKeyboard(token: IBinder?) {
        if (token != null) {
            val im: InputMethodManager =
                MyApp.mContext.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            im.hideSoftInputFromWindow(token, InputMethodManager.HIDE_NOT_ALWAYS)
        }
    }

    /**
     * 强制打开软键盘
     */
    fun showSoftInput(view: View) {
        val imm: InputMethodManager =
                view.context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        view.requestFocus()
        imm.showSoftInput(view, 0)
    }
}