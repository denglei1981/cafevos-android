package com.changanford.shop.viewmodel

import android.text.TextUtils
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.changanford.common.net.*
import com.changanford.shop.base.BaseViewModel
import com.changanford.shop.base.ResponseBean
import com.changanford.shop.bean.OrderFormState
import kotlinx.coroutines.launch

/**
 * @Author : wenke
 * @Time : 2021/9/9
 * @Description : OrderViewModel
 */
class OrderEvaluationViewModel: BaseViewModel(){
    private val _orderForm = MutableLiveData<OrderFormState>()
    val orderFormState: LiveData<OrderFormState> = _orderForm
    /**
     * [content]评论内容
     * [rating]评分等级；1-2分为差评，3分为中评，4-5分为好评
    * */
    fun evalDataChanged(content:String?,rating:Int) {
        if(!TextUtils.isEmpty(content))_orderForm.value=OrderFormState(content,content?.length!!,rating>0,rating)
        else _orderForm.value=OrderFormState(null,0,false,rating)
    }
    /**
     * 订单评价
     * [mallMallOrderId]订单id
     * [evalScore]评分(满分5分)
     *[anonymous]是否匿名,可用值:YesNoNumInDBEnum.YES,YesNoNumInDBEnum.NO
     *[evalText]评价内容
     * */
    fun orderEval(mallMallOrderId:String,evalScore:Int,anonymous:String?,evalText:String){
        viewModelScope.launch {
            fetchRequest {
                body.clear()
                body["mallMallOrderId"] = mallMallOrderId
                body["evalScore"] = evalScore
                body["evalText"] = evalText
                body["anonymous"] = anonymous?:"YesNoNumInDBEnum.NO"
                body["evalType"] = "MallEvalTypeEnum.CONSUMER"
                val rkey = getRandomKey()
                shopApiService.orderEval(body.header(rkey), body.body(rkey))
            }.onSuccess {
                responseData.postValue(ResponseBean(true))
            }.onFailure {
                responseData.postValue(ResponseBean(false))
            }
        }
    }
}