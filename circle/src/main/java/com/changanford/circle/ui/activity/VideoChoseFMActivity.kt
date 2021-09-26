package com.changanford.circle.ui.activity

import android.graphics.Bitmap
import android.media.MediaMetadataRetriever
import android.net.Uri
import android.os.AsyncTask
import android.os.Handler
import android.os.Message
import android.util.Log
import android.view.ViewGroup
import androidx.annotation.UiThread
import androidx.recyclerview.widget.LinearLayoutManager
import com.alibaba.android.arouter.facade.annotation.Route
import com.changanford.circle.adapter.ChoseVideoFMAdapter
import com.changanford.circle.databinding.VideochosefmBinding
import com.changanford.circle.widget.view.ThumbnailSelTimeView
import com.changanford.common.basic.BaseActivity
import com.changanford.common.basic.EmptyViewModel
import com.changanford.common.router.path.ARouterCirclePath
import com.changanford.common.utilext.GlideUtils
import com.changanford.common.utilext.logD
import com.changanford.common.utilext.toast
import com.lansosdk.videoeditor.VideoEditor
import com.luck.picture.lib.tools.ScreenUtils
import java.lang.ref.WeakReference
import java.util.ArrayList
import kotlin.concurrent.thread

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

    companion object {
        private const val SEL_TIME = 0
        private const val SUBMIT = 1
        private const val SAVE_BITMAP = 2
    }

    override fun initView() {
        cutpath = intent.extras?.getString("cutpath").toString()
        myHandler = MyHandler(this)
        initThumbs()
//        initSetParam()
        binding.cutRecyclerView.layoutManager =
            object : LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false) {
                override fun canScrollHorizontally(): Boolean {
                    return false
                }
            }
        binding.cutRecyclerView.adapter = mSelCoverAdapter

    }

    override fun initData() {

        binding.thumbSelTimeView.setOnScrollBorderListener(object :
            ThumbnailSelTimeView.OnScrollBorderListener {
            override fun OnScrollBorder(start: Float, end: Float) {
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


    private class MyHandler(activityWeakReference: VideoChoseFMActivity) :
        Handler() {
        private val mActivityWeakReference = WeakReference(activityWeakReference)

        override fun handleMessage(msg: Message) {
            val activity: VideoChoseFMActivity? = mActivityWeakReference.get()
            if (activity != null) {
                when (msg.what) {
                    SEL_TIME -> {

                        "${activity.mSelStartTime}".toast()
                        var bitmap = activity.mediaMetadata.getFrameAtTime(
                            (activity.mSelStartTime * 1000000).toLong(),
                            MediaMetadataRetriever.OPTION_CLOSEST
                        )
                        activity.binding.ivImg.setImageBitmap(bitmap)
//                        var str = VideoEditor().executeGetOneFrame(activity.cutpath,activity.mSelStartTime,ScreenUtils.getScreenWidth(activity),ScreenUtils.getScreenHeight(activity))
//                        GlideUtils.loadBD(str,activity.binding.ivImg)
                    }
                    SAVE_BITMAP -> {

                        activity.mBitmapList.add(
                            msg.arg1,
                            msg.obj as Bitmap
                        )
                    }

                    SUBMIT -> {
                        activity.mSelCoverAdapter.addBitmapList(activity.mBitmapList)
                        sendEmptyMessageDelayed(
                            SEL_TIME,
                            1000
                        )
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


}