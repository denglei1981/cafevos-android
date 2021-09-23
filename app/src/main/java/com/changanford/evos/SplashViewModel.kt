package com.changanford.evos

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.changanford.common.net.body
import com.changanford.common.net.fetchRequest
import com.changanford.common.net.getRandomKey
import com.changanford.common.net.header
import com.changanford.common.util.MConstant
import com.changanford.common.util.room.Db
import com.changanford.common.utilext.logD
import com.changanford.common.utilext.logE
import kotlin.math.log

class SplashViewModel : ViewModel() {
    var key: MutableLiveData<String> = MutableLiveData()
    suspend fun getKey() {
        var request = fetchRequest {
            apiService.getKey("".body())
        }
        if (request.code == 0) {//处理成功和失败
            val content = request.data
            content?.apply {
                key.postValue(this)
            }
        } else {
            request.msg ?: "tag".logE()
        }
    }
    suspend fun getConfig(){
        var request = fetchRequest {
            var body = HashMap<String, Any>()
            body["configKey"] = "app.init.config"
            body["obj"] = true
            var rKey = getRandomKey()
            apiService.getConfigByKey(body.header(rKey),body.body(rKey))
        }
        if (request.code == 0) {//处理成功和失败
            var config = request.data
            if (config != null && !config.imgCdn.isNullOrEmpty()) {
//                viewModelScope.launch(Dispatchers.IO) {
//                    repositoryConstants!!.insert(
//                        EntityConstant(
//                            MConstant.IMG_CDN,
//                            config.imgCdn
//                        )
//                    )
                Db.myDb.saveData("imgCdn", config.imgCdn)
                MConstant.imgcdn = config.imgCdn
//                }
            }
//            if (config != null && !config.pointmallServiceTerms.isNullOrEmpty()) {
//                MConstant.pointmallServiceTerms = config.pointmallServiceTerms
//            }
//            if (config!=null&&!config.unishopCartUrl.isNullOrEmpty()){
//                MConstant.ShopCarUrl = config.unishopCartUrl
//            }
//            if (config?.floatBt != null) {//悬浮按钮
//                viewModelScope.launch(Dispatchers.IO) {
//                    repositoryConstants!!.insert(
//                        EntityConstant(
//                            MConstant.FLOAT_BT,
//                            JSON.parseObject(JSONObject.toJSONString(config.floatBt))
//                                .toString()
//                        )
//                    )
//                    MConstant.floatBt =
//                        JSON.parseObject(JSONObject.toJSONString(config.floatBt)).toString()
//                }
//            }
        } else {
            request.msg ?: "tag".logE()
        }
    }
}