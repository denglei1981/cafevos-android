package com.changanford.circle.ui.activity

import android.animation.ValueAnimator
import android.animation.ValueAnimator.AnimatorUpdateListener
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.media.MediaPlayer
import android.os.Handler
import android.os.Message
import android.text.TextUtils
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.view.ViewConfiguration
import android.view.animation.LinearInterpolator
import android.widget.*
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.alibaba.android.arouter.facade.annotation.Route
import com.changanford.circle.R
import com.changanford.circle.databinding.AudioeditBinding
import com.changanford.common.basic.BaseActivity
import com.changanford.common.basic.EmptyViewModel
import com.changanford.common.router.path.ARouterCirclePath
import com.changanford.common.ui.dialog.LoadDialog
import com.changanford.common.ui.videoedit.*
import com.changanford.common.util.AppUtils
import com.changanford.common.util.FileSizeUtil
import com.changanford.common.util.MConstant
import com.changanford.common.util.bus.LiveDataBus
import com.changanford.common.util.bus.LiveDataBusKey
import com.changanford.common.utilext.toast
import com.lansosdk.videoeditor.VideoEditor
import com.luck.picture.lib.tools.ScreenUtils
import com.vincent.videocompressor.videocompressor.VideoCompress
import io.reactivex.disposables.CompositeDisposable
import java.io.File
import java.lang.ref.WeakReference
import java.math.RoundingMode
import java.text.DecimalFormat
import java.util.*
import kotlin.concurrent.schedule
import kotlin.concurrent.thread
import kotlin.math.abs

/**
 * 视频裁剪
 */
