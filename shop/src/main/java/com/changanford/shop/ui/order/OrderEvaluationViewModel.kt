package com.changanford.shop.ui.order

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.changanford.shop.bean.OrderFormState

/**
 * @Author : wenke
 * @Time : 2021/9/9 0009
 * @Description : OrderViewModel
 */
class OrderEvaluationViewModel:ViewModel() {
    private val _orderForm = MutableLiveData<OrderFormState>()
    val orderFormState: LiveData<OrderFormState> = _orderForm
    fun evalutionDataChanged(content:String?) {
        if(null!=content)_orderForm.value=OrderFormState(content,content.length,true)
        else _orderForm.value=OrderFormState(null,0,false)
    }
}