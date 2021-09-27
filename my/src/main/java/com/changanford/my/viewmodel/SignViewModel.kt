package com.changanford.my.viewmodel

import android.content.Context
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.alibaba.sdk.android.oss.model.PutObjectRequest
import com.changanford.common.MyApp
import com.changanford.common.bean.*
import com.changanford.common.manger.UserManger
import com.changanford.common.net.*
import com.changanford.common.ui.dialog.LoadDialog
import com.changanford.common.util.AliYunOssUploadOrDownFileConfig
import com.changanford.common.util.MConstant
import com.changanford.common.util.SPUtils
import com.changanford.common.util.bus.LiveDataBus
import com.changanford.common.util.bus.LiveDataBusKey.USER_LOGIN_STATUS
import com.changanford.common.util.room.UserDatabase
import com.changanford.common.utilext.logE
import com.changanford.common.utilext.toast
import com.changanford.my.interf.UploadPicCallback
import com.huawei.hms.common.ApiException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/**
 *  文件名：SignViewModel
 *  创建者: zcy
 *  创建日期：2021/9/9 16:50
 *  描述: TODO
 *  修改描述：TODO
 */
class SignViewModel : ViewModel() {

    var smsSuccess: MutableLiveData<Boolean> = MutableLiveData()

    val userDatabase: UserDatabase by lazy {
        UserDatabase.getUniUserDatabase(MyApp.mContext)
    }

    fun getSmsCode(mobile: String) {
        viewModelScope.launch {
            fetchRequest {
                var body = HashMap<String, Any>()
                body["phone"] = mobile
                var rKey = getRandomKey()
                apiService.sendFordSmsCode(body.header(rKey), body.body(rKey))
            }.onSuccess {
                smsSuccess.postValue(true)
            }
        }
    }

    fun smsCacLogin(mobile: String) {
        viewModelScope.launch {
            fetchRequest {
                var body = HashMap<String, String>()
                body["phone"] = mobile
                var rkey = getRandomKey()
                apiService.sendCacSmsCode(body.header(rkey), body.body(rkey))
            }.onSuccess {
                smsSuccess.postValue(true)
            }
        }
    }

    fun smsLogin(mobile: String, sms: String, pushId: String) {
        viewModelScope.launch {
            fetchRequest {
                var body = HashMap<String, String>()
                body["phone"] = mobile
                body["smsCode"] = sms
                body["pushId"] = pushId
                var rkey = getRandomKey()
                apiService.smsCodeSign(body.header(rkey), body.body(rkey))
            }.onSuccess {
                loginSuccess(it)
            }
        }
    }


    suspend fun otherLogin(type: String, code: String, pushId: String) {
        var other = fetchRequest {
            var body = HashMap<String, String>()
            body["type"] = type
            body["code"] = code
            body["pushId"] = pushId
            var rkey = getRandomKey()
            apiService.otherOauthSign(body.header(rkey), body.body(rkey))
        }
        if (other.code == 0) {
            other.data?.let {
                loginSuccess(it)
            }
        }
    }

    fun getUserInfo() {
        if (UserManger.isLogin()) {
            viewModelScope.launch {
                fetchRequest {
                    var body = HashMap<String, String>()
                    var rkey = getRandomKey()
                    apiService.queryUserInfo(body.header(rkey), body.body(rkey))
                }.onSuccess {
                    saveUserInfo(it)
                }.onFailure {
                    saveUserInfo(null)
                }
            }
        } else {
            saveUserInfo(null)
        }
    }


    var taskBean: MutableLiveData<List<RootTaskBean>> = MutableLiveData()

    suspend fun queryTasksList() {
        var task = fetchRequest {
            var body = HashMap<String, Any>()
            var rkey = getRandomKey()
            apiService.queryTasksList(body.header(rkey), body.body(rkey))
        }
        if (task.code == 0) {
            taskBean.postValue(task.data)
        }
    }

    var jifenBean: MutableLiveData<GrowUpBean> = MutableLiveData()

    //成长值 2 积分 1
    suspend fun mineGrowUp(pageNo: Int, type: String) {
        var task = fetchRequest {
            var body = HashMap<String, Any>()
            body["pageNo"] = pageNo
            body["pageSize"] = "20"
            body["queryParams"] = mapOf("type" to type)
            var rkey = getRandomKey()
            apiService.mineGrowUp(body.header(rkey), body.body(rkey))
        }
        if (task.code == 0) {
            jifenBean.postValue(task.data)
        }
    }

    val allMedal: MutableLiveData<ArrayList<MedalListBeanItem>> = MutableLiveData()

    suspend fun mineMedal() {
        var medal = fetchRequest {
            var body = HashMap<String, String>()
            var rkey = getRandomKey()
            apiService.queryMedalList(body.header(rkey), body.body(rkey))
        }
        if (medal.code == 0) {
            allMedal.postValue(medal.data)
        } else {
            medal.msg?.logE()
        }
    }


    /**
     *操作类型 1佩戴 2领取
     */
    val wearMedal: MutableLiveData<String> = MutableLiveData()

