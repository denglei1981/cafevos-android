package com.changanford.shop.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.changanford.common.MyApp
import com.changanford.common.bean.*
import com.changanford.common.listener.OnPerformListener
import com.changanford.common.net.*
import com.changanford.common.repository.AdsRepository
import com.changanford.common.util.MConstant
import com.changanford.common.util.MineUtils
import com.changanford.common.util.toast.ToastUtils
import com.changanford.common.utilext.toast
import com.changanford.shop.R
import com.changanford.shop.base.BaseViewModel
import com.changanford.shop.base.ResponseBean
import com.changanford.shop.utils.WConstant
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
    var goodsListData =MutableLiveData<GoodsListBean?>()
    //秒杀时段
    var seckillSessionsData =MutableLiveData<SeckillSessionsBean>()
    //秒杀列表
    var killGoodsListData =MutableLiveData<GoodsListBean?>()
    //评价列表
    var commentLiveData =MutableLiveData<CommentBean?>()
    //商品收藏状态
    var collectionGoodsStates = MutableLiveData<Boolean>()
    //分类列表
    var typesBean = MutableLiveData<MutableList<GoodsTypesItemBean>>()
    //商品列表
    var GoodsListBean = MutableLiveData<MutableList<GoodsItemBean>?>()
    /**
     * 获取banner
     * */
    fun getBannerData(){
        adsRepository.getAds("mall_top_ad_v2")
    }
    /**
     * 获取秒杀banner
     * */
    fun getKillBannerData(){
        adsRepository.getAds("recommend_seckill")
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
     * [ascOrDesc]正序(从小到大)/倒序(从大到小)(综合排序情况下，传倒序),可用值:AscOrDescEnum.ASC(code=ASC, dbCode=0, message=正序),AscOrDescEnum.DESC(code=DESC, dbCode=1, message=倒序)
     * [mallSortType]	排序规则,可用值:MallSortTypeEnum.COMPREHENSIVE(code=COMPREHENSIVE, message=综合排序),MallSortTypeEnum.SALES(code=SALES, message=销量排序),MallSortTypeEnum.PRICE(code=PRICE, message=价格排序)
     * */
    fun getGoodsList(tagId:String,pageNo:Int,tagType:String?=null,pageSize:Int=this.pageSize,ascOrDesc:String="DESC",mallSortType:String="COMPREHENSIVE"){
        if("WB"==tagType){//获取维保商品数据
            getMaintenanceGoodsList(tagId,pageNo,pageSize)
            return
        }
        viewModelScope.launch {
            fetchRequest {
                body.clear()
                body["pageNo"]=pageNo
                body["pageSize"]=pageSize
                body["queryParams"]=HashMap<String,Any>().also {
                    it["tagId"]=tagId
                    it["ascOrDesc"]=ascOrDesc
                    it["mallSortType"]=mallSortType
                }
                val randomKey = getRandomKey()
                shopApiService.queryGoodsList(body.header(randomKey), body.body(randomKey))
            }.onSuccess {
                goodsListData.postValue(it)
            }.onFailure {
                goodsListData.postValue(null)
            }.onWithMsgFailure {
                if(null!=it)ToastUtils.showLongToast(it,MyApp.mContext)
            }
        }
    }
    /**
     * 维保商品
     * [tagId]分类id
     * */
   private fun getMaintenanceGoodsList(tagId:String,pageNo:Int,pageSize:Int=this.pageSize){
        viewModelScope.launch {
            fetchRequest {
                body.clear()
                body["pageNo"]=pageNo
                body["pageSize"]=pageSize
                body["queryParams"]=HashMap<String,Any>().also {
                    it["tagId"]=tagId
                }
                val randomKey = getRandomKey()
                shopApiService.maintenanceGoodsList(body.header(randomKey), body.body(randomKey))
            }.onSuccess {
                goodsListData.postValue(it)
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
    fun queryGoodsDetails(spuId:String,showLoading: Boolean = false){
        viewModelScope.launch {
            fetchRequest(showLoading) {
                body.clear()
                val randomKey = getRandomKey()
                shopApiService.queryGoodsDetails(spuId,body.header(randomKey), body.body(randomKey))
            }.onWithMsgFailure {
//                ToastUtils.showLongToast(it,MyApp.mContext)
                responseData.postValue(ResponseBean(false,msg = it))
            }.onSuccess {
//                addFootprint(spuId)
                it?.apply {
                   val goodsDetailsBean=normalSpuDetail?:seckillSpuDetail?:haggleSpuDetailDto
                    goodsDetailData.postValue(goodsDetailsBean)
                }
            }
        }
    }
    /**
     * 获取秒杀时段
     * */
    fun getSckills(){
        viewModelScope.launch {
            fetchRequest(true) {
                body.clear()
                val randomKey = getRandomKey()
                shopApiService.getSckills(body.header(randomKey), body.body(randomKey))
            }.onSuccess {
                seckillSessionsData.postValue(it)
            }.onWithMsgFailure {
                ToastUtils.showLongToast(it)
            }
        }
    }
    /**
     * 获取秒杀列表
     * [seckillRangeId]时段id
     * */
    fun getGoodsKillList(seckillRangeId:String,pageNo:Int,pageSize:Int=this.pageSize,showLoading: Boolean = false){
        viewModelScope.launch {
            fetchRequest(showLoading){
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
    fun getGoodsEvalList(spuId:String,pageNo:Int,spuPageType:String?=null,pageSize:Int=this.pageSize){
        viewModelScope.launch {
            fetchRequest {
                body.clear()
                body["pageNo"]=pageNo
                body["pageSize"]=pageSize
                body["queryParams"]=HashMap<String,Any>().also {
                    if(WConstant.maintenanceType==spuPageType){
                        it["mallWbGoodsId"] = spuId
                    }
                    it["mallMallSpuId"] = spuId
                }
                val randomKey = getRandomKey()
                if(WConstant.maintenanceType==spuPageType)shopApiService.goodsEvalListWb(body.header(randomKey), body.body(randomKey))
                else shopApiService.goodsEvalList(body.header(randomKey), body.body(randomKey))
            }.onSuccess {
                commentLiveData.postValue(it)
            }.onWithMsgFailure {
                commentLiveData.postValue(null)
            }
        }
    }
    /**
     * 添加足迹
     * */
    private fun addFootprint(spuId:String){
        if(MConstant.token.isEmpty())return
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
    fun collectGoods(spuId:String){
        if(!isLogin())return
        if(MineUtils.getBindMobileJumpDataType(true))return
        viewModelScope.launch {
            fetchRequest {
                body.clear()
                val randomKey = getRandomKey()
                shopApiService.collectGoods(spuId,body.header(randomKey), body.body(randomKey))
            }.onWithMsgFailure {
                ToastUtils.showLongToast(it)
            }.onSuccess {
                ToastUtils.reToast(R.string.str_collectionSuccess)
                collectionGoodsStates.postValue(true)
            }
        }
    }
    /**
     * 取消收藏商品
     * [spuId]商品id
     * */
    fun cancelCollectGoods(spuId:String){
        if(!isLogin())return
        if(MineUtils.getBindMobileJumpDataType(true))return
        viewModelScope.launch {
            fetchRequest {
                body.clear()
                body["collectionType"]=5
                body["collectionContentIds"]= arrayOf(spuId)
                val randomKey = getRandomKey()
                shopApiService.cancelCollectGoods(body.header(randomKey), body.body(randomKey))
            }.onWithMsgFailure {
                ToastUtils.showLongToast(it)
            }.onSuccess {
                ToastUtils.reToast(R.string.str_cancelledCollection)
                collectionGoodsStates.postValue(false)
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

    /**
     * 获取推荐榜单分类
     * */
    fun getRecommendTypes(){
        viewModelScope.launch {
            fetchRequest {
                body.clear()
                val randomKey = getRandomKey()
                shopApiService.getRecommendTypes(body.header(randomKey), body.body(randomKey))
            }.onWithMsgFailure {
                it?.toast()
            }.onSuccess {
                typesBean.postValue(it)
            }
        }
    }
    /**
     * 获取推荐榜单分类
     * */
    fun getRecommendList(kindId:String,showLoading:Boolean=false){
        viewModelScope.launch {
            fetchRequest(showLoading){
                body.clear()
                body["mallMallRecommendKindId"]=kindId
                val randomKey = getRandomKey()
                shopApiService.getRecommendList(body.header(randomKey), body.body(randomKey))
            }.onWithMsgFailure {
                it?.toast()
                GoodsListBean.postValue(null)
            }.onSuccess {
                GoodsListBean.postValue(it)
            }
        }
    }
    /**
     * 加入购物车
     * [mallMallSpuId]商品表id
     * [skuId]sku表id
     * [fbPer]单位原价(不计算折扣)(积分)
     * [num]购买数量
     * */
    fun addShoppingCart(mallMallSpuId:String,skuId:String,fbPer:String,num:Int,listener: OnPerformListener?=null,showLoading:Boolean=false){
        viewModelScope.launch {
            fetchRequest(showLoading){
                body.clear()
                body["mallMallSpuId"]=mallMallSpuId
                body["skuId"]=skuId
                body["fbPer"]=fbPer
                body["num"]=num
                val randomKey = getRandomKey()
                shopApiService.addShoppingCart(body.header(randomKey), body.body(randomKey))
            }.onWithMsgFailure {
                it?.toast()
            }.onSuccess {
                listener?.onFinish(0)
            }
        }
    }
}