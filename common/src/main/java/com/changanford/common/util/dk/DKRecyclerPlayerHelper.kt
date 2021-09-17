package com.changanford.common.util.dk

import android.annotation.SuppressLint
import android.app.Activity
import android.content.pm.ActivityInfo
import android.view.View
import android.view.ViewGroup
import androidx.annotation.IdRes
import androidx.recyclerview.widget.RecyclerView
import com.dueeeke.videoplayer.controller.IControlComponent
import com.dueeeke.videoplayer.player.VideoView
import com.dueeeke.videoplayer.player.VideoViewManager

/**
 * @Author: hpb
 * @Date: 2020/5/15
 * @Des: DK RecyclerView播放器帮助类
 */
class DKRecyclerPlayerHelper(
    private val context: Activity,
    private val recyclerView: RecyclerView,
    private val mVideoView: VideoView<*>,
    @IdRes private val player_container_id: Int
) {

    /**
     * 控制器
     */
    private val mController by lazy {
        StandardVideoController(context)
    }

    /**
     * 当前播放的位置
     */
    private var mCurPos = -1

    /**
     * 上次播放的位置，用于页面切回来之后恢复播放
     */
    private var mLastPos = mCurPos

    /**
     * 记录上传播放方法
     */
    private var mLastStartPlay: (() -> Unit)? = null

    init {
        mVideoView.setOnStateChangeListener(object : VideoView.SimpleOnStateChangeListener() {
            override fun onPlayStateChanged(playState: Int) {
                //监听VideoViewManager释放，重置状态
                if (playState == VideoView.STATE_IDLE) {
                    Utils.removeViewFormParent(mVideoView)
                    mLastPos = mCurPos
                    mCurPos = -1
                    mLastStartPlay = null
                }
            }
        })

        mController.addControlComponent(ErrorView(context))
        mController.addControlComponent(CompleteView(context))
        mController.addControlComponent(VodControlView(context))
        mVideoView.setVideoController(mController)

        recyclerView.addOnChildAttachStateChangeListener(object :
            RecyclerView.OnChildAttachStateChangeListener {

            override fun onChildViewDetachedFromWindow(view: View) {
                val playerContainer =
                    view.findViewById<ViewGroup>(player_container_id)
                val v = playerContainer?.getChildAt(0)
                if (v != null && v === mVideoView && !mVideoView.isFullScreen) {
                    releaseVideoView()
                }
            }

            override fun onChildViewAttachedToWindow(view: View) {

            }
        })
    }

    /**
     * 开始播放
     * @param position 列表位置
     */
    fun startPlay(
        position: Int,
        url: String,
        prepareView: IControlComponent,
        playerContainer: ViewGroup
    ) {
        if (mCurPos == position) return
        if (mCurPos != -1) {
            releaseVideoView()
        }
        //边播边存
//        String proxyUrl = ProxyVideoCacheManager.getProxy(getActivity()).getProxyUrl(videoBean.getUrl());
//        mVideoView.setUrl(proxyUrl);
        mVideoView.setUrl(url)
        recyclerView.layoutManager?.findViewByPosition(position) ?: return
        //把列表中预置的PrepareView添加到控制器中，注意isPrivate此处只能为true。
        mController.addControlComponent(prepareView, true)
        Utils.removeViewFormParent(mVideoView)
        playerContainer.addView(mVideoView, 0)
        //播放之前将VideoView添加到VideoViewManager以便在别的页面也能操作它
        VideoViewManager.instance().add(mVideoView, Tag.LIST)
        mVideoView.start()
        mCurPos = position
        mLastStartPlay = { startPlay(position, url, prepareView, playerContainer) }
    }

    @SuppressLint("SourceLockedOrientationActivity")
    fun releaseVideoView() {
        mVideoView.release()
        if (mVideoView.isFullScreen) {
            mVideoView.stopFullScreen()
        }
        if (context.requestedOrientation !== ActivityInfo.SCREEN_ORIENTATION_PORTRAIT) {
            context.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        }
        mCurPos = -1
        mLastStartPlay = null
    }

    /**
     * 由于onResume必须调用super。故增加此方法，
     * 子类将会重写此方法，改变onResume的逻辑
     */
    fun resume() {
        if (mLastPos == -1) return
        //恢复上次播放的位置
        mLastStartPlay?.invoke()
    }

    fun pause() {
        releaseVideoView();
    }
}