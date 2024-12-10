package com.changanford.common.ui.activity

import android.content.Context
import android.graphics.Bitmap
import android.util.Log
import com.alibaba.android.arouter.facade.annotation.Route
import com.alibaba.sdk.android.oss.model.PutObjectRequest
import com.changanford.common.R
import com.changanford.common.basic.BaseActivity
import com.changanford.common.bean.STSBean
import com.changanford.common.databinding.ActivitySignatureBinding
import com.changanford.common.router.path.ARouterCommonPath
import com.changanford.common.ui.dialog.LoadDialog
import com.changanford.common.util.AliYunOssUploadOrDownFileConfig
import com.changanford.common.util.bus.LiveDataBus
import com.changanford.common.util.bus.LiveDataBusKey
import com.changanford.common.util.ext.setOnFastClickListener
import com.changanford.common.util.toolbar.Builder
import com.changanford.common.util.toolbar.initTitleBar
import com.changanford.common.utilext.toast
import com.changanford.common.viewmodel.SignatureViewModel
import com.changanford.common.widget.SignatureView
import java.io.File
import java.io.FileOutputStream
import java.io.IOException


/**
 * @author: niubobo
 * @date: 2024/12/6
 * @description：手写签名
 */
@Route(path = ARouterCommonPath.SignatureActivity)
class SignatureActivity : BaseActivity<ActivitySignatureBinding, SignatureViewModel>() {

    private val dialog by lazy {
        LoadDialog(this).apply {
            setCancelable(false)
            setCanceledOnTouchOutside(false)
            setLoadingText("图片上传中..")
            show()
        }
    }

    override fun initView() {
        binding.run {
            title.toolbar.initTitleBar(
                this@SignatureActivity,
                Builder().apply { title = "手写签名" })
            signature.setOnSignatureCompleteListener(object :
                SignatureView.OnSignatureCompleteListener {
                override fun onSignatureComplete(isSigned: Boolean) {
                    ivSure.isEnabled = isSigned
                    ivSure.setImageResource(if (isSigned) R.mipmap.ic_qm_sure else R.mipmap.ic_qm_sure_no)
                }

            })
            ivReset.setOnFastClickListener {
                signature.clear()
            }
            ivSure.setOnFastClickListener {
                dialog.show()
                viewModel.getOSS()
            }
        }
    }

    override fun initData() {
        viewModel.stsBean.observe(this) {
            uploadImg(it, saveBitmapToFile(this, binding.signature.getSignatureBitmap()).toString())
        }
    }

    private fun saveBitmapToFile(context: Context, bitmap: Bitmap): String? {
        val filePath = "${context.getExternalFilesDir(null)}/temp_image.png"
        val file = File(filePath)
        return try {
            FileOutputStream(file).use { out ->
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, out)
            }
            filePath
        } catch (e: IOException) {
            e.printStackTrace()
            null
        }
    }

    private fun uploadImg(
        stsBean: STSBean,
        ytPath: String
    ) {
        val path: String

        AliYunOssUploadOrDownFileConfig.getInstance(this).initOss(
            stsBean.endpoint, stsBean.accessKeyId,
            stsBean.accessKeySecret, stsBean.securityToken
        )

        Log.d("=============", "${ytPath}")
        val type = ytPath.substring(ytPath.lastIndexOf(".") + 1, ytPath.length)
        path = stsBean.tempFilePath + System.currentTimeMillis() + "androidios." + type

        AliYunOssUploadOrDownFileConfig.getInstance(this)
            .uploadFile(stsBean.bucketName, path, ytPath, "", 0)
        AliYunOssUploadOrDownFileConfig.getInstance(this).setOnUploadFile(object :
            AliYunOssUploadOrDownFileConfig.OnUploadFile {
            override fun onUploadFileSuccess(info: String) {
                dialog.dismiss()
                LiveDataBus.get().with(LiveDataBusKey.SIGNATURE_PIC_PATH).postValue(path)
                finish()
            }

            override fun onUploadFileFailed(errCode: String) {
                errCode.toast()
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

}