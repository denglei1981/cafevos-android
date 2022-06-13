package com.changanford.my.viewmodel

import android.content.Context
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.alibaba.sdk.android.oss.model.PutObjectRequest
import com.changanford.common.MyApp
import com.changanford.common.basic.BaseApplication
import com.changanford.common.bean.*
import com.changanford.common.manger.UserManger
import com.changanford.common.net.*
import com.changanford.common.util.*
import com.changanford.common.util.bus.LiveDataBus
import com.changanford.common.util.bus.LiveDataBusKey.USER_LOGIN_STATUS
import com.changanford.common.util.room.UserDatabase
import com.changanford.common.utilext.GlideUtils
import com.changanford.common.utilext.toast
import com.changanford.common.utilext.toastShow
import com.changanford.my.interf.UploadPicCallback
import com.changanford.my.utils.downLoginBg
import com.changanford.my.utils.getDiskCacheDir
import com.changanford.my.utils.getDiskCachePath
import com.luck.picture.lib.entity.LocalMedia
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import me.leolin.shortcutbadger.ShortcutBadger
import java.util.*

/**
 *  文件名：SignViewModel
 *  创建者: zcy
 *  创建日期：2021/9/9 16:50
 *  描述: TODO
 *  修改描述：TODO
 */
class SignViewModel : ViewModel() {

    var smsSuccess: MutableLiveData<Boolean> = MutableLiveData()
    var _hobbyBean: MutableLiveData<ArrayList<HobbyBeanItem>> = MutableLiveData()
    var _feedBackBean: MutableLiveData<FeedbackQBean> = MutableLiveData()
    var _lables: MutableLiveData<ArrayList<FeedbackTagsItem>> = MutableLiveData()
    var _feedbackMineListBean: MutableLiveData<FeedbackMineListBean> = MutableLiveData()

    /**
     * 反馈意见内容列表
     */
    var feedbackInfo: MutableLiveData<FeedbackInfoList> = MutableLiveData()

    /**
     * 新增意见反馈项
     */
    var feedbackSetting: MutableLiveData<FeedbackSettingBean> = MutableLiveData()

    /**
     * 标记消息已读
     */
    var changeAllToRead = MutableLiveData<Boolean>()

    val userDatabase: UserDatabase by lazy {
        UserDatabase.getUniUserDatabase(MyApp.mContext)
    }

    fun queryMessageStatus(result: (CommonResponse<MessageStatusBean>) -> Unit) {
        viewModelScope.launch {
            result(fetchRequest {
                var body = HashMap<String, String>()
                var rkey = getRandomKey()
                apiService.queryMessageStatus(body.header(rkey), body.body(rkey))
            })
        }
    }

    fun changeAllToRead(messageType: Int) {
        viewModelScope.launch {
            fetchRequest {
                var body = HashMap<String, Int>()
                body["messageType"] = messageType
                var rkey = getRandomKey()
                apiService.changeAllToRead(body.header(rkey), body.body(rkey))
            }.onSuccess {
                changeAllToRead.postValue(true)
            }.onFailure {
                changeAllToRead.postValue(false)
            }
        }
    }

    fun queryMessageList(
        pageNo: Int,
        messageType: Int, result: (CommonResponse<MessageListBean>) -> Unit
    ) {
        viewModelScope.launch {
            result(fetchRequest {
                var body = HashMap<String, Any>()
                body["queryParams"] = MessageQueryParams(messageType)
                body["pageNo"] = pageNo
                body["pageSize"] = 20
                var rkey = getRandomKey()
                apiService.queryMessageList(body.header(rkey), body.body(rkey))
            })
        }
    }

    /**
     * 删除消息
     */
    fun delUserMessage(
        userMessageIds: String, result: (CommonResponse<String>) -> Unit
    ) {
        viewModelScope.launch {
            result(fetchRequest {
                var body = HashMap<String, Any>()
                body["userMessageIds"] = userMessageIds
                var rkey = getRandomKey()
                apiService.delUserMessage(body.header(rkey), body.body(rkey))
            })
        }
    }

