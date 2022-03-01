package com.changanford.common.wutil

import android.content.Context
import com.chinaums.pppay.unify.UnifyPayPlugin
import com.chinaums.pppay.unify.UnifyPayRequest

/**
 * @Author : wenke
 * @Time : 2022/3/1
 * @Description : 银商支付
 */
object PayUtils {

    /**
     * 微信支付
    * */
    fun payWx(context:Context,appPayRequest:String){
        UnifyPayRequest().apply {
            payChannel = UnifyPayRequest.CHANNEL_ALIPAY
            payData = appPayRequest
            UnifyPayPlugin.getInstance(context).sendPayRequest(this)
        }
    }
    /**
     * 支付宝小程序支付方式
     * */
    fun payAli(context:Context,appPayRequest:String){
        UnifyPayRequest().apply {
            payChannel = UnifyPayRequest.CHANNEL_ALIPAY_MINI_PROGRAM
            payData = appPayRequest
            UnifyPayPlugin.getInstance(context).sendPayRequest(this)
        }
    }
}