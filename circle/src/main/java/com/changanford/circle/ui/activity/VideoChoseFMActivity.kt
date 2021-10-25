package com.changanford.circle.ui.activity

import android.graphics.Bitmap
import android.media.MediaMetadataRetriever
import android.net.Uri
import android.os.AsyncTask
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.util.Log
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.alibaba.android.arouter.facade.annotation.Route
import com.changanford.circle.R
import com.changanford.circle.adapter.ChoseVideoFMAdapter
import com.changanford.circle.databinding.VideochosefmBinding
import com.changanford.circle.widget.view.ThumbnailSelTimeView
import com.changanford.common.basic.BaseActivity
import com.changanford.common.basic.EmptyViewModel
import com.changanford.common.router.path.ARouterCirclePath
import com.changanford.common.router.startARouter
import com.changanford.common.util.AppUtils
import com.changanford.common.util.FileHelper
import com.changanford.common.util.MConstant
import com.changanford.common.util.PictureUtil
import com.changanford.common.util.bus.LiveDataBus
import com.changanford.common.util.bus.LiveDataBusKey
import com.luck.picture.lib.config.PictureMimeType
import com.luck.picture.lib.entity.LocalMedia
import com.luck.picture.lib.listener.OnResultCallbackListener
import java.lang.ref.WeakReference
import java.util.*
import kotlin.concurrent.schedule

/**
 * 视频选择封面
 */
@Route(path = ARouterCirclePath.VideoChoseFMActivity)
class VideoChoseFMActivity : BaseActivity<VideochosefmBinding, EmptyViewModel>() {
    lateinit var cutpath: String  //视频编辑页面裁剪的视频 不用压缩之后的
    lateinit var mVideoRotation: String
    private var mSelStartTime = 0.5f
    private lateinit var myHandler: Handler

    private var mVideoHeight = 0
    private var mVideoWidth: Int = 0
    var mVideoDuration = 0
    private val mediaMetadata by lazy {
        MediaMetadataRetriever()
    };
    val mBitmapList by lazy {
        arrayListOf<Bitmap>()
    }
    private val mSelCoverAdapter by lazy {
        ChoseVideoFMAdapter()
    }
    private lateinit var picture: String

    companion object {
        private const val Moved = 4
        private const val SEL_TIME = 0
        private const val SUBMIT = 1
        private const val SAVE_BITMAP = 2
         const val  FM_CALLBACK=0x5869
    }

