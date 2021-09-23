package com.changanford.common.util.dk

import android.app.Activity
import android.widget.ImageView
import com.changanford.common.util.dk.cache.ProxyVideoCacheManager
import com.changanford.common.utilext.GlideUtils
import com.dueeeke.videoplayer.player.VideoView

/**
 * @Author: hpb
 * @Date: 2020/5/15
 * @Des: DK播放器辅助类
 */
class DKPlayerHelper(private val context: Activity, private val mVideoView: VideoView<*>) {

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
        VodControlView(context)
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

    fun setMyOnVisibilityChanged(myOnVisibilityChanged: VodControlView.MyOnVisibilityChanged) {
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
        if (!mVideoView.onBackPressed()) {
            back()
        }
    }
}