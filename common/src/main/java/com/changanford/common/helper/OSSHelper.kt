package com.changanford.common.helper

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import com.alibaba.sdk.android.oss.model.PutObjectRequest
import com.changanford.common.basic.BaseApplication
import com.changanford.common.bean.OcrBean
import com.changanford.common.bean.OcrRequestBean
import com.changanford.common.bean.STSBean
import com.changanford.common.net.*
import com.changanford.common.ui.dialog.LoadDialog
import com.changanford.common.util.AliYunOssUploadOrDownFileConfig
import com.changanford.common.util.MConstant
import com.changanford.common.utilext.toast
import kotlinx.coroutines.launch

object OSSHelper {

    @SuppressLint("StaticFieldLeak")
    private lateinit var dialog: LoadDialog

    fun init(activity: Activity): OSSHelper {
        dialog = LoadDialog(activity)
        return this
    }

    //图片上传
    fun getOSSToImage(context: Context, base64Str: String, listener: OSSImageListener){
        BaseApplication.currentViewModelScope.launch {
            val body = HashMap<String, Any>()
            val rKey = getRandomKey()
            fetchRequest {
                apiService.getOSS(body.header(rKey), body.body(rKey))
            }.onSuccess {
                val stsBean = it
                dialog.setCancelable(false)
                dialog.setCanceledOnTouchOutside(false)
                dialog.setLoadingText("图片上传中..")
                dialog.show()
                stsBean?.let { it1 -> uploadImg(context, base64Str, it1, dialog, listener) }
            }.onFailure {
                val msg = "上传失败"
                msg.toast()
            }
        }
    }

    //身份证识别
    fun getOss(context: Context, base64Str: String, listener: OSSListener) {
        BaseApplication.currentViewModelScope.launch {
            val body = HashMap<String, Any>()
            val rKey = getRandomKey()
            fetchRequest {
                apiService.getOSS(body.header(rKey), body.body(rKey))
            }.onSuccess {
                val stsBean = it
                dialog.setCancelable(false)
                dialog.setCanceledOnTouchOutside(false)
                dialog.setLoadingText("图片上传中..")
                dialog.show()
                stsBean?.let { it1 -> uploadImgs(context, base64Str, it1, dialog, listener) }
            }.onFailure {
                val msg = "上传失败"
                msg.toast()
            }
        }
    }

    //身份证识别
    private fun uploadImgs(
        context: Context,
        upFile: String,
        stsBean: STSBean,
        dialog: LoadDialog,
        listener: OSSListener
    ) {
        AliYunOssUploadOrDownFileConfig.getInstance(context).initOss(
            stsBean.endpoint, stsBean.accessKeyId,
            stsBean.accessKeySecret, stsBean.securityToken
        )
        val path = createFileName(upFile, stsBean.tempFilePath)
        AliYunOssUploadOrDownFileConfig.getInstance(context)
            .uploadFile(stsBean.bucketName, path, upFile, "", 0)
        AliYunOssUploadOrDownFileConfig.getInstance(context).setOnUploadFile(object :
            AliYunOssUploadOrDownFileConfig.OnUploadFile {
            override fun onUploadFileSuccess(info: String) {
                // 身份证上传
                val bean = OcrRequestBean("${MConstant.imgcdn}${path}", "ID_CARD", path)
                BaseApplication.currentViewModelScope.launch {
                    val body = HashMap<String, Any>()
                    body["imgExt"] = bean.imgExt
                    body["imgType"] = bean.imgType
                    body["ocrSceneType"] = bean.ocrSceneType
                    val rKey = getRandomKey()
                    fetchRequest {
                        apiService.ocr(body.header(rKey), body.body(rKey))
                    }.also {
                        if (it.code == 0) {
                            "上传成功".toast()
                            it.data?.picUrl = path
                            dialog.dismiss()
                            listener.upLoadInfo(it)
                        } else {
                            it.msg.toast()
                        }

                    }.onWithMsgFailure {
                        it?.toast()
                        dialog.dismiss()
                    }
                }
            }

            override fun onUploadFileFailed(errCode: String) {
                dialog.dismiss()
            }

            override fun onuploadFileprogress(
                request: PutObjectRequest,
                currentSize: Long,
                totalSize: Long
            ) {
            }
        })
    }


    //图片上传
    private fun uploadImg(
        context: Context,
        upFile: String,
        stsBean: STSBean,
        dialog: LoadDialog,
        listener: OSSImageListener
    ) {
        AliYunOssUploadOrDownFileConfig.getInstance(context).initOss(
            stsBean.endpoint, stsBean.accessKeyId,
            stsBean.accessKeySecret, stsBean.securityToken
        )
        val path = createFileName(upFile, stsBean.tempFilePath)
        AliYunOssUploadOrDownFileConfig.getInstance(context)
            .uploadFile(stsBean.bucketName, path, upFile, "", 0)
        AliYunOssUploadOrDownFileConfig.getInstance(context).setOnUploadFile(object :
            AliYunOssUploadOrDownFileConfig.OnUploadFile {
            override fun onUploadFileSuccess(info: String) {
                dialog.dismiss()
                listener.getPicUrl(path)
            }

            override fun onUploadFileFailed(errCode: String) {
                dialog.dismiss()
            }

            override fun onuploadFileprogress(
                request: PutObjectRequest,
                currentSize: Long,
                totalSize: Long
            ) {
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

    interface OSSListener {
        fun upLoadInfo(info: CommonResponse<OcrBean>)
    }

    interface OSSImageListener{
        fun getPicUrl(url:String)
    }
}