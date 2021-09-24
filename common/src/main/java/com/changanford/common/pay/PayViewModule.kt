package com.changanford.common.pay

import android.app.Activity
import android.os.Message
import android.text.TextUtils
import android.util.Log
import androidx.lifecycle.ViewModel
import com.alipay.sdk.app.PayTask
import com.changanford.common.util.AppUtils
import com.changanford.common.util.ConfigUtils
import com.changanford.common.util.bus.LiveDataBus
import com.changanford.common.util.bus.LiveDataBusKey
import com.changanford.common.utilext.toastShow
import com.tencent.mm.opensdk.modelpay.PayReq
import com.tencent.mm.opensdk.openapi.WXAPIFactory
import org.json.JSONException
import org.json.JSONObject

/**********************************************************************************
 * @Copyright (C), 2018-2020.
 * @FileName: com.changanford.common.pay.PayViewModule
 * @Author:　 　
 * @Version : V1.0
 * @Date: 2020/5/20 16:18
 * @Description: 支付模块，调用支付并处理结果
 * *********************************************************************************
 */
open class PayViewModule : ViewModel() {


    fun goPay(activity: Activity, type: String, jsonStr: String) {
        //支付
        if (type == "2") {  //微信支付
            if (AppUtils.isWeixinAvilible(activity)) {
                payWx(activity, jsonStr)
            } else {
                toastShow("未安装微信")
            }
        } else if (type == "1") { //支付宝支付
            if (AppUtils.checkAliPayInstalled(activity)) {
                payV2(activity, jsonStr)
            } else {
                toastShow("未安装支付宝")
            }
        }
    }

    /**
     * 微信支付
     */
    private fun payWx(activity: Activity, wxpaystr: String) {
//        String orderStr = "{\"appid\":\"wxc58f18e68ac156a3\",\"noncestr\":\"dx2wha6yAkWR77mS\",\"package\":\"Sign=WXPay\",\"partnerid\":\"1507007951\",\"prepayid\":\"wx1118463731445632d41de80c3668575783\",\"sign\":\"3180C543E8CF8ADB7E38E9C081AA1678\",\"timestamp\":1528713947}";
        val api = WXAPIFactory.createWXAPI(activity, ConfigUtils.WXAPPID)
        api.registerApp(ConfigUtils.WXAPPID)
        val request = PayReq()
        try {
            val jsonObject = JSONObject(wxpaystr)
            request.partnerId = jsonObject.getString("machId")
            request.prepayId = jsonObject.getString("prepayId")
            request.packageValue = jsonObject.getString("packagename")
            request.nonceStr = jsonObject.getString("nonceStr")
            request.timeStamp = jsonObject.getString("timeStamp")
            request.sign = jsonObject.getString("paySign")
        } catch (e: JSONException) {
            e.printStackTrace()
        }
        request.appId = ConfigUtils.WXAPPID
        request.extData = "app data" // optional
        api.sendReq(request)
    }

    /**
     * 支付宝支付业务
     */
    private fun payV2(activity: Activity, paystr: String?) {
        /**
         * 这里只是为了方便直接向商户展示支付宝的整个支付流程；所以Demo中加签过程直接放在客户端完成；
         * 真实App里，privateKey等数据严禁放在客户端，加签过程务必要放在服务端完成；
         * 防止商户私密数据泄露，造成不必要的资金损失，及面临各种安全风险；
         *
         * orderInfo的获取必须来自服务端；
         */
//        final String orderInfo = "alipay_sdk=alipay-sdk-java-dynamicVersionNo&app_id=2016091400508893&biz_content=%7B%22out_trade_no%22%3A%22100002%22%2C%22total_amount%22%3A5%2C%22subject%22%3A%22%E6%B5%8B%E8%AF%95%22%2C%22body%22%3A%22%E6%B5%8B%E8%AF%95%22%7D&charset=utf-8&format=json&method=alipay.trade.app.pay&sign=Y3CiZDlWTvLc6NB15QpMrdMZ6qkfn%2FphmmikVoRyo70Ra3JNnSRpyU3ZWjRuNiXCBdhTTUzwE6Efs3Tvuta4kvV%2FzGO9ejUe%2Fisl9ejvhnDhhGRcm3AqVyw%2BJKl3t62Eit9kqGGCUj64eCDyscrXkY8aZOQ1wvFhAFmwk4YYXgMxlOvYTzQIuMFo8Jb%2FIlJGfvZfal09%2Fj%2FZR2wj9lpWKQlh2N6edTmfr4J8%2FOVACOaJ8ek30g0JPjnE1yFgI4BJtmuikW7Pt3kpdmD9eBWwIxr4i3IxWvXw7LuY8nU1C2SCmMQiezhQvkndDZLZAVhaXTd5y2oNrl6%2FBdqb%2FJModA%3D%3D&sign_type=RSA2&timestamp=2018-06-08+17%3A30%3A17&version=1.0";
//        EnvUtils.setEnv(EnvUtils.EnvEnum.SANDBOX);
        val payRunnable = Runnable {
            val alipay = PayTask(activity)
            val result =
                alipay.payV2(paystr, true)
            Log.i("msp", result.toString())
            val msg = Message()
            msg.obj = result
            handleAliPayResult(msg)

        }
        val payThread = Thread(payRunnable)
        payThread.start()
    }

    private fun handleAliPayResult(msg: Message) {
        val payResult =
            PayResult(msg.obj as Map<String?, String?>)
        /**
        对于支付结果，请商户依赖服务端的异步通知结果。同步通知结果，仅作为支付结束的通知。
         */
        /**
         * 对于支付结果，请商户依赖服务端的异步通知结果。同步通知结果，仅作为支付结束的通知。
         */
        val resultInfo: String = payResult.getResult() // 同步返回需要验证的信息

        val resultStatus: String = payResult.getResultStatus()
        // 判断resultStatus 为9000则代表支付成功
        // 判断resultStatus 为9000则代表支付成功
        if (TextUtils.equals(resultStatus, "9000")) {
            // 该笔订单是否真实支付成功，需要依赖服务端的异步通知。
            //TODO 回调成功
            LiveDataBus.get().with(LiveDataBusKey.ALIPAY_RESULT).postValue(true)
//            if (mWxCallBackEventpay != null) {
//                mAgentWebX5.getJsEntraceAccess()
//                    .quickCallJs(mWxCallBackEventpay.getMethod(), "true")
//        }
        } else {
            // 该笔订单真实的支付结果，需要依赖服务端的异步通知。
            //TODO 回调失败
            LiveDataBus.get().with(LiveDataBusKey.ALIPAY_RESULT).postValue(false)
//            if (mWxCallBackEventpay != null) {
//                mAgentWebX5.getJsEntraceAccess()
//                    .quickCallJs(mWxCallBackEventpay.getMethod(), "false")
//            }
        }
    }
}