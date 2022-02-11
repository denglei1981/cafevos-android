package com.changanford.circle.ui.ask.request

import android.content.Context
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.alibaba.sdk.android.oss.model.PutObjectRequest
import com.changanford.circle.api.CircleNetWork
import com.changanford.circle.bean.AskListMainData
import com.changanford.circle.bean.ChildCommentListBean
import com.changanford.circle.bean.HomeDataListBean
import com.changanford.circle.bean.MechanicData
import com.changanford.circle.interf.UploadPicCallback
import com.changanford.common.MyApp
import com.changanford.common.basic.BaseViewModel
import com.changanford.common.bean.*
import com.changanford.common.net.*
import com.changanford.common.net.response.UpdateUiState
import com.changanford.common.util.AliYunOssUploadOrDownFileConfig
import com.changanford.common.util.DeviceUtils
import com.changanford.common.utilext.createHashMap
import com.changanford.common.utilext.toast
import kotlinx.coroutines.launch

/**
 *Author lcw
 *Time on 2021/9/18
 *Purpose
 */
class MechanicMainViewModel : BaseViewModel() {


    var technicianLiveData: MutableLiveData<TechnicianData> = MutableLiveData()


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
        var type = uploadFilePath
            .substring(uploadFilePath.lastIndexOf(".") + 1, uploadFilePath.length)
        return tempFilePath + System.currentTimeMillis() + "." + type
    }


    fun getTechniciaPersonalInfo(technicianId: String) {
        launch(block = {
            val body = MyApp.mContext.createHashMap()
            body["technicianId"] = technicianId
            val rKey = getRandomKey()
            ApiClient.createApi<CircleNetWork>()
                .techniciaPersonalInfo(body.header(rKey), body.body(rKey)).also {
                    technicianLiveData.postValue(it.data)
                }.onWithMsgFailure {
                    it?.toast()
                }
        })
    }

    var questTypeList: MutableLiveData<ArrayList<QuestionData>> = MutableLiveData()
    fun getQuestionType() {
        launch(block = {
            val body = MyApp.mContext.createHashMap()
            body["dictType"] = "qa_question_type"
            val rKey = getRandomKey()
            ApiClient.createApi<CircleNetWork>()
                .getQuestionType(body.header(rKey), body.body(rKey)).also {
                    questTypeList.postValue(it.data)
                }
        })
    }

    // 更换技师个人资料
    fun upTechniciaInfo( isHeader:Boolean,map: HashMap<String, String>) {

            launch(true, block = {
                val rKey = getRandomKey()
                ApiClient.createApi<CircleNetWork>()
                    .updateTechniciaPersonalInfo(map.header(rKey), map.body(rKey)).also {
//                        questTypeList.postValue(it.data)
                    }
            })

    }


}