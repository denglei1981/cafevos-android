package com.changanford.car.control

import android.view.View
import android.view.animation.AlphaAnimation
import android.view.animation.AnimationSet
import android.view.animation.ScaleAnimation
import android.view.animation.TranslateAnimation
/**
 * @Author : wenke
 * @Time : 2022/1/18 0018
 * @Description : CarControl
 */
class AnimationControl {
    private val durationTime=1500L
    /**
     * [module]1向下 2向上 3向左 4向右
    * */
    fun startAnimation(view: View,module:Int?=1){
        val animationSet = AnimationSet(true) //共用动画补间
        animationSet.duration = durationTime
        //位移
        TranslateAnimation(0f, 0f, -60f, 0f).apply {
            duration = durationTime
            repeatCount = 0 //动画的反复次数
            fillAfter = true //设置为true，动画转化结束后被应用
            animationSet.addAnimation(this)
        }
        //透明
        AlphaAnimation(0.5f, 1f).apply {
            duration = durationTime //持续时间
            fillAfter=false
            animationSet.addAnimation(this)
        }
        //缩放  x轴0倍，x轴1倍，y轴0倍，y轴1倍
        ScaleAnimation(1f, 1f, 0.9f, 1f).apply {
            duration = durationTime
            animationSet.addAnimation(this)
        }
        view.startAnimation(animationSet)
    }
}