    fun changAllMessage(userMessageId: String) {
        viewModelScope.launch {
            fetchRequest {
                var body = HashMap<String, Any>()
                body["userMessageIds"] = userMessageId
                var rkey = getRandomKey()
                apiService.changAllMessage(body.header(rkey), body.body(rkey))
            }.onSuccess {

            }
        }
    }

    fun getHobbyList() {
        viewModelScope.launch {
            fetchRequest {
                var body = HashMap<String, String>()
                var rkey = getRandomKey()
                apiService.getHobbyList(body.header(rkey), body.body(rkey))
            }.onSuccess {
                _hobbyBean.postValue(it)
            }
        }
    }

    fun queryIndustryList(function: (ArrayList<IndustryBeanItem>) -> Unit) {
        viewModelScope.launch {
            fetchRequest {
                var body = HashMap<String, String>()
                var rkey = getRandomKey()
                apiService.queryIndustryList(body.header(rkey), body.body(rkey))
            }.onSuccess {
                function(it!!)
            }
        }
    }

    fun getFeedbackQ() {
        viewModelScope.launch {
            fetchRequest {
                var body = HashMap<String, Any>()
                body["pageNo"] = "1"
                body["pageSize"] = "20"
                var rKey = getRandomKey()
                apiService.getFeedbackQ(body.header(rKey), body.body(rKey))
            }.onSuccess {
                _feedBackBean.postValue(it)
            }
        }
    }

    fun uploadFileWithWH(upfiles: List<LocalMedia>, callback: UploadPicCallback) {
        GetOSSWithWH(BaseApplication.curActivity, upfiles, 0, callback)
    }

    /**
     * 地址带图片宽高
     */
    fun GetOSSWithWH(
        context: Context,
        upfiles: List<LocalMedia>,
        count: Int,
        callback: UploadPicCallback
    ) {
        viewModelScope.launch {
            var body = HashMap<String, Any>()
            var rkey = getRandomKey()
            fetchRequest {
                apiService.getOSS(body.header(rkey), body.body(rkey))
            }.onSuccess {
                initAliYunOss(context, it!!)//
                upimgs = ArrayList()
                uploadImgs(
                    context,
                    upfiles,
                    it!!,
                    count,
                    callback
                )
            }.onWithMsgFailure {
                it?.toast()
                callback.onUploadFailed(it ?: "")
            }
        }

    }

    /**
     * 上传带宽高的图片
     */
    @JvmName("uploadImgs1")
    private fun uploadImgs(
        context: Context,
        upfiles: List<LocalMedia>,
        stsBean: STSBean,
        count: Int,
        callback: UploadPicCallback
    ) {
        val size = upfiles.size
        AliYunOssUploadOrDownFileConfig.getInstance(context).initOss(
            stsBean.endpoint, stsBean.accessKeyId,
            stsBean.accessKeySecret, stsBean.securityToken
        )
        //重新设置图片地址
        var path = MineUtils.imgWandH(stsBean, upfiles[count])
        //保存地址
        upimgs.add(path)
        //上传地址初始化
        AliYunOssUploadOrDownFileConfig.getInstance(context)
            .uploadFile(stsBean.bucketName, path, PictureUtil.getFinallyPath(upfiles[count]), "", 0)
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

    fun getFeedbackTags() {
        viewModelScope.launch {
            fetchRequest {
                var body = HashMap<String, Any>()
                var rKey = getRandomKey()
                apiService.getFeedbackTag(body.header(rKey), body.body(rKey))
            }.onSuccess {
                _lables.postValue(it)
            }
        }
    }

    fun addFeedback(body: HashMap<String, Any>, result: (CommonResponse<String>) -> Unit) {
        viewModelScope.launch {
            result(fetchRequest {
                var rKey = getRandomKey()
                apiService.addFeedback(body.header(rKey), body.body(rKey))
            })
        }
    }

    /**
     * 获取意见常用问题
     */
    fun getMineFeedback(pageNo: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            fetchRequest {
                var body = HashMap<String, Any>()
                body["pageNo"] = pageNo
                body["pageSize"] = 20
                var rkey = getRandomKey()
                apiService.getMineFeedback(body.header(rkey), body.body(rkey))
            }.onSuccess {
                _feedbackMineListBean.postValue(it)
            }
        }
    }

