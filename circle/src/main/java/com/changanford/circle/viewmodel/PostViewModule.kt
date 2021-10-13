package com.changanford.circle.viewmodel

import android.content.Context
import androidx.lifecycle.MutableLiveData
import com.changanford.circle.api.CircleNetWork
import com.changanford.circle.bean.PlateBean
import com.changanford.circle.bean.PostKeywordBean
import com.changanford.common.MyApp
import com.changanford.common.basic.BaseViewModel
import com.changanford.common.basic.PostRoomViewModel
import com.changanford.common.bean.LocationDataBean
import com.changanford.common.bean.STSBean
import com.changanford.common.net.*
import com.changanford.common.util.MConstant
import com.changanford.common.utilext.createHashMap

class PostViewModule() :PostRoomViewModel(){
    var postsuccess = MutableLiveData<String>()
    val plateBean = MutableLiveData<PlateBean>()
    val cityCode = MutableLiveData<LocationDataBean>()
    val stsBean = MutableLiveData<STSBean>()
    val keywords = MutableLiveData<List<PostKeywordBean>>()
      fun postEdit(params: HashMap<String,Any>){
         launch (block = {
              val body = params

              val rKey = getRandomKey()
              ApiClient.createApi<CircleNetWork>().postEdit(body.header(rKey),body.body(rKey))
                  .onSuccess {
                      postsuccess.value = "upsuccess"
                  }
                  .onFailure {

                  }
          })
      }

    /**
     * 获取发帖模块
     */
    fun getPlate(){
        launch(block =  {
            val body = MyApp.mContext.createHashMap()
            val rKey = getRandomKey()
            ApiClient.createApi<CircleNetWork>().getPlate(body.header(rKey),body.body(rKey))
                .onSuccess {
                     plateBean.value = it
                }
                .onFailure {

                }
        })
    }

    /**
     * 获取发帖模块
     */
    fun getKeyWords(){
        launch(block =  {
            val body = MyApp.mContext.createHashMap()
            val rKey = getRandomKey()
            ApiClient.createApi<CircleNetWork>().getkeywords(body.header(rKey),body.body(rKey))
                .onSuccess {
                    keywords.value = it
                }
                .onFailure {

                }
        })
    }



    /**
     * 获取省市区ID
     */
    fun getCityDetailBylngAndlat(latY: Double,lngX:Double) {
        launch(block =  {
            val body = MyApp.mContext.createHashMap()
            body["latY"] = latY
            body["lngX"] = lngX
            val rKey = getRandomKey()
            ApiClient.createApi<CircleNetWork>().getCityDetailBylngAndlat(body.header(rKey),body.body(rKey))
                .onSuccess {
                    cityCode.value= it
                }
                .onFailure {

                }
        })

    }


    fun getOSS(){
        launch(block =  {
            val body = MyApp.mContext.createHashMap()
            val rKey = getRandomKey()
            ApiClient.createApi<NetWorkApi>().getOSS(body.header(rKey),body.body(rKey))
                .onSuccess {
                    stsBean.value= it
                }
                .onFailure {

                }
        })
    }
}