    suspend fun wearMedal(medalId: String, type: String) {
        var medal = fetchRequest {
            var body = HashMap<String, String>()
            body["medalId"] = medalId
            body["type"] = type
            var rkey = getRandomKey()
            apiService.wearMedal(body.header(rkey), body.body(rkey))
        }
        if (medal.code == 0) {
            wearMedal.postValue("佩戴成功")
        } else {
            wearMedal.postValue(medal.msg)
        }
    }

    val mineMedal: MutableLiveData<ArrayList<MedalListBeanItem>> = MutableLiveData()

    suspend fun oneselfMedal() {
        var medal = fetchRequest {
            var body = HashMap<String, String>()
            var rkey = getRandomKey()
            apiService.queryUserMedalList(body.header(rkey), body.body(rkey))
        }
        if (medal.code == 0) {
            mineMedal.postValue(medal.data)
        } else {
            medal.msg?.logE()
        }
    }


    suspend fun bindMobile(phone: String, smsCode: String) {
        var smsbind = fetchRequest {
            var body = HashMap<String, String>()
            body["phone"] = phone
            body["smsCode"] = smsCode
            var rkey = getRandomKey()
            apiService.bindMobile(body.header(rkey), body.body(rkey))
        }
        if (smsbind.code == 0) {
            smsbind.data?.let {
                loginSuccess(it)
            }
        }
    }

    var fansLive: MutableLiveData<FansListBean> = MutableLiveData()

    suspend fun queryFansList(pageNo: Int, type: Int, userId: String) {
        var fans = fetchRequest {
            var map = mapOf("type" to type, "userId" to userId)
            var body = HashMap<String, Any>()
            body["pageNo"] = pageNo
            body["pageSize"] = 20
            body["queryParams"] = map
            var rkey = getRandomKey()
            apiService.queryFansList(body.header(rkey), body.body(rkey))
        }
        if (fans.code == 0) {
            fansLive.postValue(fans.data)
        }
    }

    var cancelTip: MutableLiveData<String> = MutableLiveData()

    suspend fun cancelFans(
        followId: String,
        type: String
    ) {
        var cancle = fetchRequest {
            var body = HashMap<String, String>()
            body["followId"] = followId
            body["type"] = type
            var rkey = getRandomKey()
            apiService.cancelFans(body.header(rkey), body.body(rkey))
        }
        if (cancle.code == 0) {
            cancelTip.postValue("true")
        } else {
            cancelTip.postValue(cancle.msg)
        }
    }

    var bindAccount: MutableLiveData<ArrayList<BindAuthBeanItem>> = MutableLiveData()
    suspend fun bindAccount() {
        var account = fetchRequest {
            var body = HashMap<String, String>()
            var rKey = getRandomKey()
            apiService.queryBindMobileList(body.header(rKey), body.body(rKey))
        }
        if (account.code == 0) {
            bindAccount.postValue(account.data)
        }
    }


    /**
     * 三方登录
     * 授权类型 min pub weixin qq weibo douyin apple
     * 授权code， qq格式token,openid apple格式 code,userId,fullName
     */
    var bindOtherAccount: MutableLiveData<String> = MutableLiveData()

    suspend fun bindOtherAuth(type: String, code: String) {
        var otherAuth = fetchRequest {
            var body = HashMap<String, String>()
            body["type"] = type
            body["code"] = code
            var rkey = getRandomKey()
            apiService.bindOtherAuth(body.header(rkey), body.body(rkey))
        }
        if (otherAuth.code == 0) {
            bindOtherAccount.postValue("bindSuccess")
        } else {
            bindOtherAccount.postValue(otherAuth.msg)
        }
    }


    /**
     * 三方登录
     * 授权类型 min pub weixin qq weibo douyin apple
     * 授权code， qq格式token,openid apple格式 code,userId,fullName
     */

    suspend fun unBindOtherAuth(type: String) {
        var unOtherAuth = fetchRequest {
            var body = HashMap<String, String>()
            body["type"] = type
            var rkey = getRandomKey()
            apiService.unBindMobile(body.header(rkey), body.body(rkey))
        }
        if (unOtherAuth.code == 0) {
            bindOtherAccount.postValue("unBindSuccess")
        } else {
            bindOtherAccount.postValue(unOtherAuth.msg)
        }
    }


    var clearBean: MutableLiveData<ArrayList<CancelVerifyBean>> = MutableLiveData()

    suspend fun verifyCancelAccount() {
        var clearAccount = fetchRequest {
            var body = HashMap<String, String>()
            var rkey = getRandomKey()
            apiService.verifyCancelAccount(body.header(rkey), body.body(rkey))
        }
        if (clearAccount.code == 0) {
            clearBean.postValue(clearAccount.data)
        }
    }

    var clearAccountReason: MutableLiveData<ArrayList<CancelReasonBeanItem>> = MutableLiveData()
    suspend fun cancelAccountReason() {
        var clearReason = fetchRequest {
            var body = HashMap<String, Any>()
            body["dictType"] = "user_cancel_reason"
            var rkey = getRandomKey()
            apiService.cancelAccountReason(body.header(rkey), body.body(rkey))
        }
        if (clearReason.code == 0) {
            clearAccountReason.postValue(clearReason.data)
        }
    }

