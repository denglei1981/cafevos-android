package com.changanford.shop.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.changanford.common.MyApp
import com.changanford.common.bean.*
import com.changanford.common.net.*
import com.changanford.common.repository.AdsRepository
import com.changanford.common.util.toast.ToastUtils
import com.changanford.shop.R
import com.changanford.shop.base.BaseViewModel
import com.changanford.shop.listener.OnPerformListener
import kotlinx.coroutines.launch

/**
 * @Author : wenke
 * @Time : 2021/9/9 0009
 * @Description : GoodsViewModel
 */
class GoodsViewModel: BaseViewModel() {
    private var adsRepository: AdsRepository = AdsRepository(this)
    //广告
    var advertisingList: MutableLiveData<ArrayList<AdBean>> = adsRepository._ads
    //商品分类
    var classificationLiveData =MutableLiveData<GoodsClassification?>()
    //首页
    var shopHomeData =MutableLiveData<ShopHomeBean>()

    var goodsDetailData: MutableLiveData<GoodsDetailBean> = MutableLiveData()
    //商品列表
    var goodsListData =MutableLiveData<GoodsList?>()
    //秒杀时段
    var seckillSessionsData =MutableLiveData<SeckillSessionsBean>()
    //秒杀列表
    var killGoodsListData =MutableLiveData<GoodsList?>()
    //评价列表
    var commentLiveData =MutableLiveData<CommentBean?>()
    //商品收藏状态
    var collectionGoodsStates = MutableLiveData<Boolean>()
    /**
     * 获取banner
     * */
    fun getBannerData(){
        adsRepository.getAds("mall_top_ad_v2")
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
                shopHomeData.postValue(it)
            }.onWithMsgFailure {
                ToastUtils.showLongToast(it?:"", MyApp.mContext)
            }
        }
    }
    /**
     * 获取商品分类
     * */
    fun getClassification(){
        viewModelScope.launch {
            fetchRequest {
                body.clear()
                val randomKey = getRandomKey()
                shopApiService.getClassification(body.header(randomKey), body.body(randomKey))
            }.onSuccess {
                classificationLiveData.postValue(it)
            }.onWithMsgFailure {
                classificationLiveData.postValue(null)
                ToastUtils.showLongToast(it?:"", MyApp.mContext)
            }
        }
    }
    /**
     * 获取商品列表
     * [tagId]分类id
     * */
    fun getGoodsList(tagId:String,pageNo:Int,pageSize:Int=this.pageSize){
        viewModelScope.launch {
            fetchRequest {
                body.clear()
                body["pageNo"]=pageNo
                body["pageSize"]=pageSize
                body["queryParams"]=HashMap<String,Any>().also {
                    it["tagId"]=tagId
                }
                val randomKey = getRandomKey()
                shopApiService.queryGoodsList(body.header(randomKey), body.body(randomKey))
            }.onSuccess {
                goodsListData.postValue(it?.responsePageBean)
            }.onFailure {
                goodsListData.postValue(null)
            }.onWithMsgFailure {
                if(null!=it)ToastUtils.showLongToast(it,MyApp.mContext)
            }
        }
    }
    /**
     * 获取商品详情数据
     * [spuId]商品id  108
    * */
    fun queryGoodsDetails(spuId:String){
        viewModelScope.launch {
            fetchRequest {
                body.clear()
                val randomKey = getRandomKey()
                shopApiService.queryGoodsDetails(spuId,body.header(randomKey), body.body(randomKey))
            }.onWithMsgFailure {
                ToastUtils.showLongToast(it,MyApp.mContext)
            }.onSuccess {
                addFootprint(spuId)
//                if(BuildConfig.DEBUG&&it?.acountFb!!<1) it.acountFb =1000
                goodsDetailData.postValue(it)
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
        if(!isLogin())return
        viewModelScope.launch {
            fetchRequest {
                body.clear()
                body["setCancel"]=states
//                body["dto"]=HashMap<String,Any>().also {
//                    it["setCancel"]=states
//                }
                val randomKey = getRandomKey()
                shopApiService.setKillNotices(rangeId,body.header(randomKey), body.body(randomKey))
            }.onSuccess {
                listener.onFinish(0)
            }.onWithMsgFailure {
                ToastUtils.showLongToast(it)
            }
        }
    }
    /**
     * 评价列表
     * */
    fun getGoodsEvalList(spuId:String,pageNo:Int,pageSize:Int=this.pageSize){
        if(!isLogin())return
        viewModelScope.launch {
            fetchRequest {
                body.clear()
                body["pageNo"]=pageNo
                body["pageSize"]=pageSize
                body["queryParams"]=HashMap<String,Any>().also {
                    it["spuId"] = spuId
                }
                val randomKey = getRandomKey()
                shopApiService.goodsEvalList(body.header(randomKey), body.body(randomKey))
            }.onSuccess {
                commentLiveData.postValue(it)
            }
        }
    }
    /**
     * 添加足迹
     * */
    private fun addFootprint(spuId:String){
//        if(!isLogin())return
        viewModelScope.launch {
            fetchRequest {
                body.clear()
                val randomKey = getRandomKey()
                shopApiService.addFootprint(spuId,body.header(randomKey), body.body(randomKey))
            }
        }
   }
    /**
     * 收藏商品
     * [spuId]商品id
     * */
    fun collectGoods(spuId:String,isCollection:Boolean){
        if(!isLogin())return
        viewModelScope.launch {
            fetchRequest {
                body.clear()
                val randomKey = getRandomKey()
                shopApiService.collectGoods(spuId,body.header(randomKey), body.body(randomKey))
            }.onWithMsgFailure {
                ToastUtils.showLongToast(it,MyApp.mContext)
            }.onSuccess {
                ToastUtils.showShortToast(if(isCollection)R.string.str_cancelledCollection else R.string.str_collectionSuccess,MyApp.mContext)
                collectionGoodsStates.postValue(!isCollection)
            }
        }
    }
    /**
     * 获取我的积分
     * */
    fun getMyIntegral(){
        if(!isLogin())return
        viewModelScope.launch {
            fetchRequest {
                body.clear()
                val randomKey = getRandomKey()
                shopApiService.getMyIntegral(body.header(randomKey), body.body(randomKey))
            }.onWithMsgFailure {
                ToastUtils.showLongToast(it,MyApp.mContext)
            }.onSuccess {

            }
        }
    }
}