    override fun initView() {
        AppUtils.setStatusBarPaddingTop(binding.title.commTitleBar, this)
        binding.title.barTvOther.visibility = View.VISIBLE
        binding.title.barTvOther.text = "下一步"
        binding.title.barTvOther.setTextColor(resources.getColor(R.color.white))
        binding.title.barTvOther.textSize = 12f
        binding.title.barTvOther.background = resources.getDrawable(R.drawable.post_btn_bg)
        cutpath = intent.extras?.getString("cutpath").toString()
        myHandler = MyHandler(this)
        initThumbs()
        initSetParam()
        binding.cutRecyclerView.layoutManager =
            object : LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false) {
                override fun canScrollHorizontally(): Boolean {
                    return false
                }
            }
        binding.cutRecyclerView.adapter = mSelCoverAdapter

    }

    override fun initData() {
        LiveDataBus.get().with(LiveDataBusKey.PICTURESEDITED).observe(this, Observer {
            finish()
        })
        binding.title.barTvOther.setOnClickListener {
            bitmaptolocamedia()
        }
        binding.rbpicture.setOnClickListener {
            binding.rbpicture.isChecked = true
            binding.rbvcut.isChecked = false
            openPicture()
        }
        binding.rbvcut.setOnClickListener {
            binding.rbpicture.isChecked = false
            binding.rbvcut.isChecked = true


        }
        binding.thumbSelTimeView.setOnScrollBorderListener(object :
            ThumbnailSelTimeView.OnScrollBorderListener {
            override fun OnScrollBorder(start: Float, end: Float) {
//                myHandler.removeMessages(SEL_TIME)
//                val rectLeft: Float = binding.thumbSelTimeView.rectLeft
//                mSelStartTime = mVideoDuration * rectLeft / 1000
//                myHandler.sendEmptyMessage(Moved)
            }

            override fun onScrollStateChange() {

                myHandler.removeMessages(SEL_TIME)
                val rectLeft: Float = binding.thumbSelTimeView.rectLeft
                mSelStartTime = mVideoDuration * rectLeft / 1000
                Log.e("Atest", "onScrollStateChange: $mSelStartTime")
                myHandler.sendEmptyMessage(SEL_TIME)
            }

        })


    }

    private fun bitmaptolocamedia() {
        var path = MConstant.saveIMGpath
        var bitmap = mediaMetadata.getFrameAtTime(
            (mSelStartTime * 1000000).toLong(),
            MediaMetadataRetriever.OPTION_CLOSEST
        )
        FileHelper.saveBitmapToFile(bitmap, path)
        var bundle = Bundle()
        var selectList = arrayListOf(LocalMedia().apply {
            this.path = cutpath
            this.realPath = path
            this.chooseModel = PictureMimeType.ofImage()
            this.mimeType = PictureMimeType.getImageMimeType(path)
        })
        bundle.putParcelableArrayList("picList", selectList)
        bundle.putInt("position", 0)
        bundle.putInt("showEditType", -1)
        bundle.putBoolean("isVideo",true)
        startARouter(ARouterCirclePath.PictureeditlActivity, bundle)

    }

    private fun openPicture() {
        PictureUtil.openGalleryOnePic(this,
            object : OnResultCallbackListener<LocalMedia> {
                override fun onResult(result: MutableList<LocalMedia>?) {
                    val localMedia = result?.get(0)
                    var bundle = Bundle()
                    var selectList = arrayListOf(localMedia)
                    bundle.putParcelableArrayList("picList", selectList)
                    bundle.putInt("position", 0)
                    bundle.putInt("showEditType", -1)
                    bundle.putBoolean("isVideo",true)
                    startARouter(ARouterCirclePath.PictureeditlActivity, bundle)

                }

                override fun onCancel() {

                }

            })
    }


    private class MyHandler(activityWeakReference: VideoChoseFMActivity) :
        Handler() {
        private val mActivityWeakReference = WeakReference(activityWeakReference)

        override fun handleMessage(msg: Message) {
            val activity: VideoChoseFMActivity? = mActivityWeakReference.get()
            if (activity != null) {
                when (msg.what) {
                    SEL_TIME -> {

//                        var bitmap = activity.mediaMetadata.getFrameAtTime(
//                            (activity.mSelStartTime * 1000000).toLong(),
//                            MediaMetadataRetriever.OPTION_CLOSEST
//                        )
                        activity.binding.ivImg.seekTo(activity.mSelStartTime.toInt() * 1000)
                        activity.binding.ivImg.start()
                        Timer().schedule(100) {
                            activity.binding.ivImg.post {
                                activity.binding.ivImg.pause()
                            }
                        }
//                        sendEmptyMessageDelayed(
//                            SEL_TIME,
//                            1000
//                        )

                    }
                    SAVE_BITMAP -> {

                        activity.mBitmapList.add(
                            msg.arg1,
                            msg.obj as Bitmap
                        )
                    }

                    SUBMIT -> {
                        activity.mSelCoverAdapter.addBitmapList(activity.mBitmapList)
                        activity.binding.thumbSelTimeView.visibility = View.VISIBLE
                        sendEmptyMessageDelayed(
                            SEL_TIME,
                            1000
                        )
                    }
                    Moved -> {
//                        activity.binding.ivImg.seekTo(activity.mSelStartTime.toInt() * 1000)
//                        activity.binding.ivImg.start()
                    }
                }
            }
        }

    }


    private fun initThumbs() {
        mediaMetadata.setDataSource(this, Uri.parse(cutpath))
        mVideoRotation =
            mediaMetadata.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_ROTATION)!!
        mVideoWidth =
            mediaMetadata.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_WIDTH)!!.toInt()
        mVideoHeight =
            mediaMetadata.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_HEIGHT)!!
                .toInt()
        mVideoDuration =
            mediaMetadata.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)!!.toInt()
        val frame = 10
        val frameTime: Int = mVideoDuration / frame * 1000

        var mtask = object : AsyncTask<Void?, Void?, Boolean?>() {

            override fun onPostExecute(result: Boolean?) {
                myHandler.sendEmptyMessage(SUBMIT)
            }

            override fun doInBackground(vararg params: Void?): Boolean? {
                for (x in 0 until frame) {
                    val bitmap = mediaMetadata.getFrameAtTime(
                        (frameTime * x).toLong(),
                        MediaMetadataRetriever.OPTION_CLOSEST_SYNC
                    )
                    val msg: Message = myHandler.obtainMessage()
                    msg.what = SAVE_BITMAP
                    msg.obj = bitmap
                    msg.arg1 = x
                    myHandler.sendMessage(msg)
                }
//                mediaMetadata.release()
                return true
            }
        }.execute()
    }

    private fun initSetParam() {
        val layoutParams: ViewGroup.LayoutParams = binding.ivImg.layoutParams
        if (mVideoRotation == "0" && mVideoWidth > mVideoHeight) { //本地视频横屏 0表示竖屏
            layoutParams.width = 1120
            layoutParams.height = 830
        } else {
            layoutParams.width = 880
            layoutParams.height = 1220
        }
        binding.ivImg.layoutParams = layoutParams
        binding.ivImg.setVideoPath(cutpath)
        binding.ivImg.start()
        binding.ivImg.duration
    }

}