    fun deleteUserFeedback(userFeedbackId: Int) {
        viewModelScope.launch {
            fetchRequest {
                val body = HashMap<String, Any>()
                body["userFeedbackId"] = userFeedbackId
                val rKey = getRandomKey()
                apiService.deleteUserFeedback(body.header(rKey), body.body(rKey))
            }
        }
    }

    fun changeToRead(userFeedbackId: Int) {
        viewModelScope.launch {
            fetchRequest {
                val body = HashMap<String, Any>()
                body["userFeedbackId"] = userFeedbackId
                val rKey = getRandomKey()
                apiService.changeToRead(body.header(rKey), body.body(rKey))
            }
        }
    }

    /**
     * 查询管理员昵称
     */
    fun queryMemberNickName(result: (CommonResponse<FeedbackMemberBean>) -> Unit) {

        viewModelScope.launch {
            result(fetchRequest {
                var body = HashMap<String, Any>()
                body["configKey"] = "feedback_reply_set"
                body["obj"] = true
                var rkey = getRandomKey()
                apiService.queryMemberNickName(body.header(rkey), body.body(rkey))
            })
        }
    }

    /**
     * 获取用户反馈内容
     */
    fun queryFeedbackInfoList(pageNo: Int, userFeedbackId: String) {
        viewModelScope.launch {
            fetchRequest {
                var body = HashMap<String, Any>()
                body["userFeedbackId"] = userFeedbackId
                body["pageNo"] = pageNo
                body["pageSize"] = 20
                var rkey = getRandomKey()
                apiService.queryFeedbackInfoList(body.header(rkey), body.body(rkey))
            }.onSuccess {
                feedbackInfo.postValue(it)
            }
        }

    }

    fun closeFeedback(userFeedbackId: String, result: (CommonResponse<String>) -> Unit) {
        viewModelScope.launch {
            result(fetchRequest {
                var body = HashMap<String, Any>()
                body["userFeedbackId"] = userFeedbackId
                var rkey = getRandomKey()
                apiService.closeFeedback(body.header(rkey), body.body(rkey))
            })
        }

    }

    /**
     * 新增一条反馈
     */
    fun addFeedbackInfo(param: HashMap<String, Any>, result: (CommonResponse<String>) -> Unit) {
        viewModelScope.launch {
            result(fetchRequest {
                var rkey = getRandomKey()
                apiService.addFeedbackInfo(param.header(rkey), param.body(rkey))
            })
        }

    }

    /**
     * 查询联系电话
     */
    fun querySettingPhone(result: (CommonResponse<SettingPhoneBean>) -> Unit) {

        viewModelScope.launch {
            result(fetchRequest {
                var body = HashMap<String, Any>()
                body["configKey"] = "service.hotline"
                body["obj"] = true
                var rkey = getRandomKey()
                apiService.querySettingPhone(body.header(rkey), body.body(rkey))
            })
        }
    }

    fun monthSignDetail(date: String, result: (CommonResponse<MonthSignBean>) -> Unit) {
        viewModelScope.launch {
            result(fetchRequest {
                var body = HashMap<String, Any>()
                body["date"] = date
                var rkey = getRandomKey()
                apiService.monthSignDetail(body.header(rkey), body.body(rkey))
            })
        }
    }

