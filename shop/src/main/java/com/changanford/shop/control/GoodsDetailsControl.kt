package com.changanford.shop.control

import android.app.Activity
import android.os.CountDownTimer
import com.changanford.shop.databinding.ActivityGoodsDetailsBinding
import com.changanford.shop.databinding.HeaderGoodsDetailsBinding

/**
 * @Author : wenke
 * @Time : 2021/9/18 0018
 * @Description : GoodsDetailsControl
 */
class GoodsDetailsControl(val activity: Activity, val binding: ActivityGoodsDetailsBinding,
                          private val headerBinding: HeaderGoodsDetailsBinding) {
    private var timeCount: CountDownTimer?=null
    /**
     * 秒杀倒计时
     * [remainingTime]剩余时间 毫秒
    * */
    fun initTimeCount(remainingTime:Long){
        timeCount=TimeCountControl(remainingTime,headerBinding.inKill.tvKillH,headerBinding.inKill.tvKillM,headerBinding.inKill.tvKillS,object :TimeCountControl.OnTimeCountListener{
            override fun onFinish() {

            }
        })
        timeCount?.start()
    }
    fun onDestroy(){
        timeCount?.cancel()
    }
}