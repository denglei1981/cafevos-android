package com.changanford.shop.control.time

import android.annotation.SuppressLint
import android.os.CountDownTimer
import com.changanford.shop.view.TypefaceTextView

/**
 * Author:wenke
 * Email:3158817509@qq.com
 * Create Time:2021/1/21
 * Update Time:
 * Note:倒计时
 */
class PayTimeCountControl (millisInFuture: Long, private val tv: TypefaceTextView, val listener: OnTimeCountListener) : CountDownTimer(millisInFuture, 1000) {
    @SuppressLint("SetTextI18n")
    override fun onTick(millisUntilFinished: Long) {
        val hour=millisUntilFinished/1000/60/60
        val minute=millisUntilFinished/1000/60%60
        val second=millisUntilFinished/1000%60
        val h=if(hour>9)"$hour" else "0$hour"
        val m=if(minute>9)"$minute" else "0$minute"
        val s=if(second>9)"$second" else "0$second"
        tv.setText("$h:$m:$s")
    }
    override fun onFinish() {
        listener.onFinish()
    }
    interface OnTimeCountListener {
        fun onFinish()
    }
}