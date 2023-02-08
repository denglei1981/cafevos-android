package com.changanford.circle.ui.activity

import android.media.MediaMetadataRetriever
import android.os.Environment
import android.util.Log
import android.view.View
import android.widget.FrameLayout
import androidx.recyclerview.widget.LinearLayoutManager
import com.alibaba.android.arouter.facade.annotation.Route
import com.changanford.circle.adapter.EditvideoPicAdapter
import com.changanford.circle.bean.PicCutBean
import com.changanford.circle.databinding.EditvideoBinding
import com.changanford.common.basic.BaseActivity
import com.changanford.common.basic.EmptyViewModel
import com.changanford.common.router.path.ARouterCirclePath
import com.changanford.common.util.AppUtils
import com.changanford.common.util.FileUtils
import com.changanford.common.util.MConstant
import com.changanford.common.utilext.logD
import com.changanford.common.widget.RangeSlider
import com.esay.ffmtool.FfmpegTool
import com.luck.picture.lib.entity.LocalMedia
import java.io.File
import java.lang.Exception
import java.lang.NumberFormatException
import java.util.ArrayList
import java.util.concurrent.Executors

@Route(path = ARouterCirclePath.EditVideoActivity)
class EditVideoActivity : BaseActivity<EditvideoBinding, EmptyViewModel>(), RangeSlider.OnRangeChangeListener {
    lateinit var videoPath: String
    private var leftThumbIndex = 0 //滑动条的左端

    private var rightThumbIndex: Int = 0//滑动条的右端
    private var startTime = 0
    private var endTime: Int = 0 //裁剪的开始、结束时间
    private val firstItem = 0 //recycleView当前显示的第一项

    private val lastItem = 0 //recycleView当前显示的最后一项

    val editvideoPicAdapter by lazy {
        EditvideoPicAdapter(parentPath)
    }
    val ffmpegTool by lazy {
        FfmpegTool.getInstance(this)
    }
    val executorService by lazy {
        Executors.newFixedThreadPool(3)
    }
    private var parentPath: String? = null
    private var imagCount = 0 //整个视频要解码图片的总数量
    private var times = 0L;  //视频的时长毫秒
    override fun initView() {
        AppUtils.setStatusBarPaddingTop(binding.title.commTitleBar, this)
        videoPath = intent?.extras?.get("path") as String
        binding.uVideoView.setVideoPath(videoPath)
        binding.uVideoView.start()
        binding.recyclerview.layoutManager = LinearLayoutManager(this).apply {
            orientation = LinearLayoutManager.HORIZONTAL
        }
        times = getVideoDuration(videoPath)
        rightThumbIndex = (times / 1000).toInt()
        endTime=(times / 1000).toInt()
        parentPath =
            MConstant.rootPath + File.separator + "android" + File.separator + "temp" + System.currentTimeMillis() / 1000 + File.separator
        binding.recyclerview.adapter = editvideoPicAdapter
        editvideoPicAdapter.setList(getDataList(times))
        File(parentPath).apply {
            if (!exists()) {
                mkdirs()
            }
        }
        binding.rangeSlider.setRangeIndex(0, (times / 1000).toInt())
        binding.rangeSlider.setTickCount((times / 1000).toInt())

    }

    override fun initData() {
        binding.rangeSlider.setRangeChangeListener(this)

        binding.uVideoView.setOnCompletionListener {
            it.start()
            it.isLooping = true
        }

        ffmpegTool.imageDecodeing = object : FfmpegTool.ImageDecodeing {
            override fun sucessOne(p0: String, p1: Int) {
                Log.d("ffmpegToolsucessOne", p0)
                editvideoPicAdapter.notifyItemRangeChanged(p1, 1)
            }

        }

    }


    /**
     * 运行一个图片的解码任务
     *
     * @param start 解码开始的视频时间 秒
     * @param count 一共解析多少张
     */
    private fun runImagDecodTask(start: Int, count: Int) {
        executorService.execute {
            ffmpegTool.decodToImageWithCall(
                videoPath,
                parentPath,
                start,
                count
            )
        }
    }

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        if (hasFocus) {
            Log.d("recyclerview.width", binding.recyclerview.width.toString())
            editvideoPicAdapter?.with = binding.recyclerview.width
            if (times / 1000 < 10) {

            }
            runImagDecodTask(0, 15)
        }
    }

    /**
     * 根据视频的时长，按秒分割成多个data先占一个位置
     *
     * @return
     */
    fun getDataList(videoTime: Long): List<PicCutBean> {
        val dataList: MutableList<PicCutBean> = ArrayList<PicCutBean>()
        val seconds = (videoTime / 1000).toInt()
        while (imagCount < seconds) {
            dataList.add(PicCutBean(imagCount, "temp$imagCount.jpg"))
            imagCount++
        }
        return dataList
    }


    /**
     * 获取视频的时间长度
     * @param path
     * @return
     */
    fun getVideoDuration(path: String?): Long {
        try {
            val file = File(path)
            if (!file.exists()) return 0
            val mMetadataRetriever = MediaMetadataRetriever()
            mMetadataRetriever.setDataSource(path)
            val time =
                mMetadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)
            mMetadataRetriever.release()
            return stringToLong(time)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return 0
    }

    /**
     * 计算开始结束时间
     */
    private fun calStartEndTime() {
        val num = (times / 1000).toInt()
        if (rightThumbIndex > num) {
            rightThumbIndex = num
        }
        val duration: Int = rightThumbIndex - leftThumbIndex
        startTime = firstItem + leftThumbIndex
        endTime = startTime + duration
        //此时可能视频已经结束，若已结束重新start
        if (!binding.uVideoView.isPlaying) {
            binding.uVideoView.start()
        }
//        tv_time.setVisibility(View.VISIBLE)
//        tv_time.setText("已选:" + (endTime - startTime) + "s")
//        ReturnTime = endTime - startTime
        //把视频跳转到新选择的开始时间
        binding.tvtime.text="${startTime}${endTime}"
        Log.d("startTime",startTime.toString())
        binding.uVideoView.seekTo(startTime * 1000)
    }

    /**
     * 字符串转为long
     * @param str
     * @return
     */
    fun stringToLong(str: String?): Long {
        var num: Long = -1
        try {
            num = java.lang.Long.valueOf(str)
        } catch (e: NumberFormatException) {
            e.printStackTrace()
        }
        return num
    }

    override fun onDestroy() {
        super.onDestroy()
        binding.uVideoView.stopPlayback()
        FileUtils.deleteAllInDir(parentPath)
    }

    override fun onPause() {
        super.onPause()
        binding.uVideoView.pause()
    }

    override fun onRangeChange(view: RangeSlider?, leftPinIndex: Int, rightPinIndex: Int) {
        this.leftThumbIndex = leftPinIndex
        this.rightThumbIndex=rightPinIndex
        calStartEndTime()
    }

}