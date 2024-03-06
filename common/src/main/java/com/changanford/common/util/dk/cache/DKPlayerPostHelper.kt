package com.changanford.common.util.dk.cache

import android.app.Activity
import android.widget.ImageView
import com.changanford.common.util.dk.CompleteView
import com.changanford.common.util.dk.ErrorView
import com.changanford.common.util.dk.PrepareView
import com.changanford.common.util.dk.StandardVideoController
import com.changanford.common.util.dk.VodControlPostView
import com.changanford.common.utilext.GlideUtils
import com.dueeeke.videoplayer.player.VideoView

/**
 * @Author: hpb
 * @Date: 2020/5/15
 * @Des: DK播放器辅助类
 */
class DKPlayerPostHelper(private val context: Activity, private val mVideoView: VideoView<*>) {


    /**
     * 控制器
     */
    private val mController by lazy {
        StandardVideoController(context)
    }

    /**
     * 默认展示页面
     */
    private val mPrepareView by lazy {
        PrepareView(context)
    }

    private val controlView by lazy {
        VodControlPostView(context)
    }

    init {
        mController.addControlComponent(ErrorView(context))
        mController.addControlComponent(mPrepareView)
        mController.addControlComponent(CompleteView(context))
        mController.addControlComponent(controlView)
        mVideoView.setVideoController(mController)
    }

    fun fullScreenGone() {
        controlView.fullScreenGone()
    }

    fun setMyOnVisibilityChanged(myOnVisibilityChanged: VodControlPostView.MyOnVisibilityChanged) {
        controlView.setMyOnVisibilityChanged(myOnVisibilityChanged)
    }

    fun getThumbImgView(): ImageView {
        return mPrepareView.thumbImgView
    }

    /**
     * 开始播放
     */
    fun startPlay(url: String) {
        val cacheServer = ProxyVideoCacheManager.getProxy(context)
        cacheServer.getProxyUrl(GlideUtils.handleImgUrl(url))?.let {
            mVideoView.release()
            mVideoView.setUrl(it)
            mVideoView.start()
        }
    }

    fun resume() {
        mVideoView.resume()
    }

    fun pause() {
        mVideoView.pause()
    }

    fun release() {
        mVideoView.release()
    }

    fun backPressed(back: (() -> Unit)) {
        release()
        if (!mVideoView.onBackPressed()) {
            back()
        }
    }
}