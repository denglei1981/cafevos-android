package com.changanford.evos

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.changanford.common.net.CommonResponse
import com.changanford.common.net.NetWorkApi
import com.changanford.common.net.body
import com.changanford.common.net.fetchRequest
import com.changanford.common.net.getRandomKey
import com.changanford.common.net.header
import com.changanford.common.util.MConstant
import com.changanford.common.utilext.logE
import com.changanford.evos.bean.MainPopBean
import com.changanford.home.api.HomeNetWork
import kotlinx.coroutines.async
import kotlinx.coroutines.launch

/**
 *Author lcw
 *Time on 2023/3/10
 *Purpose
 */
class PopViewModel : ViewModel() {

    val popBean = MutableLiveData<MainPopBean>()

    fun getPopData(
        isUpdate: Boolean = true,
        isHoldCircle: Boolean = true,
        isGetIntegral: Boolean = true,
        isReceiveList: Boolean = true,
        isNewEstOne: Boolean = true,
        isBizCode: Boolean = true
    ) {
        viewModelScope.launch {
            try {
                val updateInfo = async {
                    if (isUpdate) {
                        //更新弹窗
                        val body = HashMap<String, Any>()
                        body["type"] = 0
                        val rKey = getRandomKey()
                        fetchRequest {
                            apiService.getUpdateInfo(body.header(rKey), body.body(rKey))
                        }
                    } else {
                        CommonResponse(data = null, msg = "", code = 1)
                    }
                }

                val holdCirclePopInfo = async {
                    if (MConstant.token.isEmpty()) return@async CommonResponse(
                        data = false,
                        msg = "",
                        code = 1
                    )
                    if (isHoldCircle) {
                        //保留圈子弹窗
                        val body = HashMap<String, Any>()
                        val rKey = getRandomKey()
                        fetchRequest {
                            apiService.showWindow(body.header(rKey), body.body(rKey))
                        }
                    } else {
                        CommonResponse(data = null, msg = "", code = 1)
                    }
                }

                val integral = async {
                    //是否有可领取的微客服小程序积分
                    if (MConstant.token.isEmpty()) return@async CommonResponse(
                        data = null,
                        msg = "",
                        code = 1
                    )
                    if (isGetIntegral) {
                        val body = HashMap<String, Any>()
                        val randomKey = getRandomKey()
                        fetchRequest {
                            createApi<HomeNetWork>()
                                .isGetIntegral(body.header(randomKey), body.body(randomKey))
                        }
                    } else {
                        CommonResponse(data = null, msg = "", code = 1)
                    }

                }

                val receive = async {
                    //优惠券弹窗
                    if (MConstant.token.isEmpty()) return@async CommonResponse(
                        data = null,
                        msg = "",
                        code = 1
                    )
                    if (isReceiveList) {
                        val body = HashMap<String, Any>()
                        val randomKey = getRandomKey()
                        body["popup"] = "YES"
                        fetchRequest {
                            createApi<HomeNetWork>()
                                .receiveList(body.header(randomKey), body.body(randomKey))
                        }
                    } else {
                        CommonResponse(data = null, msg = "", code = 1)
                    }
                }

                val bizCode = async {
                    //隐私协议更新弹窗
                    if (isBizCode) {
                        val ids = MConstant.agreementPrivacy + "," + MConstant.agreementRegister
                        val requestBody = HashMap<String, Any>()
                        requestBody["bizCodes"] = ids
                        val rKey = getRandomKey()
                        fetchRequest {
                            createApi<NetWorkApi>()
                                .bizCode(requestBody.header(rKey), requestBody.body(rKey))
                        }
                    } else {
                        CommonResponse(data = null, msg = "", code = 1)
                    }
                }

                val popRule = async {
                    //首页弹窗配置获取
                    if (isNewEstOne) {
                        val requestBody = HashMap<String, Any>()
                        val rKey = getRandomKey()
                        fetchRequest {
                            createApi<HomeNetWork>()
                                .popRule(requestBody.header(rKey), requestBody.body(rKey))
                        }
                    } else {
                        CommonResponse(data = null, msg = "", code = 1)
                    }
                }

                val newEstOne = async {
                    //广告弹窗
                    if (isNewEstOne) {
                        val body = HashMap<String, Any>()
                        val randomKey = getRandomKey()
                        body["posCode"] = "index_popover"
                        fetchRequest {
                            createApi<HomeNetWork>()
                                .newEstOne(body.header(randomKey), body.body(randomKey))
                        }
                    } else {
                        CommonResponse(data = null, msg = "", code = 1)
                    }
                }

                val updateResult = updateInfo.await()
                val holdCircleResult = holdCirclePopInfo.await()
                val integralResult = integral.await()
                val receiveResult = receive.await()
                val newEstOneResult = newEstOne.await()
                val bizCodeResult = bizCode.await()
                val popRuleResult = popRule.await()

                val mainPopBean = MainPopBean(
                    updateResult.data,
                    integralResult.data,
                    receiveResult.data,
                    newEstOneResult.data,
                    bizCodeResult.data,
                    popRuleResult.data,
                    holdCircleResult.data
                )
                popBean.value = mainPopBean
            } catch (error: Throwable) {
                error.message?.logE()
            }
        }
    }

}