    suspend fun cancelAccount() {
        var clearAccount = fetchRequest {
            var body = HashMap<String, String>()
            var rkey = getRandomKey()
            apiService.cancelAccount(body.header(rkey), body.body(rkey))
        }
    }


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
            }
        }
    }

    var menuBean = MutableLiveData<ArrayList<MenuBeanItem>>()
    fun getMenuList() {
        viewModelScope.launch {
            fetchRequest {
                var body = HashMap<String, String>()
                var rkey = getRandomKey()
                apiService.queryMenuList(body.header(rkey), body.body(rkey))
            }.onSuccess {
                menuBean.postValue(it)
            }
        }

    }

    private fun saveUserInfo(userInfoBean: UserInfoBean?) {
        UserManger.updateUserInfo(userInfoBean)
    }

    /**
     * 登录成功
     */
    private fun loginSuccess(loginBean: LoginBean?) {
        loginBean?.let {
            UserManger.saveUserInfo(loginBean)
            MConstant.token = it.token
            SPUtils.putToken(it.token)
            getUserInfo()
            when {
                it.phone.isNullOrEmpty() -> {
                    LiveDataBus.get()
                        .with(USER_LOGIN_STATUS, UserManger.UserLoginStatus::class.java)
                        .postValue(UserManger.UserLoginStatus.USE_UNBIND_MOBILE)
                }
                else -> {
                    LiveDataBus.get()
                        .with(USER_LOGIN_STATUS, UserManger.UserLoginStatus::class.java)
                        .postValue(UserManger.UserLoginStatus.USER_LOGIN_SUCCESS)
                }
            }
        }
    }
    /**
     * 上传图片
     */

    fun uploadFile(cp:Context,upfiles: List<String>, callback: UploadPicCallback) {
        GetOSS(cp, upfiles, 0, callback)
    }
    /**
     * 获取上传图片得凭证
     */
    lateinit var upimgs: ArrayList<String>

    fun GetOSS(
        context: Context,
        upfiles: List<String>,
        count: Int,
        callback: UploadPicCallback
    ) {
        viewModelScope.launch {
            var body = HashMap<String, Any>()
            var rkey = getRandomKey()
            fetchRequest {
                apiService.getOSS(body.header(rkey),body.body(rkey))
            }.onSuccess {
                initAliYunOss(context, it!!)//
                upimgs = ArrayList()
                uploadImgs(
                    context,
                    upfiles,
                    it,
                    count,
                    callback
                )
            }.onFailure {
                var msg = "上传失败"
                msg.toast()
                callback.onUploadFailed(msg)
            }
        }
    }
    /**
     * 取文件后缀名 创建文件名
     */
    private fun createFileName(uploadFilePath: String, tempFilePath: String): String {
        var type = uploadFilePath
            .substring(uploadFilePath.lastIndexOf(".") + 1, uploadFilePath.length)
        return tempFilePath + System.currentTimeMillis() + "." + type

    }
    /**
     * 初始化oss上传
     */
    private fun initAliYunOss(context: Context, stsBean: STSBean) {
        AliYunOssUploadOrDownFileConfig.getInstance(context).initOss(
            stsBean.endpoint, stsBean.accessKeyId,
            stsBean.accessKeySecret, stsBean.securityToken
        )
    }
    private fun uploadImgs(
        context: Context,
        upfiles: List<String>,
        stsBean: STSBean,
        count: Int,
        callback: UploadPicCallback
    ) {
        val size = upfiles.size
        AliYunOssUploadOrDownFileConfig.getInstance(context).initOss(
            stsBean.endpoint, stsBean.accessKeyId,
            stsBean.accessKeySecret, stsBean.securityToken
        )
        var path = createFileName(upfiles[count], stsBean.tempFilePath)
        upimgs.add(path)
        AliYunOssUploadOrDownFileConfig.getInstance(context)
            .uploadFile(stsBean.bucketName, path, upfiles[count], "", 0)
        AliYunOssUploadOrDownFileConfig.getInstance(context).setOnUploadFile(object :
            AliYunOssUploadOrDownFileConfig.OnUploadFile {
            override fun onUploadFileSuccess(info: String) {
                val scount = count + 1
                if (scount == size) {
                    callback.onUploadSuccess(upimgs)

                }
                uploadImgs(context, upfiles, stsBean, scount, callback)
            }

            override fun onUploadFileFailed(errCode: String) {
                callback.onUploadFailed(errCode)
            }

            override fun onuploadFileprogress(
                request: PutObjectRequest,
                currentSize: Long,
                totalSize: Long
            ) {
                //currentSize*100/totalSize
            }
        })
    }

    /**
     * 退出登录
     */
    fun loginOut() {
        UserManger.deleteUserInfo()
        MConstant.token = ""
        LiveDataBus.get().with(USER_LOGIN_STATUS, UserManger.UserLoginStatus::class.java)
            .postValue(UserManger.UserLoginStatus.USER_LOGIN_OUT)
    }
}