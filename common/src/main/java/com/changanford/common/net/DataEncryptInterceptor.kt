package com.changanford.common.net

import com.changanford.common.MyApp
import com.changanford.common.basic.BaseApplication
import com.changanford.common.manger.RouterManger
import com.changanford.common.router.path.ARouterCarControlPath
import com.changanford.common.router.path.ARouterMyPath
import com.changanford.common.router.startARouter
import com.changanford.common.util.AppUtils
import com.changanford.common.util.MConstant
import com.changanford.common.util.SPUtils
import com.changanford.common.util.bus.LiveDataBus
import com.changanford.common.util.bus.LiveDataBusKey
import com.changanford.common.utilext.longE
import com.changanford.common.utilext.toast
import com.google.gson.Gson
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.ResponseBody.Companion.toResponseBody
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import org.json.JSONTokener

/**********************************************************************************
 * @Copyright (C), 2018-2020.
 * @Author:　 　
 * @Version : V1.0
 * @Date: 2020/5/15 09:47
 * @Description: 解密数据拦截器，key不能传进Header!
 * *********************************************************************************
 */
class DataEncryptInterceptor : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        var request: Request = chain.request()
        var key: String? = request.header("codelab")
        var body: String? = request.header("body")
        var headers: Headers = request.headers
        request =
            request.newBuilder().headers(headers = headers).header("codelab", "codelabs").build()
        var response: Response
        response = chain.proceed(request)
        if (response.isSuccessful) {//请求成功解密
            key?.let {
                var responseBody1: ResponseBody = response.body!!
                var responseStr1: String = responseBody1.string()
                //数据转json
                if (responseStr1.isNullOrEmpty()) {
                    return response
//                    throw ServerException(ERROR.UNKNOWN, "请稍后再试")
                }
                var json = JSONObject(responseStr1)
                var jsonData: String = ""
                //读取加密标识
                var isEncr = json.getBoolean("encr")
                //读取data数据
                if (json.has("data")) {
                    jsonData = json.getString("data")
                }

                //业务的500处理为异常，与服务器区分开
                var code: Int = if (json.has("code")) json.getInt("code") else 0
                //按返回组装数据
                var commonResponse = CommonResponse(
                    if (code == StatusCode.ERROR) StatusCode.SERVICE_ERROR else code,
                    if (json.has("message")) json.getString("message") else "",
                    if (json.has("msg")) json.getString("msg") else "",
                    jsonData,
                    if (json.has("timestamp")) json.getString("timestamp") else "",
                    if (json.has("msgId")) json.getString("msgId") else "",
                    //默认按加密流程走
                    if (json.has("encr")) json.getBoolean("encr") else true
                )

                //按加密流程走
                if (isEncr) {
                    commonResponse = Gson().fromJson(
                        responseStr1,
                        CommonResponse::class.java
                    ) as CommonResponse<String>
                }

                if (commonResponse.code == StatusCode.REDIRECT_NOT_FOUND_PAGE) {
                    val activity = BaseApplication.curActivity
                    startARouter(ARouterCarControlPath.NothingActivity)
                    activity.finish()
                }
                if(commonResponse.code==StatusCode.ERROR_ADDRESS){
                    LiveDataBus.get().with(LiveDataBusKey.SHOW_ERROR_ADDRESS)
                        .postValue(commonResponse.msg)
                }
                if (commonResponse.code == StatusCode.UN_LOGIN) {  //登录过期 清空token 跳转到登录页面
                    AppUtils.Unbinduserid()
                    if(MConstant.token.isNotEmpty()){
                        val activity = BaseApplication.curActivity
                        activity.runOnUiThread {
                            "您已退出登录".toast()
                        }
                    }
                    RouterManger.param("isClear", true).startARouter(ARouterMyPath.SignUI)
                    try {
                        var isfirstin =
                            SPUtils.getParam(MyApp.mContext, "isfirstin", false) as Boolean
                        var isDebug = SPUtils.getParam(
                            BaseApplication.INSTANT,
                            MConstant.ISDEBUG,
                            true
                        ) as Boolean
                        var isPopAgreement =
                            SPUtils.getParam(
                                MyApp.mContext,
                                "isPopAgreement",
                                true
                            ) as Boolean
                        var versionCode =
                            SPUtils.getParam(BaseApplication.INSTANT, "versionCode", 0) as Int
                        var pushId =
                            SPUtils.getParam(MyApp.mContext, MConstant.PUSH_ID, "11111") as String
                        SPUtils.clear(MyApp.mContext)
                        SPUtils.setParam(MyApp.mContext, MConstant.PUSH_ID, pushId)
                        SPUtils.setParam(MyApp.mContext, MConstant.ISDEBUG, isDebug)
                        SPUtils.setParam(MyApp.mContext, "isfirstin", isfirstin)
                        SPUtils.setParam(MyApp.mContext, "isPopAgreement", isPopAgreement)
                        SPUtils.setParam(BaseApplication.INSTANT, "versionCode", versionCode)

                    } catch (e: java.lang.Exception) {
                        SPUtils.clear(MyApp.mContext)
                    }
                }
                var decryptStr: String? = null
                //初始化data字段 有加密去解密 无加密直接赋值
                if (!commonResponse.data.isNullOrEmpty()) {
                    try {
                        decryptStr =
                            if (isEncr) decryResult(commonResponse.data!!, key) else jsonData
                    } catch (e: Exception) {

                    }

                }
//                "result---->$decryptStr".longE()
                var jsonObject = JSONObject()
                if (!decryptStr.isNullOrEmpty()) {
                    try {
                        var tokner = JSONTokener(decryptStr)
                        var type = tokner.nextValue()
                        when (type) {
                            is JSONObject -> {//对象
                                try {
                                    var subJSONObject = JSONObject(decryptStr)
                                    if (subJSONObject.has("dataList")) {
                                        if (subJSONObject["dataList"].toString() == "null") {
                                            subJSONObject.put("dataList", JSONArray())
                                        }
                                    }
                                    jsonObject.put("data", subJSONObject)
                                } catch (e: JSONException) {
                                    jsonObject.put("data", decryptStr)
                                } catch (e: Exception) {
                                    throw JSONException("Json格式解析错误")
                                }
                            }
                            is JSONArray -> {//列表
                                try {
                                    var subJSONArray = JSONArray(decryptStr)
                                    jsonObject.put("data", subJSONArray)
                                } catch (e: JSONException) {
                                    jsonObject.put("data", decryptStr)
                                } catch (e: Exception) {
                                    throw JSONException("Json格式解析错误")
                                }
                            }
                            else -> {//字符串
                                if (decryptStr != "null") {
                                    try {
                                        jsonObject.put("data", handleStrJson(decryptStr))
                                    } catch (e: JSONException) {
                                        jsonObject.put("data", decryptStr)
                                    } catch (e: Exception) {
                                        throw JSONException("Json格式解析错误")
                                    }
                                }
                            }
                        }
                    } catch (e: Exception) {
                        if (decryptStr != "null") {
                            jsonObject.put("data", decryptStr)
                        }
                    }
                }
                jsonObject.put("code", commonResponse.code)
                jsonObject.put("message", commonResponse.message)
                jsonObject.put("msg", commonResponse.msg)
                jsonObject.put("timestamp", commonResponse.timestamp)
                jsonObject.put("msgId", commonResponse.msgId)
                jsonObject.put("encr", commonResponse.encr)

                var baseBeanStr = jsonObject.toString()
                responseBody1.close()
                var responseBody2: ResponseBody =
                    baseBeanStr.toResponseBody("application/json;charset=utf-8".toMediaType())
                response = response.newBuilder().body(responseBody2).build()

            }
        }
        return response
    }

    private fun handleStrJson(strJson: String): JSONObject {
        var sub = strJson.replace("\\", "")
        sub = sub.substring(1, sub.length - 1)
        return JSONObject(strJson)
    }
}