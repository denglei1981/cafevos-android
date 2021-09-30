package com.changanford.shop.viewmodel

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.changanford.common.MyApp
import com.changanford.common.bean.GoodsItemBean
import com.changanford.common.bean.GoodsList
import com.changanford.common.bean.SeckillSessionsBean
import com.changanford.common.net.*
import com.changanford.common.utilext.createHashMap
import com.changanford.shop.base.BaseViewModel
import com.changanford.shop.listener.OnPerformListener
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
    //秒杀时段
    var seckillSessionsData =MutableLiveData<SeckillSessionsBean>()
    var killGoodsListData =MutableLiveData<GoodsList?>()
    private var pageSize=20
    /**
     * 获取商品列表
     * [typeId]分类id
     * */
    fun getGoodsList(typeId:String,pageNo:Int,pageSize:Int=this.pageSize){
        viewModelScope.launch {
            fetchRequest {
                body.clear()
                body["pageNo"]=pageNo
                body["pageSize"]=pageSize
//                body["tagId"]=typeId
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
    /**
     * 获取秒杀时段
     * */
    fun getSckills(){
        viewModelScope.launch {
            fetchRequest {
                body.clear()
                val randomKey = getRandomKey()
                shopApiService.getSckills(body.header(randomKey), body.body(randomKey))
            }.onSuccess {
                seckillSessionsData.postValue(it)
            }
        }
    }
    /**
     * 获取秒杀列表
     * [seckillRangeId]时段id
     * */
    fun getGoodsKillList(seckillRangeId:String,pageNo:Int,pageSize:Int=this.pageSize){
        viewModelScope.launch {
            fetchRequest {
                body.clear()
                body["pageNo"]=pageNo
                body["pageSize"]=pageSize
//                body["tagId"]=typeId
                val randomKey = getRandomKey()
                shopApiService.getGoodsKillList(seckillRangeId,body.header(randomKey), body.body(randomKey))
            }.onSuccess {
                killGoodsListData.postValue(it)
            }.onFailure {
                killGoodsListData.postValue(it)
            }
        }
    }
    /**
     * 秒杀提醒设置/取消
     * [states]SET,CANCEL
     * */
    fun setKillNotices(states:String,rangeId:String,listener: OnPerformListener){
        viewModelScope.launch {
            fetchRequest {
                body.clear()
                body["setCancel"]=states
                val randomKey = getRandomKey()
                shopApiService.setKillNotices(rangeId,body.header(randomKey), body.body(randomKey))
            }.onSuccess {
                listener.onFinish(0)
            }.onFailure {
                listener.onFinish(-1)
            }
        }
    }
}