    fun weekSignDetail(result: (CommonResponse<MonthSignBean>) -> Unit) {
        viewModelScope.launch {
            result(fetchRequest {
                var body = HashMap<String, Any>()
                var rkey = getRandomKey()
                apiService.weekSignDetail(body.header(rkey), body.body(rkey))
            })
        }
    }

    /****------------------****/
    fun getSmsCode(mobile: String) {
        if (mobile?.isNullOrEmpty()) {
            toastShow("请输入手机号")
            return
        }
        viewModelScope.launch {
            fetchRequest(showLoading = true) {
                var body = HashMap<String, Any>()
                body["phone"] = mobile
                var rKey = getRandomKey()
                apiService.sendFordSmsCode(body.header(rKey), body.body(rKey))
            }.onSuccess {
                smsSuccess.postValue(true)
            }.onWithMsgFailure {
                it?.let {
                    toastShow(it)
                }
            }
        }
    }

    fun smsCacSmsCode(mobile: String) {
        if (mobile?.isNullOrEmpty()) {
            toastShow("请输入手机号")
            return
        }
        viewModelScope.launch {
            fetchRequest(showLoading = true) {
                var body = HashMap<String, String>()
                body["phone"] = mobile
                var rkey = getRandomKey()
                apiService.sendCacSmsCode(body.header(rkey), body.body(rkey))
            }.onSuccess {
                smsSuccess.postValue(true)
            }.onWithMsgFailure {
                it?.let {
                    toastShow(it)
                }
            }
        }
    }

    fun saveUniUserInfo(body: HashMap<String, String>) {
        viewModelScope.launch {
            fetchRequest {
                var rkey = getRandomKey()
                apiService.saveUniUserInfo(body.header(rkey), body.body(rkey))
            }.onSuccess {
                "保存成功".toast()
                LiveDataBus.get().with(MConstant.REFRESH_USER_INFO, Boolean::class.java)
                    .postValue(true)
            }.onWithMsgFailure {
                it?.let {
                    toastShow(it)
                }
            }
        }
    }

    fun saveUniUserInfoV1(body: HashMap<String, String>, result: (CommonResponse<String>) -> Unit) {
        viewModelScope.launch {
            result(fetchRequest(showLoading = true) {
                var rkey = getRandomKey()
                apiService.saveUniUserInfo(body.header(rkey), body.body(rkey))
            })
        }
    }

    fun smsLogin(mobile: String, sms: String, pushId: String) {
        viewModelScope.launch {
            fetchRequest(showLoading = true) {
                var body = HashMap<String, String>()
                body["phone"] = mobile
                body["smsCode"] = sms
                body["pushId"] = pushId
                var rkey = getRandomKey()
                apiService.smsCodeSign(body.header(rkey), body.body(rkey))
            }.onSuccess {
                loginSuccess(it)
            }.onWithMsgFailure {
                it?.toast()
            }
        }
    }

    fun nameNick(nameNick: String, callback: (CommonResponse<String>) -> Unit) {
        viewModelScope.launch {
            callback(fetchRequest {
                var body = HashMap<String, String>()
                body["nickname"] = nameNick
                var rkey = getRandomKey()
                apiService.nameNick(body.header(rkey), body.body(rkey))
            })
        }
    }


