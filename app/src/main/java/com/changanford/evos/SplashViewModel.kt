package com.changanford.evos

import android.os.SystemClock
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.alibaba.fastjson.JSON
import com.alibaba.fastjson.JSONObject
import com.changanford.common.basic.BaseApplication
import com.changanford.common.bean.AdBean
import com.changanford.common.net.*
import com.changanford.common.util.MConstant
import com.changanford.common.util.MConstant.IMGURLTAG
import com.changanford.common.util.SPUtils
import com.changanford.common.util.room.Db
import com.changanford.common.utilext.logD
import com.changanford.common.utilext.logE
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.lang.reflect.Type
import kotlin.math.log

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
            MConstant.pubKey = Db.myDb.getData("pubKey")?.storeValue ?: ""
            MConstant.imgcdn = Db.myDb.getData("imgCdn")?.storeValue ?: ""
            if (MConstant.pubKey.isNotEmpty()) {
                key.postValue(MConstant.pubKey)
                if (MConstant.imgcdn.isNullOrEmpty()) {
                    getConfig()
                }
            }
        }
        viewModelScope.launch {
            fetchRequest {
                apiService.getKey("".body())
            }.onSuccess {
                if (it != null) {
                    if (MConstant.pubKey.isNullOrEmpty()) {
                        key.postValue(it)
                    }
                    MConstant.pubKey = it
                    getConfig()
                    viewModelScope.launch{
                        Db.myDb.saveData("pubKey", it)
                    }
                } else {
                    getKey()
                }
            }.onFailure {
                it ?: "tag".logE()
            }
        }
    }

    /**
     * 获取配置
     */
    private fun getConfig() {
        viewModelScope.launch {
            var request = fetchRequest {
                var body = HashMap<String, Any>()
                body["configKey"] = "app.init.config"
                body["obj"] = true
                var rKey = getRandomKey()
                apiService.getConfigByKey(body.header(rKey), body.body(rKey))
            }
            if (request.code == 0) {//处理成功和失败
                var config = request.data
                if (config != null && !config.imgCdn.isNullOrEmpty()) {
                    Db.myDb.saveData("imgCdn", config.imgCdn)
                    MConstant.imgcdn = config.imgCdn
                }
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