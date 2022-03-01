package com.changanford.common.wutil

import android.content.Context
import android.content.Intent
import android.util.Log
import com.changanford.common.BuildConfig
import com.chinaums.pppay.unify.UnifyPayListener
import com.chinaums.pppay.unify.UnifyPayPlugin
import com.chinaums.pppay.unify.UnifyPayRequest
import com.unionpay.UPPayAssistEx
import org.json.JSONException
import org.json.JSONObject

/**
 * @Author : wenke
 * @Time : 2022/3/1
 * @Description : 银商支付
 */
object PayUtils {
    private const val TAG="PayUtils"
    /**
     * 微信支付
    * */
    fun payWx(context:Context,appPayRequest:String,listener: UnifyPayListener?=null){
        UnifyPayRequest().apply {
            payChannel = UnifyPayRequest.CHANNEL_ALIPAY
            payData = appPayRequest
            UnifyPayPlugin.getInstance(context).let {
                it.sendPayRequest(this)
                listener?.apply {it.listener = this}
            }
        }
    }
    /**
     * 支付宝小程序支付方式
     * */
    fun payAliPayMiniPro(context:Context,appPayRequest:String){
        UnifyPayRequest().apply {
            payChannel = UnifyPayRequest.CHANNEL_ALIPAY_MINI_PROGRAM
            payData = appPayRequest
            UnifyPayPlugin.getInstance(context).sendPayRequest(this)
        }
    }
    /**
     * 云闪付
     * [serverMode]为后台环境标识，默认使用“00”生产环境
     * */
    fun payCloudQuickPay(context:Context,appPayRequest:String,serverMode:String="00"){
        var tn = "空"
        try {
            val e = JSONObject(appPayRequest)
            tn = e.getString("tn")
        } catch (e1: JSONException) {
            e1.printStackTrace()
        }
        UPPayAssistEx.startPay(context, null, null, tn, serverMode)
        if(BuildConfig.DEBUG)Log.d(TAG, "云闪付支付 tn = $tn")
    }
    /**
     * 云闪付 -支付回调
     * pay_result:支付控件返回字符串:success、fail、cancel 分别代表支付成功，支付失败，支付取消
    * */
    fun payOnActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        /**
         * 处理银联云闪付手机支付控件返回的支付结果
         */
        var msg = "resultCode:$resultCode"
        data?.extras?.getString("pay_result")?.apply {
            when {
                equals("success", ignoreCase = true) -> {
                    //如果想对结果数据校验确认，直接去商户后台查询交易结果，
                    //校验支付结果需要用到的参数有sign、data、mode(测试或生产)，sign和data可以在result_data获取到
                    /**
                     * result_data参数说明：
                     * sign —— 签名后做Base64的数据
                     * data —— 用于签名的原始数据
                     * data中原始数据结构：
                     * pay_result —— 支付结果success，fail，cancel
                     * tn —— 订单号
                     */
                    msg = "云闪付支付成功"
                }
                equals("fail", ignoreCase = true) -> {
                    msg = "云闪付支付失败！"
                }
                equals("cancel", ignoreCase = true) -> {
                    msg = "用户取消了云闪付支付"
                }
            }
        }
        if(BuildConfig.DEBUG)Log.d(TAG, "onActivityResult>>msg:$msg")
    }
}