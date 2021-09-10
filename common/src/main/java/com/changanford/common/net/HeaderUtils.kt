package com.changanford.common.net

import android.util.Log
import com.alibaba.fastjson.JSON
import com.changan.nativelib.JNIInterface
import com.changanford.common.MyApp
import com.changanford.common.util.*
import com.changanford.common.util.bus.COOKIE_DB
import com.changanford.common.util.bus.LiveDataBus
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import kotlin.random.Random

/**********************************************************************************
 * @Copyright (C), 2020-2021.
 * @FileName: com.changanford.common.net.HeaderUtils
 * @Author:　 　
 * @Version : V1.0
 * @Date: 2021/6/21 17:05
 * @Description: 接口请求扩展函数
 * 按照原有的参数传递
 * @see ApiClient.client 设置userAgent的方式已经放入OkHttpClient
 * *********************************************************************************
 */

suspend fun Map<String, Any>.header(key: String): HashMap<String, String> {
    return getHeader(this, key, SPUtils.getToken())
}


fun getHeader(
    body: Map<String, Any>,
    key: String,
    token: String?
): HashMap<String, String> {
    var timestamp = System.currentTimeMillis().toString()
    val map = HashMap<String, String>()
    map["Content-Type"] = "application/json"
    if (MConstant.pubKey.isNullOrEmpty()) {
        LiveDataBus.get().with(COOKIE_DB).postValue(true)
    }
    map["seccode"] = handlePubKey(MConstant.pubKey, key)
    map["codelab"] = key
    map["sign"] = MD5Utils.encode_big(
        getAESBody(body, key).plus(timestamp).plus("hyzh-unistar-5KWJKH291IvadR")
    )

    if (null != token && token.isNotEmpty()) {
        map["token"] = token
    }
    //设置默认token
//    map["token"] = "user:token:app:28425:weixin-c013db196f696c1c6815e14e78a727ac"
    map["timestamp"] = timestamp
    map["os"] = "Android"//操作系统 （ios、Android、wp）
    map["osVersion"] = DeviceUtils.getDeviceVersion()//操作系统版本号
    map["loginChannel"] =
        DeviceUtils.getMetaData(
            MyApp.mContext, "CHANNEL_VALUE"
        )
    map["appVersion"] = DeviceUtils.getversionName()//app版本号
    map["model"] = DeviceUtils.getDeviceModel()//手机机型
    map["brand"] = DeviceUtils.getManuFacture()//手机品牌
    map["operatorName"] = NetUtils.getOperatorName(MyApp.mContext)//运营商名称
    map["networkState"] = NetUtils.getNetworkState(MyApp.mContext)//网络类型
    map["body"] = MD5Utils.encode_big(JSON.toJSON(body).toString())//body的明文，用于键值判断，在传输的时候删除

    return map
}


fun getRandomKey(): String {
    return System.currentTimeMillis().toString().plus((Random.nextInt(26) + 65).toChar().toString())
        .plus((Random.nextInt(26) + 65).toChar().toString())
        .plus((Random.nextInt(26) + 65).toChar().toString())
}

fun getAESBody(body: Map<String, Any>, key: String): String {
    var hashMap = HashMap<String, String>()
    hashMap["paramEncr"] = AESUtil.encrypts(JSON.toJSON(body).toString(), key)
    return JSON.toJSON(hashMap).toString()
}

fun handlePubKey(pubKey: String, key: String): String {
    var jniInterface = JNIInterface()
    if (pubKey.isNullOrEmpty() || pubKey.length <= 8) {
        return "publicKey Error!"
    }
    var encPubKey = jniInterface.testApi(pubKey)
    val seccode = RsaUtils.encryptByPublicKey(encPubKey, key)
    return seccode
}


fun decryResult(result: String, key: String): String = AESUtil.decrypts(result, key)


fun Map<String, Any>.body(key: String): RequestBody {
    if (MConstant.isDebug)
        Log.d("body----------", JSON.toJSONString(this))
    return getAESBody(this, key)
        .toRequestBody("application/json;charset=utf-8".toMediaType())
}


fun String.body(): RequestBody {
    return this
        .toRequestBody("application/json;charset=utf-8".toMediaType())
}