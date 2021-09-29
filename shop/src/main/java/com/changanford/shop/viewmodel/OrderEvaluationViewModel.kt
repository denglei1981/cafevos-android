package com.changanford.shop.viewmodel

import android.text.TextUtils
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.changanford.shop.bean.OrderFormState

/**
 * @Author : wenke
 * @Time : 2021/9/9
 * @Description : OrderViewModel
 */
class OrderEvaluationViewModel:ViewModel() {
    private val _orderForm = MutableLiveData<OrderFormState>()
    val orderFormState: LiveData<OrderFormState> = _orderForm
    /**
     * [content]评论内容
     * [rating]评分等级；1-2分为差评，3分为中评，4-5分为好评
    * */
    fun evalutionDataChanged(content:String?,rating:Int) {
        if(!TextUtils.isEmpty(content))_orderForm.value=OrderFormState(content,content?.length!!,rating>0,rating)
        else _orderForm.value=OrderFormState(null,0,false,rating)
    }
}