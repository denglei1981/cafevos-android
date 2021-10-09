package com.changanford.common.util.toast

import android.annotation.SuppressLint
import android.content.Context
import android.view.Gravity
import android.view.View
import android.widget.Toast
import com.changanford.common.MyApp

/**
 * @Author: lcw
 * @Date: 2020/8/4
 * @Des: 可以复用的Toast，不会因为多次点击一直弹出或其他问题 引用APP的Context 防止内存泄露
 */
object ToastUtils {
    private var mToast: Toast? = null  //toast样式
    private var mToastView: ToastView? = null  //自定义view
    private var mToastGravity: Int = -1  //位置

    /**
     * 弹出提示
     * @param msg  提示信息
     * @param time  显示时间
     */
    @SuppressLint("ShowToast")
    fun showToast(msg: String?, time: Int, context: Context?) {
        if (mToast == null) {
            mToastView = ToastView(context!!)
            mToast = Toast.makeText(context, msg, time)
            mToast!!.view = mToastView
            mToastView!!.setText(msg!!)
        } else {
            mToastView!!.setText(msg!!)
            mToast!!.duration = time
        }
        if (mToastGravity != -1) {
            mToast!!.setGravity(mToastGravity, 0, 0)
        }

        //不设置的话，最高显示到状态栏下面
        mToast?.view?.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
        mToast?.show()
//        resetToast()
    }

    /**
     * 弹出提示信息
     * @param msgId  提示信息id
     * @param time  显示时间
     */
    fun showToast(msgId: Int, time: Int, context: Context?) {
        showToast(context?.getString(msgId), time, context)
    }

    /**
     * 弹出短时间提示
     * @param msg  提示信息
     */
    fun showShortToast(msg: String, context: Context?) {
        setToastGravity(Gravity.CENTER)
        showToast(msg, Toast.LENGTH_SHORT, context)
    }

    fun showShortToast(msgId: Int, context: Context?) {
        showToast(msgId, Toast.LENGTH_SHORT, context)
    }

    /**
     * 弹出长时间提示
     * @param msg  提示信息
     */
    fun showLongToast(msg: String, context: Context?) {
        showToast(msg, Toast.LENGTH_LONG, context)
    }

    /**
     * 关闭当前Toast
     */
    fun cancelCurrentToast() {
        if (mToast != null) {
            mToast!!.cancel()
        }
    }

    fun reToast(msg: String) {
        Toast.makeText(MyApp.mContext, msg, Toast.LENGTH_SHORT).show()
    }

    fun reToast(msgId: Int) {
        Toast.makeText(MyApp.mContext, msgId, Toast.LENGTH_SHORT).show()
    }

    fun setToastView(context: Context?) {
        mToastView = ToastView(context!!)
    }

    fun setToastGravity(gravity: Int) {
        mToastGravity = gravity
    }

    /**
     * 重置toast 信息
     */
    fun resetToast() {
        mToastView = null
        mToastGravity = -1
        mToast = null
    }

    /**
     * 显示
     */
    fun showToastCenter(context:Context,msg: String) {
        if (mToast == null) {
            mToast = Toast.makeText(context, msg, Toast.LENGTH_SHORT)
        } else {
            mToast?.duration = Toast.LENGTH_SHORT
            mToastView?.setText(msg)
        }
        mToast?.setGravity(Gravity.CENTER, 0, 0)
        mToast?.show()
    }
}