package com.changanford.shop.control.time

import android.annotation.SuppressLint
import android.os.CountDownTimer
import android.widget.TextView
import com.changanford.shop.listener.OnTimeCountListener

/**
 * Author:wenke
 * Email:3158817509@qq.com
 * Create Time:2021/1/21
 * Update Time:
 * Note:倒计时
 */
class KllTimeCountControl (millisInFuture: Long, private val tvH:TextView, private val tvM:TextView, private val tvS:TextView, val listener: OnTimeCountListener) : CountDownTimer(millisInFuture, 1000) {
    @SuppressLint("SetTextI18n")
    override fun onTick(millisUntilFinished: Long) {
        val hour=millisUntilFinished/1000/60/60
        val minute=millisUntilFinished/1000/60%60
        val second=millisUntilFinished/1000%60
        tvH.text=if(hour>9)"$hour" else "0$hour"
        tvM.text=if(minute>9)"$minute" else "0$minute"
        tvS.text=if(second>9)"$second" else "0$second"
    }
    override fun onFinish() {
        listener.onFinish()
    }
}