package com.changanford.evos

import android.os.SystemClock
import android.text.TextUtils
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.alibaba.fastjson.JSON
import com.alibaba.fastjson.JSONObject
import com.bumptech.glide.Glide
import com.changanford.common.basic.BaseApplication
import com.changanford.common.bean.AdBean
import com.changanford.common.net.body
import com.changanford.common.net.fetchRequest
import com.changanford.common.net.getRandomKey
import com.changanford.common.net.header
import com.changanford.common.net.onFailure
import com.changanford.common.net.onSuccess
import com.changanford.common.net.onWithMsgFailure
import com.changanford.common.util.MConstant
import com.changanford.common.util.MConstant.IMGURLTAG
import com.changanford.common.util.SPUtils
import com.changanford.common.util.room.Db
import com.changanford.common.utilext.GlideUtils
import com.changanford.common.utilext.logE
import com.changanford.common.utilext.toast
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.lang.reflect.Type

class SplashViewModel : ViewModel() {
    private var time = SystemClock.elapsedRealtime() + 5 * 1000
    var jump = false
    fun getTime(): Long {
        return time
    }

    fun setTime(s: String?) {
        if (s.isNullOrEmpty())
            return
        try {
            time = SystemClock.elapsedRealtime() + Integer.valueOf(s) * 1000
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    var key: MutableLiveData<String> = MutableLiveData()
    var imgBean: MutableLiveData<AdBean> = MutableLiveData()

    fun getKey() {
        viewModelScope.launch {
            Db.myDb.getData("pubKey")?.storeValue?.apply {
                MConstant.pubKey = this
            }
            Db.myDb.getData("imgCdn")?.storeValue?.apply {
                MConstant.imgcdn = if (TextUtils.isEmpty(this)) MConstant.defaultImgCdn else this
            }
            if (MConstant.pubKey.isNotEmpty()) {
//                key.postValue(MConstant.pubKey)
//                if (MConstant.imgcdn.isNullOrEmpty()) {
//                    getConfig()
//                }
            }
        }
        viewModelScope.launch {
            fetchRequest {
                apiService.getKeyV2("".body())
            }.onSuccess {
                if (it != null) {
                    MConstant.pubKey = it
                    getConfig()
                    viewModelScope.launch {
                        Db.myDb.saveData("pubKey", it)
                    }
                } else {
                    getKey()
                }
            }.onFailure {
                it ?: "tag".logE()
            }.onWithMsgFailure {
                it?.toast()
                launch(Dispatchers.IO) {
                    delay(1000)
                    withContext(Dispatchers.Main) {
                        getKey()
                    }
                }
            }
        }
    }

    private fun getBottomNavigate() {
        viewModelScope.launch {
            val request = fetchRequest {
                val body = HashMap<String, Any>()
                val rKey = getRandomKey()
                apiService.appNavigateIcon(body.header(rKey), body.body(rKey))
            }
            if (request.code == 0) {//处理成功和失败
                if (request.data!=null){
                    val bitmapOne = withContext(Dispatchers.IO) {
                        Glide.with(BaseApplication.curActivity).asBitmap()
                            .load(GlideUtils.handleImgUrl(request.data?.iconFirst)).into(74, 74).get()
                    }
                    val bitmapTwo = withContext(Dispatchers.IO) {
                        Glide.with(BaseApplication.curActivity).asBitmap()
                            .load(GlideUtils.handleImgUrl(request.data?.iconSecond)).into(74, 74).get()
                    }
                    val bitmapThree = withContext(Dispatchers.IO) {
                        Glide.with(BaseApplication.curActivity).asBitmap()
                            .load(GlideUtils.handleImgUrl(request.data?.iconThird)).into(74, 74).get()
                    }
                    val bitmapFour = withContext(Dispatchers.IO) {
                        Glide.with(BaseApplication.curActivity).asBitmap()
                            .load(GlideUtils.handleImgUrl(request.data?.iconFourth)).into(74, 74).get()
                    }
                    val bitmapFive = withContext(Dispatchers.IO) {
                        Glide.with(BaseApplication.curActivity).asBitmap()
                            .load(GlideUtils.handleImgUrl(request.data?.iconFive)).into(74, 74).get()
                    }
                    val bean = request.data
                    bean?.btOne = bitmapOne
                    bean?.btTwo = bitmapTwo
                    bean?.btThree = bitmapThree
                    bean?.btFour = bitmapFour
                    bean?.btFive = bitmapFive
                    MConstant.bottomNavigateBean = bean
                }
//                val config = request.data ?: 0
            }
            key.postValue(MConstant.pubKey)
        }
    }

    /**
     * 获取配置
     */
    private fun getConfig() {
        viewModelScope.launch {
            val request = fetchRequest {
                val body = HashMap<String, Any>()
                body["configKey"] = "app.init.config"
                body["obj"] = true
                val rKey = getRandomKey()
                apiService.getConfigByKey(body.header(rKey), body.body(rKey))
            }
            if (request.code == 0) {//处理成功和失败
                val config = request.data
                MConstant.configBean = config
                if (config != null && !config.imgCdn.isNullOrEmpty()) {
                    Db.myDb.saveData("imgCdn", config.imgCdn)
                    getBottomNavigate()
                }
            } else {
                request.msg ?: "tag".logE()
            }
        }
        viewModelScope.launch {
            val request = fetchRequest {
                val body = HashMap<String, Any>()
                body["configKey"] = "app_mourning_mode"
                body["obj"] = true
                val rKey = getRandomKey()
                apiService.getAppMourningMode(body.header(rKey), body.body(rKey))
            }
            if (request.code == 0) {//处理成功和失败
                val config = request.data ?: 0
                MConstant.app_mourning_mode = config
            } else {
                request.msg ?: "tag".logE()
            }
        }
    }

    /**
     * 获取启动广告
     */
    fun adService(code: String) {
        viewModelScope.launch {
            var body = HashMap<String, Any>()
            body["posCode"] = code
            var rkey = getRandomKey()
            fetchRequest {
                apiService.getHeadBanner(body.header(rkey), body.body(rkey))
            }.onSuccess {
                if (!it.isNullOrEmpty() && it.size > 0) {
                    viewModelScope.launch(Dispatchers.IO) {
                        it?.let { _ ->
                            Db.myDb.saveData(
                                IMGURLTAG,
                                JSON.parseArray(JSONObject.toJSONString(it)).toString()
                            )
                        }
                    }
                } else {
                    viewModelScope.launch(Dispatchers.IO) {
                        Db.myDb.saveData(IMGURLTAG, "")
                    }
                }
            }
        }
    }

    /**
     * 获取缓存的启动广告
     */
    fun getDbAds() {
        viewModelScope.launch(Dispatchers.IO) {
            MConstant.pubKey?.let { it ->
                if (Db.myDb.getData(IMGURLTAG) == null) {
                    imgBean.postValue(null)
                } else {
                    Db.myDb.getData(IMGURLTAG)?.let { it ->
                        it.storeValue?.let { its ->
                            if (it.storeValue.isNullOrEmpty()) {
                                imgBean.postValue(null)
                            } else {
                                try {
                                    val type: Type =
                                        object : TypeToken<ArrayList<AdBean?>?>() {}.type
                                    var lists: ArrayList<AdBean> =
                                        Gson().fromJson(its, type)
                                    var i =
                                        SPUtils.getParam(
                                            BaseApplication.curActivity,
                                            "showImg",
                                            0
                                        ) as Int
                                    SPUtils.setParam(BaseApplication.curActivity, "showImg", i + 1)
                                    imgBean.postValue(lists[i % lists.size])
                                } catch (e: Exception) {
                                    imgBean.postValue(null)
                                }
                            }
                        }
                    }
                }
            }
        }
    }

}