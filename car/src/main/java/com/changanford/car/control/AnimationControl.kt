package com.changanford.car.control

import android.view.View
import android.view.animation.AlphaAnimation
import android.view.animation.Animation
import android.view.animation.TranslateAnimation




/**
 * @Author : wenke
 * @Time : 2022/1/18 0018
 * @Description : CarControl
 */
class AnimationControl {
    fun startAnimation(view: View){
        val height=view.height
        //位移
        val animation: Animation = TranslateAnimation(0f, 0f, -height.toFloat(), 0f)
        animation.duration = 1500
        animation.repeatCount = 0 //动画的反复次数
        animation.fillAfter = true //设置为true，动画转化结束后被应用
        //透明
        val alphaAnimation = AlphaAnimation(0f, 1f) //透明度从0~1
        alphaAnimation.duration = 1000 //持续时间

        view.startAnimation(alphaAnimation)
        view.startAnimation(animation)
    }
}