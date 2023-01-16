package com.changanford.common.util.image

import android.content.Context
import android.util.Log
import com.zs.easy.imgcompress.EasyImgCompress
import com.zs.easy.imgcompress.bean.ErrorBean
import com.zs.easy.imgcompress.listener.OnCompressMultiplePicsListener
import com.zs.easy.imgcompress.util.GBMBKBUtil
import java.io.File

/**
 *Author lcw
 *Time on 2023/1/16
 *Purpose 图片压缩
 */
object ImageCompress {
    fun compressImage(context: Context, imgs: ArrayList<String?>, listener: ImageCompressResult) {
        EasyImgCompress.withMultiPics(context, imgs)
            .maxPx(1200)
            .maxSize(200)
            .enableLog(true)
            .setOnCompressMultiplePicsListener(object : OnCompressMultiplePicsListener {
                override fun onStart() {
                    Log.i("EasyImgCompress", "onStart")
                }

                override fun onSuccess(successFiles: List<File>) {
                    listener.compressSuccess(successFiles)
                    for (i in successFiles.indices) {
                        Log.i(
                            "EasyImgCompress",
                            "onSuccess: successFile size = " + GBMBKBUtil.getSize(
                                successFiles[i].length()
                            ).toString() + "path = " + successFiles[i].absolutePath
                        )
                    }
                }

                override fun onHasError(successFiles: List<File>, errorImages: List<ErrorBean>) {
                    listener.compressFailed()
                    for (i in successFiles.indices) {
                        Log.i(
                            "EasyImgCompress",
                            "onHasError: successFile  size = " + GBMBKBUtil.getSize(
                                successFiles[i].length()
                            ).toString() + "path = " + successFiles[i].absolutePath
                        )
                    }
                    for (i in errorImages.indices) {
                        Log.e(
                            "EasyImgCompress",
                            "onHasError: errorImg url = " + errorImages[i].getErrorImgUrl()
                        )
                        Log.e(
                            "EasyImgCompress",
                            "onHasError: errorImg msg = " + errorImages[i].getErrorMsg()
                        )
                    }
                }
            }).start()
    }

    interface ImageCompressResult {
        fun compressSuccess(list: List<File>)
        fun compressFailed()
    }
}
