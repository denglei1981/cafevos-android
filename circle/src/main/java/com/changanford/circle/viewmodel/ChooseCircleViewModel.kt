package com.changanford.circle.viewmodel

import androidx.lifecycle.MutableLiveData
import com.changanford.circle.api.CircleNetWork
import com.changanford.circle.bean.ChooseCircleData
import com.changanford.common.MyApp
import com.changanford.common.basic.BaseViewModel
import com.changanford.common.net.*
import com.changanford.common.utilext.createHashMap
import com.changanford.common.utilext.toast

class ChooseCircleViewModel : BaseViewModel() {
    var datas = MutableLiveData<ArrayList<ChooseCircleData>>()
    var lists = arrayListOf<ChooseCircleData>()
    fun getjoinCircle() {//我加入的圈子
        launch(block = {
            val body = MyApp.mContext.createHashMap()
            body["type"] = 1
            val rKey = getRandomKey()
            ApiClient.createApi<CircleNetWork>().getjoinCircle(body.header(rKey), body.body(rKey))
                .onSuccess {
                    it?.let {
                        if (it.dataList.isNotEmpty()) {
                            var chooseCircleData = ChooseCircleData(title = "我加入的", mItemType = 1)
                            lists.add(chooseCircleData)
                            for (bean in it.dataList) {
                                bean.apply {
                                    mItemType = 2
                                }
                            }
                            lists.addAll(it.dataList)
                        }
                        datas.value = lists
                    }


                }
                .onFailure {

                }
        })
    }

    fun getCreateCircles() {//我创建的圈子
        launch(block = {
            val body = MyApp.mContext.createHashMap()
            body["type"] = 1
            val rKey = getRandomKey()
            ApiClient.createApi<CircleNetWork>()
                .getCreateCircles(body.header(rKey), body.body(rKey))
                .onSuccess {
                    it?.let { it ->
                        if (it.dataList.isNotEmpty()) {
                            var chooseCircleBean = ChooseCircleData(title = "我创建的", mItemType = 1)
                            lists.add(chooseCircleBean)
                            for (bean in it.dataList) {
                                bean.apply {
                                    mItemType = 2
                                }
                            }
                            lists.addAll(it.dataList)
                            getjoinCircle()
                        } else {
                            getjoinCircle()
                        }
                    }
                }
                .onFailure {

                }
        }, error = {
            it.message?.toast()
        })
    }

    fun circleSelectedByPosts() {//我创建的圈子
        launch(block = {
            val body = MyApp.mContext.createHashMap()
//            body["type"]=1
            val rKey = getRandomKey()
            ApiClient.createApi<CircleNetWork>()
                .circleSelectedByPosts(body.header(rKey), body.body(rKey))
                .onSuccess {
                    it?.let {
                        it.forEach { bean ->
                            if (bean.circles.isNotEmpty()) {
                                val chooseCircleBean =
                                    ChooseCircleData(title = bean.typeName, mItemType = 1)
                                lists.add(chooseCircleBean)
                                for (data in bean.circles) {
                                    data.apply {
                                        mItemType = 2
                                    }
                                }
                                lists.addAll(bean.circles)
                            }
                        }
                        datas.value = lists
                    }

                }
                .onFailure {

                }
        }, error = {
            it.message?.toast()
        })
    }
}
