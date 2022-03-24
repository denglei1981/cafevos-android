package com.changanford.car.control

import android.app.Activity
import android.widget.ImageView
import com.changanford.common.util.dk.CompleteView
import com.changanford.common.util.dk.ErrorView
import com.changanford.common.util.dk.PrepareView
import com.changanford.common.util.dk.VodControlView
import com.changanford.common.util.dk.cache.ProxyVideoCacheManager
import com.changanford.common.utilext.GlideUtils
import com.dueeeke.videoplayer.player.VideoView

/**
 * @Author: hpb
 * @Date: 2020/5/15
 * @Des: DK播放器辅助类
 */
class PlayerHelper(private val context: Activity, private val mVideoView: VideoView<*>) {
    /**
     * 控制器
     */
    private val mController by lazy {
        VideoController(context)
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
        mController.setGestureEnabled(false)
        controlView.fullScreenGone()
        mController.isLocked=true
        mVideoView.setLooping(true)
        mVideoView.setVideoController(mController)

    }
    /**
     * 只播放
    * */
    fun purePlayVideo(url: String?){
        startPlay(url)
        mController.setGestureEnabled(false)
        controlView.fullScreenGone()
        mController.isLocked=true
        mVideoView.setLooping(true)
        mVideoView.isMute=true
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
    private fun startPlay(url: String?) {
        url?.apply {
            val cacheServer = ProxyVideoCacheManager.getProxy(context)
            cacheServer.getProxyUrl(GlideUtils.handleImgUrl(this))?.let {
                mVideoView.release()
                mVideoView.setUrl(it)
                mVideoView.start()
            }
        }
    }

    /**
     * 循环播放， 默认不循环播放
     */
    fun setLooping(looping: Boolean) {
        mVideoView.setLooping(looping)
    }
    fun resume(videoUrl:String?) {
        if(getCurrentPlayState()==VideoView.STATE_PAUSED){
            mVideoView.resume()
        }else{
            purePlayVideo(videoUrl)
        }
    }

    fun pause() {
        mVideoView.pause()
    }

    fun release() {
        mVideoView.release()
    }
    fun setMute(isMute:Boolean) {
        mVideoView.isMute=isMute
    }
    fun backPressed(back: (() -> Unit)) {
        if (!mVideoView.onBackPressed()) {
            back()
        }
    }
    private fun getCurrentPlayState():Int{
        return mVideoView.currentPlayState
    }
}