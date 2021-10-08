package com.changanford.circle.viewmodel

import android.content.Context
import androidx.lifecycle.MutableLiveData
import com.changanford.circle.api.CircleNetWork
import com.changanford.circle.bean.PlateBean
import com.changanford.common.MyApp
import com.changanford.common.basic.BaseViewModel
import com.changanford.common.net.*
import com.changanford.common.util.MConstant
import com.changanford.common.utilext.createHashMap

class PostViewModule :BaseViewModel(){
    var postsuccess = MutableLiveData<String>()
    val plateBean = MutableLiveData<PlateBean>()
      fun postEdit(params: HashMap<String,Any>){
         launch (block = {
              val body = params

              val rKey = getRandomKey()
              ApiClient.createApi<CircleNetWork>().postEdit(body.header(rKey),body.body(rKey))
                  .onSuccess {
                      postsuccess.value = it
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
}
