package com.changanford.circle.ui.activity.baoming

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.alibaba.fastjson.JSON
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.Target
import com.changanford.circle.bean.BaoMingReqBean
import com.changanford.circle.bean.ImageList
import com.changanford.common.basic.BaseApplication
import com.changanford.common.basic.BaseViewModel
import com.changanford.common.bean.AttributeBean
import com.changanford.common.bean.DtoBean
import com.changanford.common.bean.DtoBeanNew
import com.changanford.common.bean.VoteBean
import com.changanford.common.chat.utils.LogUtil
import com.changanford.common.net.*
import com.changanford.common.util.MConstant
import com.changanford.common.utilext.GlideUtils
import com.luck.picture.lib.config.PictureMimeType
import com.luck.picture.lib.entity.LocalMedia
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.IOException

class BaoMingViewModel : BaseViewModel() {

    var attributeBean = MutableLiveData<AttributeBean>()
    var downloadLocalMedias = ArrayList<LocalMedia?>()
    var _downloadLocalMedias = MutableLiveData<ArrayList<LocalMedia?>>()


    fun getAttributes() {
        viewModelScope.launch {
            fetchRequest {
                var body = HashMap<String, Any>()
                var rkey = getRandomKey()
                body["Content-Type"] = "activiity_list_topad"
                apiService.getAttributes(body.header(rkey), body.body(rkey))
            }.onSuccess {
                LogUtil.d(it?.toString())
                attributeBean.value = it
            }
        }
    }

    /**
     * 发布活动
     */
    fun CommitACT(dtoBean: DtoBeanNew, rpo: (CommonResponse<Any>) -> Unit) {
        viewModelScope.launch {
            rpo(fetchRequest {
                var body = HashMap<String, Any>()
                var rkey = getRandomKey()
                body["circleId"] = dtoBean.circleId
                body["coverImgUrl"] = dtoBean.coverImgUrl
                body["attributes"] = dtoBean.attributes
                body["beginTime"] = dtoBean.beginTime
                body["content"] = dtoBean.content
                body["contentImgList"] = dtoBean.contentImgList
                body["endTime"] = dtoBean.endTime

                body["activityAddr"] = dtoBean.activityAddr?:""
                body["cityId"] = dtoBean.cityId?:""
                body["cityName"] = dtoBean.cityName?:""
                body["latitude"] = dtoBean.latitude?:""
                body["longitude"] = dtoBean.longitude?:""
                body["provinceId"] = dtoBean.provinceId?:""
                body["provinceName"] = dtoBean.provinceName?:""
                body["townName"] = dtoBean.townName?:""

                body["signBeginTime"] = dtoBean.signBeginTime
                body["signEndTime"] = dtoBean.signEndTime
                body["title"] = dtoBean.title
                body["wonderfulType"] = dtoBean.wonderfulType
                apiService.ADDActNew(body.header(rkey), body.body(rkey))
            })
        }
    }

    /**
     * 发布投票
     */
    fun AddVote(voteBean: VoteBean,rpo: (CommonResponse<Any>) -> Unit) {
        viewModelScope.launch {
            rpo(fetchRequest {
                var body = HashMap<String, Any>()
                var rkey = getRandomKey()
                body["allowMultipleChoice"] = voteBean.allowMultipleChoice
                body["allowViewResult"] = voteBean.allowViewResult
                body["beginTime"] = voteBean.beginTime
                body["circleId"] = voteBean.circleId
                body["coverImg"] = voteBean.coverImg
                body["endTime"] = voteBean.endTime
                body["optionList"] = voteBean.optionList
                body["title"] = voteBean.title
                body["voteDesc"] = voteBean.voteDesc
                body["voteType"] = voteBean.voteType
                apiService.ADDVote(body.header(rkey), body.body(rkey))
            })
        }
    }


    /**
     * 修改活动
     */
    fun updateActivity(wonderfulId: Int,dtoBean: DtoBeanNew, result: (CommonResponse<Any>) -> Unit) {
        viewModelScope.launch {
            result(fetchRequest {
                var body = HashMap<String, Any>()
                body["dto"] = dtoBean
                body["wonderfulId"] = wonderfulId
                var rkey = getRandomKey()
                apiService.updateActivity(body.header(rkey), body.body(rkey))
            })
        }
    }

