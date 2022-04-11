package com.changanford.shop.viewmodel

import android.content.Context
import androidx.lifecycle.viewModelScope
import com.alibaba.sdk.android.oss.model.PutObjectRequest
import com.changanford.common.bean.STSBean
import com.changanford.common.net.*
import com.changanford.common.util.AliYunOssUploadOrDownFileConfig
import com.changanford.common.utilext.toast
import com.changanford.shop.base.BaseViewModel
import com.changanford.shop.listener.UploadPicCallback
import kotlinx.coroutines.launch

/**
 * @Author : wenke
 * @Time : 2022/4/11 0011
 * @Description : UploadViewModel
 */
class UploadViewModel:BaseViewModel() {
    /**
     * 上传图片
     */

    fun uploadFile(cp: Context, upFiles: List<String>?, callback: UploadPicCallback) {
        upFiles?.let {
            getOSS(cp, it, 0, callback)
        }
    }

    /**
     * 获取上传图片得凭证
     */
   private lateinit var upimgs: ArrayList<String>
    private fun getOSS(context: Context,upfiles: List<String>,count: Int,callback: UploadPicCallback) {
        viewModelScope.launch {
            val body = HashMap<String, Any>()
            val rkey = getRandomKey()
            fetchRequest {
                apiService.getOSS(body.header(rkey), body.body(rkey))
            }.onSuccess {
                initAliYunOss(context, it!!)
                upimgs = ArrayList()
                uploadImgs(context,upfiles,it,count,callback)
            }.onFailure {
                val msg = "上传失败"
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

    private fun uploadImgs(context: Context,upfiles: List<String>,stsBean: STSBean,count: Int,callback: UploadPicCallback) {
        val size = upfiles.size
        AliYunOssUploadOrDownFileConfig.getInstance(context).initOss(
            stsBean.endpoint, stsBean.accessKeyId,
            stsBean.accessKeySecret, stsBean.securityToken
        )
        val path = createFileName(upfiles[count], stsBean.tempFilePath)
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
}