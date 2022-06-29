package com.changanford.my.request

import android.content.Context
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.alibaba.sdk.android.oss.model.PutObjectRequest
import com.changanford.common.basic.BaseViewModel
import com.changanford.common.bean.STSBean
import com.changanford.common.bean.UserInfoBean
import com.changanford.common.manger.UserManger
import com.changanford.common.net.*
import com.changanford.common.util.AliYunOssUploadOrDownFileConfig
import com.changanford.common.util.MConstant
import com.changanford.common.util.bus.LiveDataBus
import com.changanford.common.utilext.toast
import com.changanford.common.utilext.toastShow
import com.changanford.my.interf.UploadPicCallback
import kotlinx.coroutines.launch

class PersonCenterViewModel: BaseViewModel() {

    /**
     * 获取用户信息
     */
    fun queryOtherInfo(userId: String, result: (CommonResponse<UserInfoBean>) -> Unit) {
        viewModelScope.launch {
            result(fetchRequest {
                val body = HashMap<String, Any>()
                body["userId"] = userId
                val rkey = getRandomKey()
                apiService.queryOtherInfo(body.header(rkey), body.body(rkey))
            })
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
     * 取文件后缀名 创建文件名
     */
    private fun createFileName(uploadFilePath: String, tempFilePath: String): String {
        val type = uploadFilePath
            .substring(uploadFilePath.lastIndexOf(".") + 1, uploadFilePath.length)
        return tempFilePath + System.currentTimeMillis() + "." + type
    }

    fun saveUniUserInfoV1(body: HashMap<String, String>, result: (CommonResponse<String>) -> Unit) {
        viewModelScope.launch {
            result(fetchRequest(showLoading = true) {
                var rkey = getRandomKey()
                apiService.saveUniUserInfo(body.header(rkey), body.body(rkey))
            })
        }
    }
    var cancelTip: MutableLiveData<String> = MutableLiveData()

    fun cancelFans(
        followId: String,
        type: String
    ) {
        viewModelScope.launch {
            val cancle = fetchRequest(showLoading = true) {
                val body = HashMap<String, String>()
                body["followId"] = followId
                body["type"] = type
                val rkey = getRandomKey()
                apiService.cancelFans(body.header(rkey), body.body(rkey))
            }
            if (cancle.code == 0) {
                cancelTip.postValue("true")
            } else {
                cancelTip.postValue(cancle.msg)
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
}