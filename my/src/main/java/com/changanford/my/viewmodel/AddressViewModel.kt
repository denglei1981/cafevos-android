package com.changanford.my.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.changanford.common.bean.AddressBeanItem
import com.changanford.common.bean.CityBeanItem
import com.changanford.common.net.*
import kotlinx.coroutines.launch

/**
 *  文件名：AddressViewmodel
 *  创建者: zcy
 *  创建日期：2021/9/26 9:19
 *  描述: TODO
 *  修改描述：TODO
 */
class AddressViewModel : ViewModel() {

    /**
     * 获取所以区域
     */

    var allCity: MutableLiveData<ArrayList<CityBeanItem>> = MutableLiveData()

    fun getAllCity() {
        viewModelScope.launch {
            fetchRequest {
                var body = HashMap<String, String>()
                body["district"] = "true"
                var rkey = getRandomKey()
                apiService.getAllCity(body.header(rkey), body.body(rkey))
            }.onSuccess {
                allCity.postValue(it)
            }.onFailure {
                allCity.postValue(null)
            }
        }
    }

    /**
     * 获取地址列表
     */
    var addressList: MutableLiveData<ArrayList<AddressBeanItem>> = MutableLiveData()

    fun getAddressList() {
        viewModelScope.launch {
            fetchRequest {
                var body = HashMap<String, String>()
                var rkey = getRandomKey()
                apiService.getAddressList(body.header(rkey), body.body(rkey))
            }.onSuccess {
                addressList.postValue(it)
            }
        }
    }

    /**
     * 保存地址
     */
    var saveAddressStatus: MutableLiveData<String> = MutableLiveData()

    fun saveAddress(map: HashMap<String, Any>) {
        if (null == map) {
            return
        }
        viewModelScope.launch {
            fetchRequest {
                var rkey = getRandomKey()
                apiService.saveAddress(map.header(rkey), map.body(rkey))
            }.onSuccess {
                saveAddressStatus.postValue("true")
            }.onWithMsgFailure {
                saveAddressStatus.postValue(it)
            }
        }
    }

    /**
     * 删除地址
     */
    var deleteAddressStatus: MutableLiveData<String> = MutableLiveData()

    fun deleteAddress(addressId: String) {
        viewModelScope.launch {
            fetchRequest {
                var body = HashMap<String, String>()
                body["addressId"] = addressId
                var rkey = getRandomKey()
                apiService.deleteAddress(body.header(rkey), body.body(rkey))
            }.onSuccess {
                deleteAddressStatus.postValue("true")
            }.onWithMsgFailure {
                deleteAddressStatus.postValue(it)
            }
        }
    }
}