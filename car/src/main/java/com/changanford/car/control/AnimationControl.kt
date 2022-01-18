package com.changanford.car.control

import android.view.View
import android.view.animation.Animation
import android.view.animation.TranslateAnimation




/**
 * @Author : wenke
 * @Time : 2022/1/18 0018
 * @Description : CarControl
 */
class AnimationControl {
    fun startAnimation(view: View){
        val animation: Animation = TranslateAnimation(0f, 0f, 0f, 300f)
        animation.duration = 1500
        animation.repeatCount = 1 //动画的反复次数
        animation.fillAfter = true //设置为true，动画转化结束后被应用
        view.startAnimation(animation)
    }
}