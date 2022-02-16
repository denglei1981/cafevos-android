package com.changanford.common.buried

import android.content.ContentValues
import android.content.Context
import android.net.Uri
import android.os.Environment
import android.provider.MediaStore
import androidx.work.CoroutineWorker
import androidx.work.Data
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.luck.picture.lib.config.PictureMimeType
import com.luck.picture.lib.tools.DateUtils
import com.luck.picture.lib.tools.PictureFileUtils
import com.luck.picture.lib.tools.SdkVersionUtils
import com.luck.picture.lib.tools.ValueOf
import okio.BufferedSource
import okio.buffer
import okio.source
import java.io.File
import java.io.InputStream
import java.io.OutputStream
import java.net.URL
import java.util.*

/**********************************************************************************
 * @Copyright (C), 2018-2020.
 * @FileName: com.hpb.mvvm.other.repository.MDownLoadPic
 * @Author:　 　
 * @Version : V1.0
 * @Date: 2020/5/29 09:51
 * @Description: 　下载图片
 * *********************************************************************************
 */
class MDownLoadPic(val context: Context, workerParams: WorkerParameters) :
    CoroutineWorker(context, workerParams) {
    override suspend fun doWork(): Result {
        var urlPath = inputData.getString("urlPath")
        if (urlPath.isNullOrEmpty()) return Result.failure()
        var mMimeType = urlPath.substring(urlPath.indexOf("."), urlPath.length)
        var output: Data? = null
        var outImageUri: Uri? = null
        var outputStream: OutputStream? = null
        var inputStream: InputStream? = null
        var inBuffer: BufferedSource? = null
        try {
            if (SdkVersionUtils.checkedAndroid_Q()) {
                outImageUri = createOutImageUri(mMimeType)
            } else {
                val suffix = PictureMimeType.getLastImgSuffix(mMimeType)
                val state = Environment.getExternalStorageState()
                val rootDir =
                    if (state == Environment.MEDIA_MOUNTED) Environment.getExternalStoragePublicDirectory(
                        Environment.DIRECTORY_DCIM
                    ) else context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
                if (rootDir != null) {
                    if (!rootDir.exists()) {
                        rootDir.mkdirs()
                    }
                    val folderDir =
                        File(if (state != Environment.MEDIA_MOUNTED) rootDir.absolutePath else rootDir.absolutePath + File.separator + PictureMimeType.CAMERA + File.separator)
                    if (!folderDir.exists() && folderDir.mkdirs()) {
                    }
                    val fileName =
                        DateUtils.getCreateFileName("IMG_") + suffix
                    val file = File(folderDir, fileName)
                    outImageUri = Uri.fromFile(file)
                }
            }
            if (outImageUri != null) {
                outputStream = Objects.requireNonNull(
                    context.contentResolver.openOutputStream(outImageUri)
                )
                val u = URL(urlPath)
                inputStream = u.openStream()
                inBuffer = inputStream.source().buffer()
                val bufferCopy = PictureFileUtils.bufferCopy(inBuffer, outputStream)
                if (bufferCopy) {
                    var str = PictureFileUtils.getPath(context, outImageUri)
                    output = Data.Builder().putString("urlPath", str).build()

                }
            }
        } catch (e: Exception) {
            if (outImageUri != null && SdkVersionUtils.checkedAndroid_Q()) {
                context.contentResolver.delete(outImageUri, null, null)
            }
            output = Data.Builder().putString("urlPath", e.message).build()
            return Result.failure(output)
        } finally {
            PictureFileUtils.close(inputStream)
            PictureFileUtils.close(outputStream)
            PictureFileUtils.close(inBuffer)
        }
        return output?.let { Result.success(it) } ?: Result.failure()
    }

    /**
     * 针对Q版本创建uri
     *
     * @return
     */
    private fun createOutImageUri(mMimeType: String): Uri? {
        val contentValues = ContentValues()
        contentValues.put(
            MediaStore.Images.Media.DISPLAY_NAME,
            DateUtils.getCreateFileName("IMG_")
        )
        contentValues.put(
            MediaStore.Images.Media.DATE_TAKEN,
            ValueOf.toString(System.currentTimeMillis())
        )
        contentValues.put(MediaStore.Images.Media.MIME_TYPE, mMimeType)
        contentValues.put(MediaStore.Images.Media.RELATIVE_PATH, PictureMimeType.DCIM)
        return context.contentResolver.insert(
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
            contentValues
        )
    }
}