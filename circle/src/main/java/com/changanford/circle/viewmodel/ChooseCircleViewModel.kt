package com.changanford.circle.viewmodel

import androidx.lifecycle.MutableLiveData
import com.changanford.circle.api.CircleNetWork
import com.changanford.circle.bean.ChooseCircleBean
import com.changanford.circle.bean.ChooseCircleData
import com.changanford.common.MyApp
import com.changanford.common.basic.BaseViewModel
import com.changanford.common.net.*
import com.changanford.common.utilext.createHashMap
import com.changanford.common.utilext.toast

class ChooseCircleViewModel :BaseViewModel(){
    var datas = MutableLiveData<ArrayList<ChooseCircleData>>()
    var lists = arrayListOf<ChooseCircleData>()
    fun getjoinCircle(){//我加入的圈子
        launch (block = {
            val body = MyApp.mContext.createHashMap()
            body["type"]=1
            val rKey = getRandomKey()
            ApiClient.createApi<CircleNetWork>().getjoinCircle(body.header(rKey),body.body(rKey))
                .onSuccess {
                    it?.let {
                        if (it.dataList.isNotEmpty()){
                            var chooseCircleData = ChooseCircleData(title = "我加入的",ItemType = 1)
                            lists.add(chooseCircleData)
                            for (chooseCircleData in it.dataList) {
                                chooseCircleData.apply {
                                    itemType = 2
                                }
                            }
                            lists.addAll(it.dataList)
                            datas.value = lists
                        }
                    }


                }
                .onFailure {

                }
        })
    }

    fun getCreateCircles(){//我创建的圈子
        launch (block = {
            val body = MyApp.mContext.createHashMap()
            body["type"]=1
            val rKey = getRandomKey()
            ApiClient.createApi<CircleNetWork>().getCreateCircles(body.header(rKey),body.body(rKey))
                .onSuccess {
                    it?.let { it ->
                        if (it.dataList.isNotEmpty()){
                            var chooseCircleBean = ChooseCircleData(title = "我创建的",ItemType = 1)
                            lists.add(chooseCircleBean)
                            for (chooseCircleData in it.dataList) {
                                chooseCircleData.apply {
                                    itemType = 2
                                }
                            }
                            lists.addAll(it.dataList)
                            datas.value = lists
                            getjoinCircle()
                        }else{
                            getjoinCircle()
                        }
                    }
                }
                .onFailure {

                }
        },error = {
            it.message?.toast()
        })
    }
}