    /**
     * 修改投票
     */
    fun updateVote(wonderfulId: Int,addVoteDto:VoteBean, result: (CommonResponse<Any>) -> Unit) {
        viewModelScope.launch {
            result(fetchRequest {
                var body = HashMap<String, Any>()
                body["addVoteDto"] = addVoteDto
                body["wonderfulId"] = wonderfulId
                var rkey = getRandomKey()
                apiService.updateVote(body.header(rkey), body.body(rkey))
            })
        }
    }

    /**
     * 下载图片
     */
    fun downGlideImgs(imageList:List<DtoBeanNew.ContentImg>) {
        viewModelScope.launch(Dispatchers.IO) {
            Observable.fromIterable(imageList)
                .map { t ->
                    if(t.contentImgUrl.isNullOrEmpty()){
                        LocalMedia()
                    }else {
                        try {
                            val file =
                                Glide.with(BaseApplication.INSTANT)
                                    .load(GlideUtils.handleImgUrl(t.contentImgUrl))
                                    .downloadOnly(
                                        Target.SIZE_ORIGINAL,
                                        Target.SIZE_ORIGINAL
                                    ).get()
                            //获取到下载得到的图片，进行本地保存
                            val pictureFolder =
                                MConstant.ftFilesDir
                            //第二个参数为你想要保存的目录名称
                            val appDir = File(pictureFolder)
                            if (!appDir.exists()) {
                                appDir.mkdirs()
                            }
                            val fileName =
                                System.currentTimeMillis().toString() + ".jpg"
                            val destFile = File(appDir, fileName)
                            //把gilde下载得到图片复制到定义好的目录中去
                            copy(file, destFile)
                            val localMedia = LocalMedia(
                                destFile.path,
                                0,
                                PictureMimeType.ofImage(),
                                "image/jpeg"
                            )
                            localMedia.androidQToPath = destFile.path
                            localMedia.realPath = destFile.path
                            localMedia
                        } catch (e: Exception) {
                            e.printStackTrace()
                            LocalMedia("", 0, PictureMimeType.ofImage(), "image/jpeg").apply {
                                androidQToPath = ""
                                realPath = ""
                            }
                        }
                    }
                }
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe {
                    Log.d("lists", JSON.toJSONString(it))
                    downloadLocalMedias.add(it)
                    _downloadLocalMedias.postValue(downloadLocalMedias)
                }
        }
    }

    /**
     * 下载图片
     */
    fun downGlideImg(imageList:List<DtoBeanNew.ContentImg>,result:(LocalMedia)->Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            Observable.fromIterable(imageList)
                .map { t ->
                    if(t.contentImgUrl.isNullOrEmpty()){
                        null
                    }else {
                        try {
                            val file =
                                Glide.with(BaseApplication.INSTANT)
                                    .load(GlideUtils.handleImgUrl(t.contentImgUrl))
                                    .downloadOnly(
                                        Target.SIZE_ORIGINAL,
                                        Target.SIZE_ORIGINAL
                                    ).get()
                            //获取到下载得到的图片，进行本地保存
                            val pictureFolder =
                                MConstant.ftFilesDir
                            //第二个参数为你想要保存的目录名称
                            val appDir = File(pictureFolder)
                            if (!appDir.exists()) {
                                appDir.mkdirs()
                            }
                            val fileName =
                                System.currentTimeMillis().toString() + ".jpg"
                            val destFile = File(appDir, fileName)
                            //把gilde下载得到图片复制到定义好的目录中去
                            copy(file, destFile)
                            val localMedia = LocalMedia(
                                destFile.path,
                                0,
                                PictureMimeType.ofImage(),
                                "image/jpeg"
                            )
                            localMedia.androidQToPath = destFile.path
                            localMedia.realPath = destFile.path
                            localMedia
                        } catch (e: Exception) {
                            e.printStackTrace()
                            LocalMedia("", 0, PictureMimeType.ofImage(), "image/jpeg").apply {
                                androidQToPath = ""
                                realPath = ""
                            }
                        }
                    }
                }
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe {
                    it?.let { it1 -> result(it1) }
                }
        }
    }
    /**
     * 复制文件
     *
     * @param source 输入文件
     * @param target 输出文件
     */
    private fun copy(source: File, target: File) {
        var fileInputStream: FileInputStream? = null
        var fileOutputStream: FileOutputStream? = null
        try {
            fileInputStream = FileInputStream(source)
            fileOutputStream = FileOutputStream(target)
            val buffer = ByteArray(1024)
            while (fileInputStream.read(buffer) > 0) {
                fileOutputStream.write(buffer)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            try {
                fileInputStream!!.close()
                fileOutputStream!!.close()
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }
}