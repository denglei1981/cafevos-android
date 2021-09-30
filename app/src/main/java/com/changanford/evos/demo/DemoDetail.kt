package com.changanford.evos.demo

import android.os.Handler
import android.os.Looper
import android.os.Message
import android.view.View
import androidx.core.view.ViewCompat
import com.changanford.common.basic.BaseActivity
import com.changanford.common.basic.EmptyViewModel
import com.changanford.evos.R
import com.changanford.evos.databinding.DemoDetailBinding

class DemoDetail : BaseActivity<DemoDetailBinding, EmptyViewModel>() {

    override fun initView() {
        ViewCompat.setTransitionName(binding.img, "videoid")
        binding.video.visibility = View.INVISIBLE


        var like = binding.mymotion
        binding.heart.setOnClickListener {
            like.setTransition(R.id.unlike)
            like.transitionToEnd()
        }
        binding.heartgray.setOnClickListener {
            like.setTransition(R.id.like)
            like.transitionToEnd()
        }
    }

    private var handler = MyHandler()

    inner class MyHandler : Handler(Looper.getMainLooper()) {
        override fun handleMessage(msg: Message) {
            super.handleMessage(msg)
            when (msg.what) {
                1 -> {
                    binding.video.visibility = View.VISIBLE
                    binding.video.setVideoPath("https://img.cs.leshangche.com/uni-stars-manager/apk/1630404027815153.mp4")
//                    var mediaController = MediaController(this@DemoDetail)
//                    mediaController.visibility = View.GONE
//                    binding.video.setMediaController(mediaController)
//                    mediaController.setMediaPlayer(binding.video)
                    binding.video.start()
                }
            }
        }
    }

    override fun onStart() {
        super.onStart()
        handler.sendEmptyMessageAtTime(1, 1000L)
    }

    override fun initData() {
    }
}