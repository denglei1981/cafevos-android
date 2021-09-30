package com.changanford.shop.control

import android.app.Activity
import android.os.CountDownTimer
import com.changanford.common.bean.GoodsItemBean
import com.changanford.shop.control.time.KllTimeCountControl
import com.changanford.shop.databinding.ActivityGoodsDetailsBinding
import com.changanford.shop.databinding.HeaderGoodsDetailsBinding
import com.changanford.shop.listener.OnTimeCountListener
import com.changanford.shop.popupwindow.GoodsAttrsPop

/**
 * @Author : wenke
 * @Time : 2021/9/18 0018
 * @Description : GoodsDetailsControl
 */
class GoodsDetailsControl(val activity: Activity, val binding: ActivityGoodsDetailsBinding,
                          private val headerBinding: HeaderGoodsDetailsBinding) {
    private var timeCount: CountDownTimer?=null
    fun bindingData(data:GoodsItemBean?){

    }
    /**
     * 秒杀倒计时
     * [remainingTime]剩余时间 毫秒
    * */
    fun initTimeCount(remainingTime:Long){
        timeCount= KllTimeCountControl(remainingTime,headerBinding.inKill.tvKillH,headerBinding.inKill.tvKillM,headerBinding.inKill.tvKillS,object :
            OnTimeCountListener {
            override fun onFinish() {

            }
        })
        timeCount?.start()
    }
    /**
     * 创建选择商品属性弹窗
    * */
    fun createAttribute(){
        val pop= GoodsAttrsPop(activity)
        pop.showPopupWindow()
    }

    fun onDestroy(){
        timeCount?.cancel()
    }
}