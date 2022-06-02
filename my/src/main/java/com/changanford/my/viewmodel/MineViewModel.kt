package com.changanford.my.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.changanford.common.basic.BaseViewModel
import com.changanford.common.bean.*
import com.changanford.common.manger.UserManger
import com.changanford.common.net.*
import com.changanford.common.net.response.UpdateUiState
import com.changanford.common.util.MConstant
import com.changanford.common.util.SafeMutableLiveData
import com.changanford.common.utilext.toast
import com.changanford.common.utilext.toastShow
import com.changanford.my.R
import com.changanford.my.bean.MineMenuData
import kotlinx.coroutines.launch

class MineViewModel : BaseViewModel() {


    var menuBean = MutableLiveData<ArrayList<MenuBeanItem>>()
    fun getMenuList() {
        viewModelScope.launch {
            fetchRequest {
                val body = HashMap<String, String>()
                val rkey = getRandomKey()
                apiService.queryMenuList(body.header(rkey), body.body(rkey))
            }.onSuccess {
                menuBean.postValue(it)
            }
        }
    }

    var userInfo: MutableLiveData<UserInfoBean> = MutableLiveData()
    fun getUserInfo() {
        if (UserManger.isLogin()) {
            viewModelScope.launch {
                fetchRequest {
                    val body = HashMap<String, String>()
                    val rkey = getRandomKey()
                    apiService.queryUserInfo(body.header(rkey), body.body(rkey))
                }.onSuccess {
                    it?.let {
                        saveUserInfo(it)
                    }
                }.onFailure {
                    saveUserInfo(null)
                }
            }
        } else {
            saveUserInfo(null)
        }
    }

    private fun saveUserInfo(userInfoBean: UserInfoBean?) {
        userInfo.postValue(userInfoBean)
        UserManger.updateUserInfo(userInfoBean)
    }

    //认证信息
    val carAuthBean = MutableLiveData<CarAuthBean?>()

    /**
     * 认证信息
     * */
    fun getAuthCarInfo() {
        viewModelScope.launch {
            fetchRequest {
                val hashMap = HashMap<String, Any>()
                val randomKey = getRandomKey()
                apiService.queryAuthCarList(hashMap.header(randomKey), hashMap.body(randomKey))
            }.onSuccess {
                carAuthBean.postValue(it)
            }.onWithMsgFailure {
                carAuthBean.postValue(null)
                it?.toast()
            }
        }
    }
    val recommdCircleLiveData = MutableLiveData<MutableList<MineRecommendCircle>>()
    fun getCircleInfo() {
        viewModelScope.launch {
            fetchRequest {
                val hashMap = HashMap<String, Any>()
                val randomKey = getRandomKey()
                apiService.carRecommend(hashMap.header(randomKey), hashMap.body(randomKey))
            }.onSuccess {
                recommdCircleLiveData.postValue(it)
            }.onWithMsgFailure {
                it?.toast()
            }
        }
    }



    val  updateCarAdLiveData  = MutableLiveData<UpdateUiState<List<MineMenuData>>>()
    fun getCarListAds( allList:ArrayList<MineMenuData>) {
        launch(false, {
            val body = HashMap<String, Any>()
            val rkey = getRandomKey()
            body["posCode"] = "my_info_car"
            ApiClient.createApi<NetWorkApi>()
                .getAdList(body.header(rkey), body.body(rkey))
                .onSuccess {
//                    carListLiveData.postValue(it)
                    val list = arrayListOf<MenuBeanItem>()
                    it?.let {l->
                    l.forEach { d->
                        list.add(MenuBeanItem(icon=d.getAdImgUrl(),menuName = d.adName,jumpDataType = d.jumpDataType,jumpDataValue=d.jumpDataValue))
                    }
                        val menu = MineMenuData("", list)
                        allList.add(0,menu)
                        val updateUiState =UpdateUiState<List<MineMenuData>>(allList,true,"")
                        updateCarAdLiveData.postValue(updateUiState)
                    }

                }.onWithMsgFailure {
                    val updateUiState =UpdateUiState<List<MineMenuData>>(allList,false,"")
                    updateCarAdLiveData.postValue(updateUiState)
                }
        })
    }
    //广告
    val adListLiveData = MutableLiveData<ArrayList<AdBean>?>()
    fun getBottomAds() {
        launch(false, {
            val body = HashMap<String, Any>()
            val rkey = getRandomKey()
            body["posCode"] = "my_info_ads"
            ApiClient.createApi<NetWorkApi>()
                .getAdList(body.header(rkey), body.body(rkey))
                .onSuccess {
                    adListLiveData.postValue(it)
                }.onWithMsgFailure {
//                    if (it != null) {
//                        toastShow(it)
//                    }
                }
        })
    }

    fun myOrders(): MineMenuData {
        val orderItemBeanList = mutableListOf<MenuBeanItem>()
        val menuItemBean = MenuBeanItem(drawInt = R.mipmap.icon_mine_order_car, menuName = "购车订单",jumpDataType = 1,jumpDataValue = MConstant.H5_BASE_URL.plus("/order/#/vehicleOrder/vehicleOrder") )
        orderItemBeanList.add(menuItemBean)

//        val menuItemBean01 = MenuBeanItem(drawInt = R.mipmap.icon_mine_service, menuName = "服务订单")
//        orderItemBeanList.add(menuItemBean01)

        val menuItemBean02 =
            MenuBeanItem(drawInt = R.mipmap.icon_mine_order_shop, menuName = "商品订单",jumpDataType = 52,jumpDataValue = "")
        orderItemBeanList.add(menuItemBean02)
        val menu = MineMenuData("我的订单", orderItemBeanList)

        return menu
    }



    /**
     * 获取订单类型
     * */
    //订单类型
    var  mOrderTypesLiveData: MutableLiveData<MutableList<MenuBeanItem>> = MutableLiveData()
    val  updateOrderAdLiveData  = MutableLiveData<UpdateUiState<List<MineMenuData>>>()
    fun getOrderKey( allList:ArrayList<MineMenuData>){
        viewModelScope.launch {
            fetchRequest {
                body.clear()
                val randomKey = getRandomKey()
                apiService.getOrderKey(body.header(randomKey), body.body(randomKey))
            }.onWithMsgFailure {
                val updateUiState =UpdateUiState<List<MineMenuData>>(allList,false,"")
                updateOrderAdLiveData.postValue(updateUiState)
            }.onSuccess {
                val menu = it?.let { it1 -> MineMenuData("我的订单", it1) }
                if (menu != null) {
                    allList.add(0,menu)
                }
                val updateUiState =UpdateUiState<List<MineMenuData>>(allList,true,"")
                updateOrderAdLiveData.postValue(updateUiState)

            }
        }
    }
}