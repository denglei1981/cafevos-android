package com.changanford.shop.viewmodel

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.changanford.common.MyApp
import com.changanford.common.bean.*
import com.changanford.common.net.*
import com.changanford.common.repository.AdsRepository
import com.changanford.common.util.toast.ToastUtils
import com.changanford.shop.base.BaseViewModel
import com.changanford.shop.listener.OnPerformListener
import kotlinx.coroutines.launch

/**
 * @Author : wenke
 * @Time : 2021/9/9 0009
 * @Description : GoodsViewModel
 */
class GoodsViewModel: BaseViewModel() {
    private var pageSize=20
    private var adsRepository: AdsRepository = AdsRepository(this)
    //广告
    var advertisingList: MutableLiveData<ArrayList<AdBean>> = adsRepository._ads
    //秒杀
    var KillListData =MutableLiveData<MutableList<GoodsItemBean>>()

    var goodsDetailData: MutableLiveData<GoodsDetailBean> = MutableLiveData()
    //商品列表
    var goodsListData =MutableLiveData<GoodsList?>()
    //秒杀时段
    var seckillSessionsData =MutableLiveData<SeckillSessionsBean>()
    //秒杀列表
    var killGoodsListData =MutableLiveData<GoodsList?>()
    //商城首页
    var shopHomeData =MutableLiveData<GoodsHomeBean>()
    /**
     * 获取banner
     * */
    fun getBannerData(){
        adsRepository.getAds("商城广告位")
    }
    /**
     * 获取 商城首页
     * */
    fun getShopHomeData(){
        viewModelScope.launch {
            fetchRequest {
                body.clear()
                val randomKey = getRandomKey()
                shopApiService.queryShopHomeData(body.header(randomKey), body.body(randomKey))
            }.onSuccess {
                KillListData.postValue(it)
            }.onWithMsgFailure {
                ToastUtils.showLongToast(it?:"", MyApp.mContext)
            }
        }
    }
    /**
     * 获取商品列表
     * [typeId]分类id
     * */
    fun getGoodsList(typeId:String,pageNo:Int,pageSize:Int=this.pageSize){
        Log.e("okhttp","typeId:$typeId")
        viewModelScope.launch {
            fetchRequest {
                body.clear()
                body["pageNo"]=pageNo
                body["pageSize"]=pageSize
                body["queryParams"]=HashMap<String,Any>().also {
                    it["tagId"]=typeId
                }
                val randomKey = getRandomKey()
                shopApiService.queryGoodsList(body.header(randomKey), body.body(randomKey))
            }.onSuccess {
                shopHomeData.postValue(it)
                goodsListData.postValue(it?.responsePageBean)
            }.onFailure {
                shopHomeData.postValue(it)
            }.onWithMsgFailure {
                if(null!=it)ToastUtils.showLongToast(it,MyApp.mContext)
            }
        }
    }
    /**
     * 获取商品详情数据
     * [spuId]商品id
     * [spuPageType] 商品类型,可用值:NOMROL,SECKILL,MEMBER_EXCLUSIVE,MEMBER_DISCOUNT
    * */
    fun queryGoodsDetails(spuId:String,spuPageType:String="NOMROL"){
        viewModelScope.launch {
            fetchRequest {
                body.clear()
                body["spuPageType"] = spuPageType
                val rkey = getRandomKey()
                shopApiService.queryGoodsDetails(spuId,body.header(rkey), body.body(rkey))
            }.onSuccess {
                goodsDetailData.postValue(it)
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