package com.changanford.shop.viewmodel

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.changanford.common.MyApp
import com.changanford.common.bean.GoodsItemBean
import com.changanford.common.bean.GoodsList
import com.changanford.common.net.*
import com.changanford.common.utilext.createHashMap
import com.changanford.shop.base.BaseViewModel
import kotlinx.coroutines.launch

/**
 * @Author : wenke
 * @Time : 2021/9/9 0009
 * @Description : GoodsViewModel
 */
class GoodsViewModel: BaseViewModel() {
    private val body = MyApp.mContext.createHashMap()
    var goodsItemData: MutableLiveData<GoodsItemBean> = MutableLiveData()
    //商品列表
    var goodsListData =MutableLiveData<GoodsList?>()
    private var pageSize=20
    /**
     * 获取商品列表
     * [typeId]分类id
     * */
    fun getGoodsList(typeId:String,page:Int,pageSize:Int=this.pageSize){
        viewModelScope.launch {
            fetchRequest {
                body.clear()
                body["page"]=page
                body["pageSize"]=pageSize
                body["tagId"]=typeId
                val randomKey = getRandomKey()
                shopApiService.queryGoodsList(body.header(randomKey), body.body(randomKey))
            }.onSuccess {
                Log.e("wenke","onSuccess:$it")
                goodsListData.postValue(it)
            }.onFailure {
                goodsListData.postValue(it)
                Log.e("wenke","onFailure:$it")
            }
        }
    }
    /**
     * 获取商品详情数据
    * */
    fun queryGoodsDetails(goodsId:String){
        viewModelScope.launch {
            fetchRequest {
                body.clear()
                body["goodsId"] = goodsId
                val rkey = getRandomKey()
                shopApiService.queryGoodsDetails(body.header(rkey), body.body(rkey))
            }.onSuccess {
                goodsItemData.postValue(it)
            }.onFailure {
            }
        }
    }
}