    fun otherLogin(type: String, code: String, pushId: String) {
        viewModelScope.launch {
            fetchRequest(showLoading = true) {
                var body = HashMap<String, String>()
                body["type"] = type
                body["code"] = code
                body["pushId"] = pushId
                var rkey = getRandomKey()
                apiService.otherOauthSign(body.header(rkey), body.body(rkey))
            }.onSuccess {
                it?.let {
                    loginSuccess(it)
                }
            }.onWithMsgFailure {
                it?.let {
                    it.toast()
                }
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
                    it?.let { saveUserInfo(it) }
                }.onFailure {
                    saveUserInfo(null)
                }
            }
        } else {
            saveUserInfo(null)
        }
    }

    fun getEditUserInfo(result: (CommonResponse<UserInfoBean>) -> Unit) {
        if (UserManger.isLogin()) {
            viewModelScope.launch {
                result(fetchRequest(showLoading = true) {
                    var body = HashMap<String, String>()
                    var rkey = getRandomKey()
                    apiService.queryUserInfo(body.header(rkey), body.body(rkey))
                })
            }
        } else {
            saveUserInfo(null)
        }
    }


    var taskBean: MutableLiveData<List<RootTaskBean>> = MutableLiveData()

    fun queryTasksList() {
        viewModelScope.launch {
            fetchRequest {
                var body = HashMap<String, Any>()
                var rkey = getRandomKey()
                apiService.queryTasksList(body.header(rkey), body.body(rkey))
            }.onSuccess {
                taskBean.postValue(it)
            }.onFailure {
                taskBean.postValue(null)
            }
        }
    }

    var jifenBean: MutableLiveData<GrowUpBean> = MutableLiveData()

    //成长值 2 积分 1
    fun mineGrowUp(pageNo: Int, type: String) {
        viewModelScope.launch {
            fetchRequest {
                var body = HashMap<String, Any>()
                body["pageNo"] = pageNo
                body["pageSize"] = "20"
                body["queryParams"] = mapOf("type" to type)
                var rkey = getRandomKey()
                when (type) {
                    "1" -> {
                        apiService.mineGrowUp(body.header(rkey), body.body(rkey))
                    }
                    else -> {
                        apiService.mineGrowUpLog(body.header(rkey), body.body(rkey))
                    }
                }
            }.onSuccess {
                jifenBean.postValue(it)
            }.onFailure {
                jifenBean.postValue(null)
            }
        }
    }

    //等级权益
    fun mineGrowUpQy(result: (CommonResponse<ArrayList<GrowUpQYBean>>) -> Unit) {
        viewModelScope.launch {
            result(fetchRequest {
                var body = HashMap<String, Any>()
                body["interestsType"] = 2
                var rkey = getRandomKey()
                apiService.queryUserQy(body.header(rkey), body.body(rkey))
            })
        }
    }

    /*------------勋章------------*/

    val allMedal: MutableLiveData<ArrayList<MedalListBeanItem>> = MutableLiveData()
    val medalTotalNum: MutableLiveData<Int> = MutableLiveData()

    var medalMap: MutableMap<Int, ArrayList<MedalListBeanItem>> = TreeMap { o1, o2 ->
        o1.compareTo(o2)
    }
    val titles: ArrayList<MedalListBeanItem> = ArrayList()
    private var medalWithWearTotalNum: Int = 0

    fun mineMedal(medalType: Int = 0) {
        viewModelScope.launch {
            fetchRequest {
                var body = HashMap<String, String>()
                var rkey = getRandomKey()
                apiService.queryMedalList(body.header(rkey), body.body(rkey))
            }.onSuccess {
                medal(it, medalType)
            }.onFailure {
                allMedal.postValue(null)
            }
        }
    }

    private fun medal(allMedals: ArrayList<MedalListBeanItem>?, medalType: Int = 0) {
        allMedals?.let { medals ->
            medalWithWearTotalNum = 0
            medalMap.clear()
            titles.clear()
            medals?.forEach { item ->
                var list: ArrayList<MedalListBeanItem>? = medalMap[item.medalType]
                if (null == list) {
                    list = ArrayList()
                    list.add(item)
                    medalMap[item.medalType] = list
                    titles.add(item)
                } else {
                    list.add(item)
                }
                if (item.isGet == "1") {//已领取
                    medalWithWearTotalNum++
                }
            }
            medalTotalNum.postValue(medalWithWearTotalNum)

            when (medalType) {
                0 -> {
                    allMedal.postValue(medals)
                }
                else -> {
                    allMedal.postValue(medalMap[medalType])
                }
            }
        }
    }


    /**
     *操作类型 1佩戴 2领取
     */
    val wearMedal: MutableLiveData<String> = MutableLiveData()

    fun wearMedal(medalId: String, type: String) {
        viewModelScope.launch {
            fetchRequest(showLoading = true) {
                var body = HashMap<String, String>()
                body["medalId"] = medalId
                body["type"] = type
                var rkey = getRandomKey()
                apiService.wearMedal(body.header(rkey), body.body(rkey))
            }.onSuccess {
                wearMedal.postValue("true")
            }.onWithMsgFailure {
                wearMedal.postValue(it)
            }
        }
    }


    fun oneselfMedal(result: (CommonResponse<ArrayList<MedalListBeanItem>>) -> Unit) {
        viewModelScope.launch {
            result(fetchRequest {
                var body = HashMap<String, String>()
                var rkey = getRandomKey()
                apiService.queryUserMedalList(body.header(rkey), body.body(rkey))
            })
        }
    }


    fun bindMobile(phone: String, smsCode: String) {
        viewModelScope.launch {
            fetchRequest {
                var body = HashMap<String, String>()
                body["phone"] = phone
                body["smsCode"] = smsCode
                var rkey = getRandomKey()
                apiService.bindMobile(body.header(rkey), body.body(rkey))
            }.onSuccess {
                //发现绑定后，userId,改变了
                UserManger.deleteUserInfo()
                it?.jumpData = BindMobileJumpData()
                loginSuccess(it)
            }.onWithMsgFailure {
                it?.let {
                    toastShow(it)
                }
            }
        }
    }

    fun queryFansList(
        pageNo: Int,
        type: Int,
        userId: String,
        result: (CommonResponse<FansListBean>) -> Unit
    ) {
        viewModelScope.launch {
            result(fetchRequest {
                var map = mapOf("type" to type, "userId" to userId)
                var body = HashMap<String, Any>()
                body["pageNo"] = pageNo
                body["pageSize"] = 20
                body["queryParams"] = map
                var rkey = getRandomKey()
                apiService.queryFansList(body.header(rkey), body.body(rkey))
            })
        }
    }

    var cancelTip: MutableLiveData<String> = MutableLiveData()

    fun cancelFans(
        followId: String,
        type: String
    ) {
        viewModelScope.launch {
            var cancle = fetchRequest(showLoading = true) {
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
    }

    var bindAccount: MutableLiveData<ArrayList<BindAuthBeanItem>> = MutableLiveData()
    fun bindAccount() {
        viewModelScope.launch {
            var account = fetchRequest {
                var body = HashMap<String, String>()
                var rKey = getRandomKey()
                apiService.queryBindMobileList(body.header(rKey), body.body(rKey))
            }
            if (account.code == 0) {
                bindAccount.postValue(account.data)
            }
        }
    }


    /**
     * 三方登录
     * 授权类型 min pub weixin qq weibo douyin apple
     * 授权code， qq格式token,openid apple格式 code,userId,fullName
     */
    var bindOtherAccount: MutableLiveData<String> = MutableLiveData()

    fun bindOtherAuth(type: String, code: String) {
        viewModelScope.launch {
            var otherAuth = fetchRequest(showLoading = true) {
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

    fun verifyCancelAccount() {
        viewModelScope.launch {
            var clearAccount = fetchRequest {
                var body = HashMap<String, String>()
                var rkey = getRandomKey()
                apiService.verifyCancelAccount(body.header(rkey), body.body(rkey))
            }
            if (clearAccount.code == 0) {
                clearBean.postValue(clearAccount.data)
            }
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

    fun cancelAccount(
        phone: String,
        smsCode: String,
        delReason: String, result: (CommonResponse<String>) -> Unit
    ) {
        viewModelScope.launch {
            result(fetchRequest(showLoading = true) {
                var body = HashMap<String, String>()
                body["phone"] = phone
                body["smsCode"] = smsCode
                body["delReason"] = delReason
                var rkey = getRandomKey()
                apiService.cancelAccount(body.header(rkey), body.body(rkey))
            })
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
                val body = HashMap<String, String>()
                val rkey = getRandomKey()
                apiService.queryMenuList(body.header(rkey), body.body(rkey))
            }.onSuccess {
                menuBean.postValue(it)
            }
        }

    }

    var userInfo: MutableLiveData<UserInfoBean> = MutableLiveData()

    private fun saveUserInfo(userInfoBean: UserInfoBean?) {
        userInfo.postValue(userInfoBean)

        UserManger.updateUserInfo(userInfoBean)
    }

    /**
     * 获取其他用户信息
     */
    fun queryOtherInfo(userId: String, result: (CommonResponse<UserInfoBean>) -> Unit) {
        viewModelScope.launch {
            result(fetchRequest {
                var body = HashMap<String, Any>()
                body["userId"] = userId
                var rkey = getRandomKey()
                apiService.queryOtherInfo(body.header(rkey), body.body(rkey))
            })
        }
    }


    fun queryOtherUserMedal(
        userId: String,
        result: (CommonResponse<ArrayList<MedalListBeanItem>>) -> Unit
    ) {
        viewModelScope.launch {
            result(fetchRequest {
                var body = HashMap<String, Any>()
                body["userId"] = userId
                var rkey = getRandomKey()
                apiService.queryOtherUserMedal(body.header(rkey), body.body(rkey))
            })
        }
    }

    /**
     * 登录成功
     */
    private fun loginSuccess(loginBean: LoginBean?) {
        loginBean?.let {
            UserManger.saveUserInfo(loginBean)
            it.userId?.let {
                MConstant.userId = it
                AppUtils.binduserid(it)
            }
            MConstant.token = it.token
            MConstant.mine_phone = "${it.phone}"
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

    fun uploadFile(cp: Context, upfiles: List<String>, callback: UploadPicCallback) {
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
                apiService.getOSS(body.header(rkey), body.body(rkey))
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
     * 获取会员所有身份
     */
    fun getUserIdCardList(result: (CommonResponse<ArrayList<UserIdCardBeanItem>>) -> Unit) {
        viewModelScope.launch() {
            result(fetchRequest {
                var body = HashMap<String, String>()
                var rkey = getRandomKey()
                apiService.getUserIdCardList(body.header(rkey), body.body(rkey))
            })
        }
    }

    /**
     * 获取会员身份
     */
    fun queryLoginUserIdCardList(result: (CommonResponse<ArrayList<UserIdCardBeanItem>>) -> Unit) {
        viewModelScope.launch() {
            result(fetchRequest {
                var body = HashMap<String, String>()
                var rkey = getRandomKey()
                apiService.queryLoginUserIdCardList(body.header(rkey), body.body(rkey))
            })
        }
    }

    /**
     * 显示用户身份
     */
    fun showUserIdCard(memberId: String, result: (CommonResponse<String>) -> Unit) {
        viewModelScope.launch() {
            result(fetchRequest {
                var body = HashMap<String, String>()
                body["memberId"] = memberId
                var rkey = getRandomKey()
                apiService.showUserIdCard(body.header(rkey), body.body(rkey))
            })
        }
    }


    /**
     * 获取用户会员
     * memberType 申请类型 1车迷 2领袖 3大咖
     */
    fun getUserIdCard(
        memberId: Int,
        memberType: String,
        result: (CommonResponse<AuthBean>) -> Unit
    ) {
        viewModelScope.launch {
            result(fetchRequest {
                var body = HashMap<String, Any>()
                body["memberId"] = memberId.toString()
                body["memberKey"] = memberType
                var rkey = getRandomKey()
                apiService.getUserIdCard(body.header(rkey), body.body(rkey))
            })
        }
    }


    /**
     * 提交会员身份
     */
    fun submitUserIdCard(
        body: HashMap<String, Any>,
        result: (CommonResponse<String>) -> Unit
    ) {
        viewModelScope.launch {
            result(fetchRequest {
                var rkey = getRandomKey()
                apiService.submitUserIdCard(body.header(rkey), body.body(rkey))
            })
        }
    }

    /**
     * 获取绑定手机jumpDataType true跳转 false 不跳转
     */
    fun getBindMobileJumpDataType(): Boolean {
        return MineUtils.getBindMobileJumpDataType(true)
    }

    var loginBgPath: MutableLiveData<String> = MutableLiveData()

    fun downLoginBgUrl() {
        try {
            if (MConstant.isDownLoginBgSuccess) {
                loginBgPath.postValue("${
                    getDiskCachePath(BaseApplication.INSTANT)?.let {
                        getDiskCacheDir(it,MConstant.loginBgVideoPath)
                    }
                }")
            } else {
                MConstant.loginBgVideoUrl?.apply {
                    loginBgPath.postValue(GlideUtils.handleImgUrl(this))
                    return
                }
                viewModelScope.launch {
                    fetchRequest {
                        val body = HashMap<String, Any>()
                        body["configKey"] = "login_background"
                        body["obj"] = true
                        val rkey = getRandomKey()
                        apiService.loginBg(body.header(rkey), body.body(rkey))
                    }.onSuccess {
                        it?.video?.let {
                            loginBgPath.postValue(GlideUtils.handleImgUrl(it))
                            downLoginBg(it)
                        }
                    }.onFailure {
//                play("ford-manager/2021/10/29/1c748b05a0c34fee8a172ae75f3df393.mp4")
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun queryAuthCarAndIncallList(result: (CommonResponse<CarAuthBean>) -> Unit) {
        if (UserManger.isLogin()) {
            viewModelScope.launch {
                result(fetchRequest {
                    var body = HashMap<String, Any>()
                    var rkey = getRandomKey()
                    apiService.queryAuthCarList(body.header(rkey), body.body(rkey))
                })
            }
        }
    }

    /**
     * 退出登录
     */
    fun loginOut() {
        viewModelScope.launch {
            fetchRequest(showLoading = true) {
                var body = HashMap<String, Any>()
                var rkey = getRandomKey()
                apiService.loginOut(body.header(rkey), body.body(rkey))
            }.onSuccess {
                clearLoginUserInfo()
            }.onFailure {
                clearLoginUserInfo()
            }
        }
    }

    /**
     * 清楚登录信息
     */
    private fun clearLoginUserInfo() {
        UserManger.deleteUserInfo()
        AppUtils.Unbinduserid()
        ShortcutBadger.applyCount(MyApp.mContext, 0)
        SPUtils.clearByKey(MConstant.FORD_CHANNEL)
        MConstant.token = ""
        MConstant.mine_phone = ""
        MConstant.userId = ""
        LiveDataBus.get().with(USER_LOGIN_STATUS, UserManger.UserLoginStatus::class.java)
            .postValue(UserManger.UserLoginStatus.USER_LOGIN_OUT)
    }

    fun getCov(result: (CommonResponse<MutableList<MyFastInData>>) -> Unit) {
        viewModelScope.launch {
            result(fetchRequest {
                val body = HashMap<String, Any>()
                body["configKey"] = "my_cov"
                body["obj"] = true
                val rkey = getRandomKey()
                apiService.myCov(body.header(rkey), body.body(rkey))
            })
        }
    }
    fun receiveList(result: (CommonResponse<MutableList<CouponsItemBean>>) -> Unit) {
        viewModelScope.launch {
            result(fetchRequest {
                val body = HashMap<String, Any>()
                val randomKey = getRandomKey()
                body["popup"]="YES"
                val rkey = getRandomKey()
                apiService.receiveList(body.header(rkey), body.body(rkey))
            })
        }

    }
}