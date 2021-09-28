package com.changanford.shop.viewmodel

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.changanford.common.bean.GoodsItemBean
import com.changanford.common.net.*
import kotlinx.coroutines.launch

/**
 * @Author : wenke
 * @Time : 2021/9/9 0009
 * @Description : GoodsViewModel
 */
class GoodsViewModel:ViewModel() {
    private val body = HashMap<String, Any>()
    var goodsItemData: MutableLiveData<GoodsItemBean> = MutableLiveData()
    /**
     * 获取商品详情数据
    * */
    fun queryGoodsDetails(goodsId:String){
        Log.e("wenke","goodsId:$goodsId")
        viewModelScope.launch {
            fetchRequest {
                body.clear()
                body["goodsId"] = goodsId
                val rkey = getRandomKey()
                apiService.queryGoodsDetails(body.header(rkey), body.body(rkey))
            }.onSuccess {
                goodsItemData.postValue(it)
            }.onFailure {
            }
        }
    }
}