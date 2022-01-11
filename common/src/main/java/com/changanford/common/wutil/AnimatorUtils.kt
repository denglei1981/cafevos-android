package com.changanford.common.wutil

import android.annotation.SuppressLint
import android.os.Handler
import android.os.Looper
import android.os.Message
import android.view.View
import android.view.animation.Animation
import android.view.animation.LinearInterpolator
import android.view.animation.RotateAnimation
import android.view.animation.ScaleAnimation

/**
 * Author:wenke
 * Update Time:
 * Note:动画工具类
 */
class AnimatorUtils (val view: View){
    private var rotate: RotateAnimation?=null
    private var isStop=false
    private var durationTime:Long=1000
    private lateinit var mScaleAnimation:ScaleAnimation
    private var maxCount=5//最大执行周期
    private var count=0
    fun rotateAnimation() {
        //逆时针旋转
        rotate = RotateAnimation(360f,0f,Animation.RELATIVE_TO_SELF,0.5f,Animation.RELATIVE_TO_SELF,0.5f).apply {
            val lin = LinearInterpolator()
            interpolator = lin
            duration = durationTime//设置动画持续周期
            repeatCount = 1
//          repeatCount = Animation.INFINITE//设定无限循环
            fillAfter = false//动画执行完后是否停留在执行完的状态
//        rotate.startOffset = 10//执行前的等待时间
            view.startAnimation(this)
            handler.sendEmptyMessageDelayed(1, durationTime)
        }
    }
//    fun scaleAnimation(){
//        mScaleAnimation = ScaleAnimation(1.2f, 1f, 1.2f, 1f,Animation.RELATIVE_TO_SELF, 0.5f,1, 0.5f)
//        mScaleAnimation.duration = durationTime//设置动画持续周期
//        mScaleAnimation.repeatCount = 0
////        rotate.repeatCount = Animation.INFINITE//设定无限循环
//        mScaleAnimation.fillAfter = false//动画执行完后是否停留在执行完的状态
////        rotate.startOffset = 10//执行前的等待时间
//        view.startAnimation(mScaleAnimation)
//    }
    fun stopAnimator() {
        isStop=true
    }

    fun resumeAnimator() {
        if(isStop){
            isStop=false
            handler.sendEmptyMessageDelayed(1, durationTime)
            view.startAnimation(rotate)
        }
    }

    @SuppressLint("HandlerLeak")
    private val handler: Handler = object : Handler(Looper.myLooper()!!) {
        override fun handleMessage(msg: Message) {
            if(isStop||maxCount==count){
                rotate?.cancel()
                count=0
                isStop=true
            }else {
                this.sendEmptyMessageDelayed(1, durationTime)
                rotate?.start()
                count++
            }
        }
    }
}