@Route(path = ARouterCirclePath.PictureEditAudioActivity)
class PictureEditAudioActivity : BaseActivity<AudioeditBinding, EmptyViewModel>(),
    RangeSeekBar.OnRangeSeekBarChangeListener {
    private val TAG = PictureEditAudioActivity::class.java.simpleName
    private val MIN_CUT_DURATION = 3 * 1000L // 最小剪辑时间1s

    private val MAX_CUT_DURATION = 60 * 1000L //视频最多剪切多长时间

    private val MAX_COUNT_RANGE = 10 //seekBar的区域内一共有多少张图片
    private lateinit var mExtractVideoInfoUtil: ExtractVideoInfoUtil
    private var mMaxWidth = 0
    private var duration = 0L  //视频时长

    private lateinit var seekBar: RangeSeekBar
    private var videoEditAdapter: VideoEditAdapter? = null
    private var averageMsPx //每毫秒所占的px
            = 0f
    private var averagePxMs //每px所占用的ms毫秒
            = 0f
    private lateinit var OutPutFileDirPath: String  //视频裁剪文件目录
    private lateinit var OutMoviePath: String  //视频裁剪名称后缀
    private lateinit var mExtractFrameWorkThread: ExtractFrameWorkThread
    private var leftProgress = 0L
    private var rightProgress = 0L
    private var scrollPos = 0L
    private var mScaledTouchSlop = 0
    private var lastScrollX = 0
    private var isSeeking = false
    private lateinit var path: String //传递进来的视频路劲

    private var videoWidth = 0
    private var videoHeight = 0

    private var startCropTime = 0
    private var endcropTime = 0

    //add by tanhaiqin
    private var thumbnailsCount = 0
    private val compositeDisposable by lazy {
        CompositeDisposable()
    }

    private var isOverScaledTouchSlop = false

    private lateinit var animator: ValueAnimator
    private lateinit var cutHandler: Handler
    private lateinit var finalPath: String   //最终向外输出的文件
    private lateinit var cutpath: String

    val progressDialog: LoadDialog by lazy {
        LoadDialog(this)
    }
    private val mEditor by lazy {
        VideoEditor()
    }
    private val mUIHandler by lazy {
        MainHandler(this)
    }

    companion object {
        const val EDIT_VIDEOPATH = 0X4568
    }

    override fun initView() {
        AppUtils.setStatusBarPaddingTop(binding.title.commTitleBar, this)
        binding.title.barTvOther.visibility = View.VISIBLE
        binding.title.barTvOther.text = "下一步"
        binding.title.barTvTitle.text = "视频最长60秒"
        binding.title.barTvOther.setTextColor(resources.getColor(R.color.white))
        binding.title.barTvOther.textSize = 12f
        binding.title.barTvOther.background = resources.getDrawable(R.drawable.post_btn_bg)
        path = intent?.extras?.getString("path").toString()
        if (TextUtils.isEmpty(path) || !File(path).exists()) {
            "视频文件不存在".toast()
            finish()
        }
        Log.i(TAG, "TANHQ===> getMyIntent() video_path:$path")
        mExtractVideoInfoUtil = ExtractVideoInfoUtil(path)
        duration = mExtractVideoInfoUtil!!.videoLength.toLong()
        mMaxWidth = ScreenUtils.getScreenWidth(this) - ScreenUtils.dip2px(this, 70f)
        mScaledTouchSlop = ViewConfiguration.get(this).scaledTouchSlop
        binding.mRecyclerView.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        videoEditAdapter = VideoEditAdapter(
            this,
            (ScreenUtils.getScreenWidth(this) - ScreenUtils.dip2px(this, 70f)) / 10
        )
        binding.mRecyclerView.adapter = videoEditAdapter

    }

    @SuppressLint("SetTextI18n")
    override fun initData() {
        binding.mRecyclerView.addOnScrollListener(mOnScrollListener)
        initEditVideo()
        initPlay()
        binding.tvchoosetime.text = "已选取${gettext(leftProgress, rightProgress)}s"
        startCropTime = (leftProgress / 1000).toInt()
        endcropTime = (rightProgress / 1000).toInt()
        binding.title.barTvOther.setOnClickListener {
            oncut()
        }
        binding.title.barImgBack.setOnClickListener {
            finish()
        }
        LiveDataBus.get().with(LiveDataBusKey.PICTURESEDITED).observe(this, Observer {
            finish()
        })
    }

    override fun onBackPressed() {
        super.onBackPressed()
    }

    fun oncut() {
        Log.d(
            "shijian",
            "${(leftProgress / 1000).toInt()}----${((rightProgress - leftProgress) / 1000).toInt()}"
        )
        cutHandler = object : Handler() {
            override fun handleMessage(msg: Message) {
                super.handleMessage(msg)
                if (msg.what == 1) {
                    var cutpath = msg.obj.toString()
                    if (cutpath.isNotEmpty()) {
                        if (FileSizeUtil.getFileOrFilesSize(cutpath, 3) < 60) {
                            progressDialog.dismiss()
                            finalPath = cutpath
                            Callback()
                        } else if (FileSizeUtil.getFileOrFilesSize(cutpath, 3) < 100) {
                            //                                    String compressPath = SiliCompressor.with(EsayVideoEditActivity.this).compressVideo(cutPath, Environment.getExternalStorageDirectory().getPath() + "/Uni/video",1080,1920,1200000);
                            val task: VideoCompress.VideoCompressTask =
                                VideoCompress.compressVideoLow(
                                    cutpath,
                                    OutPutFileDirPath + OutMoviePath,
                                    object : VideoCompress.CompressListener {
                                        override fun onStart() {
                                            //Start Compress
                                        }

                                        override fun onSuccess() {
                                            //Finish successfully
                                            runOnUiThread {
                                                progressDialog.dismiss()
                                                finalPath = cutpath
                                                Callback()
//                                            val intent = Intent()
//                                            intent.putExtra("cutPath", mpath)
//                                            intent.putExtra("time", ReturnTime)
//                                            setResult(RESULT_OK, intent)
//                                            finish()
                                            }
                                        }

                                        override fun onFail() {
                                            //Failed
                                        }

                                        override fun onProgress(percent: Float) {
                                            //Progress
                                            progressDialog.setTvprogress(
                                                "${percent.toInt()}%"
                                            )
                                        }
                                    })
                        }
                    }
                }
            }
        }
        binding.uVideoView.stopPlayback()
        OutPutFileDirPath = MConstant.ftFilesDir
        OutMoviePath = "${System.currentTimeMillis()}.mp4"
        File(OutPutFileDirPath).apply {
            if (!exists()) {
                mkdirs()
            }
        }
        runOnUiThread {
            progressDialog.setLoadingText("视频处理中...")
            progressDialog.show()
        }

        thread {
            cutpath = mEditor.executeCutVideo(path, leftProgress, rightProgress)
            cutHandler.sendMessage(Message().apply {
                what = 1
                obj = cutpath
            })
        }

    }

    private fun initEditVideo() {
        //for video edit
        val startPosition: Long = 0
        val endPosition = duration
        val rangeWidth: Int
        val isOver_10_s: Boolean
        if (endPosition <= MAX_CUT_DURATION) {
            isOver_10_s = false
            thumbnailsCount = MAX_COUNT_RANGE
            rangeWidth = mMaxWidth
        } else {
            isOver_10_s = true
            thumbnailsCount =
                ((endPosition * 1.0f / (MAX_CUT_DURATION * 1.0f) * MAX_COUNT_RANGE).toInt())
            rangeWidth = mMaxWidth / MAX_COUNT_RANGE * thumbnailsCount
        }
        binding.mRecyclerView.addItemDecoration(
            EditSpacingItemDecoration(
                ScreenUtils.dip2px(this, 35f),
                thumbnailsCount
            )
        )

        //init seekBar
        if (isOver_10_s) {
            seekBar = RangeSeekBar(this, 0L, MAX_CUT_DURATION)
            seekBar.selectedMinValue = 0L
            seekBar.selectedMaxValue = MAX_CUT_DURATION
        } else {
            seekBar = RangeSeekBar(this, 0L, endPosition)
            seekBar.selectedMinValue = 0L
            seekBar.selectedMaxValue = endPosition
        }
        seekBar.setMin_cut_time(MIN_CUT_DURATION) //设置最小裁剪时间
        seekBar.isNotifyWhileDragging = true
        seekBar.setOnRangeSeekBarChangeListener(this)
        binding.idSeekBarLayout!!.addView(seekBar)
        Log.d(TAG, "-------thumbnailsCount--->>>>$thumbnailsCount")
        averageMsPx = duration * 1.0f / rangeWidth * 1.0f
        Log.d(TAG, "-------rangeWidth--->>>>$rangeWidth")
        Log.d(TAG, "-------localMedia.getDuration()--->>>>$duration")
        Log.d(TAG, "-------averageMsPx--->>>>$averageMsPx")
        OutPutFileDirPath = PictureUtils.getSaveEditThumbnailDir(this)
        val extractW: Int = (ScreenUtils.getScreenWidth(this) - ScreenUtils.dip2px(
            this,
            70f
        )) / MAX_COUNT_RANGE
        val extractH: Int = ScreenUtils.dip2px(this, 55f)
        mExtractFrameWorkThread = ExtractFrameWorkThread(
            extractW,
            extractH,
            mUIHandler,
            path,
            OutPutFileDirPath,
            startPosition,
            endPosition,
            thumbnailsCount
        )
        mExtractFrameWorkThread.start()

        //init pos icon start
        leftProgress = 0
        rightProgress = if (isOver_10_s) {
            MAX_CUT_DURATION
        } else {
            endPosition
        }
        averagePxMs = mMaxWidth * 1.0f / (rightProgress - leftProgress)
        Log.d(TAG, "------averagePxMs----:>>>>>$averagePxMs")

        //add by tanhaiqin thumbnailsCount 传递到Recycler Adapter中 用于更新 "完成"按钮状态
//        if (videoEditAdapter != null) {
//            videoEditAdapter!!.setThumbnailsCount(thumbnailsCount)
//            Log.d(TAG, "videoEditAdapter.setListener")
//            videoEditAdapter!!.setListener(object : VideoEditAdapter.EditAdapterListener {
//                override fun enable(enable: Boolean) {
//                    if (edit_ok == null) return
//                    edit_ok!!.isEnabled = enable //完成按钮 可用
//                }
//            })
//        }
        //end
    }


    /**
     * 水平滑动了多少px
     *
     * @return int px
     */
    private fun getScrollXDistance(): Int {
        val layoutManager = binding.mRecyclerView.layoutManager as LinearLayoutManager
        val position = layoutManager.findFirstVisibleItemPosition()
        val firstVisibleChildView = layoutManager.findViewByPosition(position)
        val itemWidth = firstVisibleChildView!!.width
        return position * itemWidth - firstVisibleChildView.left
    }

    private fun videoPause() {
        isSeeking = false
        if (binding.uVideoView != null && binding.uVideoView.isPlaying) {
            binding.uVideoView.pause()
            handler.removeCallbacks(run)
        }
        Log.d(TAG, "----videoPause----->>>>>>>")
        if (binding.positionIcon.visibility == View.VISIBLE) {
            binding.positionIcon.visibility = View.GONE
        }
        binding.positionIcon.clearAnimation()
        if (::animator.isInitialized && animator != null && animator.isRunning) {
            animator.cancel()
        }
    }


    private val handler = Handler()
    private val run: Runnable = object : Runnable {
        override fun run() {
            videoProgressUpdate()
            handler.postDelayed(this, 1000)
        }
    }

    private fun videoProgressUpdate() {
        val currentPosition = binding.uVideoView!!.currentPosition.toLong()
//        Log.d(TAG, "----onProgressUpdate-cp---->>>>>>>$currentPosition")
        if (currentPosition >= rightProgress) {
            binding.uVideoView.seekTo(leftProgress.toInt())
            binding.positionIcon!!.clearAnimation()
            if (::animator.isInitialized && animator != null && animator.isRunning) {
                animator.cancel()
            }
            anim()
        }
    }

    private fun anim() {
        Log.d(
            TAG,
            "--anim--onProgressUpdate---->>>>>>>" + binding.uVideoView!!.currentPosition
        )
        if (binding.positionIcon!!.visibility == View.GONE) {
            binding.positionIcon.visibility = View.VISIBLE
        }
        val params = binding.positionIcon.layoutParams as FrameLayout.LayoutParams
        val start = (ScreenUtils.dip2px(
            this,
            35f
        ) + (leftProgress /*mVideoView.getCurrentPosition()*/ - scrollPos) * averagePxMs).toInt()
        val end =
            (ScreenUtils.dip2px(this, 35f) + (rightProgress - scrollPos) * averagePxMs).toInt()
        animator = ValueAnimator
            .ofInt(start, end)
            .setDuration(rightProgress - scrollPos - (leftProgress /*mVideoView.getCurrentPosition()*/ - scrollPos))
        animator.interpolator = LinearInterpolator()
        animator.addUpdateListener(AnimatorUpdateListener { animation ->
            params.leftMargin = animation.animatedValue as Int
            binding.positionIcon.layoutParams = params
        })
        animator.start()
    }

    private class MainHandler internal constructor(activity: PictureEditAudioActivity) :
        Handler() {
        private val mActivity: WeakReference<PictureEditAudioActivity> = WeakReference(activity)
        override fun handleMessage(msg: Message) {
            val activity = mActivity.get()
            if (activity != null) {
                if (msg.what == ExtractFrameWorkThread.MSG_SAVE_SUCCESS) {
                    if (activity.videoEditAdapter != null) {
                        val info = msg.obj as VideoEditInfo
                        activity.videoEditAdapter!!.addItemVideoInfo(info)
                    }
                }
            }
        }

    }

    @SuppressLint("SetTextI18n")
    override fun onRangeSeekBarValuesChanged(
        bar: RangeSeekBar?,
        minValue: Long,
        maxValue: Long,
        action: Int,
        isMin: Boolean,
        pressedThumb: RangeSeekBar.Thumb?
    ) {
        Log.d(TAG, "-----minValue----->>>>>>$minValue");
        Log.d(TAG, "-----maxValue----->>>>>>$maxValue");
        leftProgress = minValue + scrollPos;
        rightProgress = maxValue + scrollPos;
        Log.d(TAG, "-----leftProgress----->>>>>>$leftProgress");
        Log.d(TAG, "-----rightProgress----->>>>>>$rightProgress");
        startCropTime = (leftProgress / 1000).toInt()
        endcropTime = (rightProgress / 1000).toInt()
        binding.tvchoosetime.text = "已选取${gettext(leftProgress, rightProgress)}s"
        when (action) {
            MotionEvent.ACTION_DOWN -> {
                Log.d(TAG, "-----ACTION_DOWN---->>>>>>")
                isSeeking = false;
                videoPause();
            }

            MotionEvent.ACTION_MOVE -> {
                Log.d(TAG, "-----ACTION_MOVE---->>>>>>");
                isSeeking = true;
                binding.uVideoView.seekTo(
                    (if (pressedThumb == RangeSeekBar.Thumb.MIN) leftProgress else rightProgress).toInt()
                )
            }

            MotionEvent.ACTION_UP -> {
                Log.d(TAG, "-----ACTION_UP--leftProgress--->>>>>>$leftProgress");
                isSeeking = false;
                //从minValue开始播
                binding.uVideoView.seekTo(leftProgress.toInt());
            }

        }

    }


    private fun initPlay() {
        binding.uVideoView.setVideoPath(path)
        //设置videoview的OnPrepared监听
        binding.uVideoView.setOnPreparedListener { mp -> //得到视频宽高信息
            videoWidth = mp.videoWidth
            videoHeight = mp.videoHeight
            //Log.e(TAG, " TANHQ===> videoWidth: " + videoWidth + ", videoHeight: " + videoHeight);

            //设置MediaPlayer的OnSeekComplete监听
            mp.setOnSeekCompleteListener {
                Log.d(TAG, "------ok----real---start-----")
                Log.d(TAG, "------isSeeking-----$isSeeking")
                if (!isSeeking) {
                    videoStart()
                }
            }
            mp.isLooping = true

        }
        //first
//        binding.uVideoView.start()
//        Timer().schedule(300) {
//            binding.uVideoView.post {
//                binding.uVideoView.pause()
//            }
//        }
        videoStart()

        // 获取开始时间

        // 获取开始时间
        val startS = leftProgress.toInt() / 1000
        // 获取结束时间
        // 获取结束时间
        val endS = rightProgress.toInt() / 1000
        Log.d(
            TAG,
            "-------leftProgress:>>>>>" + leftProgress + "---" + rightProgress + "starts" + startS + "ends" + endS
        )
    }

    private fun videoStart() {
        Log.d(TAG, "----videoStart----->>>>>>>")
        binding.uVideoView.start()
        binding.positionIcon.clearAnimation()
        if (::animator.isInitialized && animator != null && animator.isRunning) {
            animator.cancel()
        }
        anim()
        handler.removeCallbacks(run)
        handler.post(run)
    }


    private val mOnScrollListener = object : RecyclerView.OnScrollListener() {
        override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
            super.onScrollStateChanged(recyclerView, newState)
            Log.d(TAG, "-------newState:>>>>>$newState")
            if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                isSeeking = false
                //                videoStart();
            } else {
                isSeeking = true
                if (isOverScaledTouchSlop && binding.uVideoView != null && binding.uVideoView.isPlaying) {
                    videoPause()
                }
            }
        }

        @SuppressLint("SetTextI18n")
        override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
            super.onScrolled(recyclerView, dx, dy)
            isSeeking = false
            val scrollX: Int = getScrollXDistance()
            //达不到滑动的距离
            if (abs(lastScrollX - scrollX) < mScaledTouchSlop) {
                isOverScaledTouchSlop = false
                return
            }
            isOverScaledTouchSlop = true
            Log.d(TAG, "-------scrollX:>>>>>$scrollX")
            //初始状态,why ? 因为默认的时候有35dp的空白！
            if (scrollX == -ScreenUtils.dip2px(this@PictureEditAudioActivity, 35f)) {
                scrollPos = 0
            } else {
                // why 在这里处理一下,因为onScrollStateChanged早于onScrolled回调
                if (binding.uVideoView != null && binding.uVideoView.isPlaying) {
                    videoPause()
                }
                isSeeking = true
                scrollPos = (averageMsPx * (ScreenUtils.dip2px(
                    this@PictureEditAudioActivity,
                    35f
                ) + scrollX)).toLong()
                Log.d(TAG, "-------scrollPos:>>>>>$scrollPos")
                leftProgress = seekBar.selectedMinValue + scrollPos
                rightProgress = seekBar.selectedMaxValue + scrollPos
                Log.d(TAG, "-------leftProgress:>>>>>$leftProgress")
                startCropTime = (leftProgress / 1000).toInt()
                endcropTime = (rightProgress / 1000).toInt()
                val num = 1.34567
                val df = DecimalFormat("#.###")
                df.roundingMode = RoundingMode.CEILING
                binding.tvchoosetime.text = "已选取${gettext(leftProgress, rightProgress)}s"
                binding.uVideoView.seekTo(leftProgress.toInt())
            }
            lastScrollX = scrollX
        }
    }

    fun gettext(left: Long, right: Long): Long {
        return Math.round((right - left) / 1000.0)
    }


    override fun onDestroy() {
        if (::animator.isInitialized && animator != null) {
            animator.cancel()
        }
        if (binding.uVideoView != null) {
            binding.uVideoView.stopPlayback()
        }
        if (::mExtractVideoInfoUtil.isInitialized && mExtractVideoInfoUtil != null) {
            mExtractVideoInfoUtil.release()
        }
        binding.mRecyclerView.removeOnScrollListener(mOnScrollListener)
        if (::mExtractFrameWorkThread.isInitialized && mExtractFrameWorkThread != null) {
            mExtractFrameWorkThread.stopExtract()
        }
        mUIHandler.removeCallbacksAndMessages(null)
        handler.removeCallbacksAndMessages(null)
//        if (!TextUtils.isEmpty(OutPutFileDirPath)) {
//            PictureUtils.deleteFile(File(OutPutFileDirPath))
//        }
        if (compositeDisposable != null) {
            compositeDisposable.dispose()
        }
        super.onDestroy()
    }

    fun Callback() {
//        cutpath?.let {
//            startARouter(ARouterCirclePath.VideoChoseFMActivity, Bundle().apply {
//                putString("cutpath",cutpath)
//            })
//        }

        cutpath?.let {
            var intent = Intent()
            intent.putExtra("finalPath", finalPath)
            setResult(Activity.RESULT_OK, intent)
            finish()
        }
    }


}