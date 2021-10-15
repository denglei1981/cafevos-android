package com.changanford.circle.viewmodel

import android.content.Context
import android.os.Environment
import androidx.lifecycle.MutableLiveData
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.Target
import com.changanford.circle.api.CircleNetWork
import com.changanford.circle.bean.ImageList
import com.changanford.circle.bean.PlateBean
import com.changanford.circle.bean.PostKeywordBean
import com.changanford.circle.bean.PostsDetailBean
import com.changanford.common.MyApp
import com.changanford.common.basic.BaseApplication
import com.changanford.common.basic.BaseViewModel
import com.changanford.common.basic.PostRoomViewModel
import com.changanford.common.bean.LocationDataBean
import com.changanford.common.bean.STSBean
import com.changanford.common.net.*
import com.changanford.common.util.MConstant
import com.changanford.common.util.bus.CircleLiveBusKey
import com.changanford.common.util.bus.LiveDataBus
import com.changanford.common.utilext.GlideUtils
import com.changanford.common.utilext.createHashMap
import com.changanford.common.utilext.toast
import com.luck.picture.lib.config.PictureMimeType
import com.luck.picture.lib.entity.LocalMedia
import com.xiaomi.push.it
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.functions.Function
import io.reactivex.schedulers.Schedulers
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.IOException

class PostViewModule() :PostRoomViewModel(){
    var postsuccess = MutableLiveData<String>()
    val plateBean = MutableLiveData<PlateBean>()
    val cityCode = MutableLiveData<LocationDataBean>()
    val stsBean = MutableLiveData<STSBean>()
    val keywords = MutableLiveData<List<PostKeywordBean>>()
    val postDetailsBean = MutableLiveData<PostsDetailBean>()
    var downloadLocalMedias = ArrayList<LocalMedia>()
    var _downloadLocalMedias = MutableLiveData<ArrayList<LocalMedia>>()

    fun postEdit(params: HashMap<String,Any>){
         launch (block = {
              val body = params

              val rKey = getRandomKey()
              ApiClient.createApi<CircleNetWork>().postEdit(body.header(rKey),body.body(rKey))
                  .onSuccess {
                      postsuccess.value = "upsuccess"
                  }
                  .onFailure {

                  }
          })
      }

    /**
     * 获取发帖模块
     */
    fun getPlate(){
        launch(block =  {
            val body = MyApp.mContext.createHashMap()
            val rKey = getRandomKey()
            ApiClient.createApi<CircleNetWork>().getPlate(body.header(rKey),body.body(rKey))
                .onSuccess {
                     plateBean.value = it
                }
                .onFailure {

                }
        })
    }

    /**
     * 获取发帖模块
     */
    fun getKeyWords(){
        launch(block =  {
            val body = MyApp.mContext.createHashMap()
            val rKey = getRandomKey()
            ApiClient.createApi<CircleNetWork>().getkeywords(body.header(rKey),body.body(rKey))
                .onSuccess {
                    keywords.value = it
                }
                .onFailure {

                }
        })
    }



    /**
     * 获取省市区ID
     */
    fun getCityDetailBylngAndlat(latY: Double,lngX:Double) {
        launch(block =  {
            val body = MyApp.mContext.createHashMap()
            body["latY"] = latY
            body["lngX"] = lngX
            val rKey = getRandomKey()
            ApiClient.createApi<CircleNetWork>().getCityDetailBylngAndlat(body.header(rKey),body.body(rKey))
                .onSuccess {
                    cityCode.value= it
                }
                .onFailure {

                }
        })

    }


    fun getOSS(){
        launch(block =  {
            val body = MyApp.mContext.createHashMap()
            val rKey = getRandomKey()
            ApiClient.createApi<NetWorkApi>().getOSS(body.header(rKey),body.body(rKey))
                .onSuccess {
                    stsBean.value= it
                }
                .onFailure {

                }
        })
    }

    fun getPostById(postsId:String){
        launch(block = {
            val body = MyApp.mContext.createHashMap()
            body["postsId"] = postsId
            val rKey = getRandomKey()

            ApiClient.createApi<CircleNetWork>()
                .getPostsDetail(body.header(rKey), body.body(rKey))
                .onSuccess {
                    postDetailsBean.value = it
                }.onWithMsgFailure {
                    it?.toast()
                    LiveDataBus.get().with(CircleLiveBusKey.CLOSE_POST_DETAILS).postValue(false)
                }
        }, error = {
            it.message?.toast()
        })
    }
    /**
     * 下载图片
     */
    fun downGlideImgs(imageList:List<ImageList>) {
        Observable.fromIterable(imageList)
            .map { t ->
                val file =
                    Glide.with(BaseApplication.curActivity).load(GlideUtils.handleImgUrl(t.imgUrl))
                        .downloadOnly(
                            Target.SIZE_ORIGINAL,
                            Target.SIZE_ORIGINAL
                        ).get()
                //获取到下载得到的图片，进行本地保存
                val pictureFolder =
                    Environment.getExternalStorageDirectory()
                //第二个参数为你想要保存的目录名称
                val appDir = File(pictureFolder, "Uni")
                if (!appDir.exists()) {
                    appDir.mkdirs()
                }
                val fileName =
                    System.currentTimeMillis().toString() + ".jpg"
                val destFile = File(appDir, fileName)
                //把gilde下载得到图片复制到定义好的目录中去
                copy(file, destFile)
                val localMedia =
                    LocalMedia(destFile.path, 0, PictureMimeType.ofImage(), "image/jpeg")
                localMedia.androidQToPath = destFile.path
                localMedia.realPath = destFile.path
                localMedia
            }
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe {
//                yuanPathList.add(it.path)
                downloadLocalMedias.add(it)
                _downloadLocalMedias.postValue(downloadLocalMedias)
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
