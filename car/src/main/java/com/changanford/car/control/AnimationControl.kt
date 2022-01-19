package com.changanford.car.control

import android.view.View
import android.view.animation.AlphaAnimation
import android.view.animation.AnimationSet
import android.view.animation.TranslateAnimation

/**
 * @Author : wenke
 * @Time : 2022/1/18 0018
 * @Description : CarControl
 */
class AnimationControl {
    private val durationTime=800L
    /**
     * [module]1向下 2向上 3向左 4向右
    * */
    fun startAnimation(view: View,module:Int?=1){
        val animationSet = AnimationSet(true)
//        animationSet.duration = durationTime
        var fromYDelta=0f
        var fromXDelta=0f
        val toYDelta=if(2==module)10f else -10f
        when(module){
            1->{
                fromYDelta=-60f
                fromXDelta=0f
            }
            2->{
                fromYDelta=60f
                fromXDelta=0f
            }
            3->{
                fromYDelta=0f
                fromXDelta=-60f
            }
            4->{
                fromYDelta=0f
                fromXDelta=60f
            }
        }
        //不使用动画
        if(fromYDelta==0f&&fromXDelta==0f){
            return
        }
        //位移
        TranslateAnimation(fromXDelta, 0f, fromYDelta, 0f).apply {
            duration = durationTime
            repeatCount = 0 //动画的反复次数
            fillAfter = false //设置为true，动画转化结束后被应用
            animationSet.addAnimation(this)
        }
        //透明
        AlphaAnimation(0.2f, 1f).apply {
            duration = durationTime+300
            fillAfter=false
            animationSet.addAnimation(this)
        }
//        //缩放  x轴0倍，x轴1倍，y轴0倍，y轴1倍
//        ScaleAnimation(1f, 1f, 0.98f, 1f).apply {
//            duration = durationTime+300
//            animationSet.addAnimation(this)
//        }
        view.startAnimation(animationSet)
//        Handler(Looper.myLooper()!!).postDelayed({
//            startAnimation0(view,module)
//        },durationTime)
    }
   private fun startAnimation0(view: View,module:Int?=1){
        val animationSet = AnimationSet(true) //共用动画补间
        animationSet.duration = durationTime
        val fromYDelta=if(2==module)10f else -10f
        //位移
        TranslateAnimation(0f, 0f, fromYDelta, 0f).apply {
            duration = durationTime
            repeatCount = 0 //动画的反复次数
            fillAfter = false //设置为true，动画转化结束后被应用
            animationSet.addAnimation(this)
        }
//        //透明
        AlphaAnimation(0.8f, 1f).apply {
            duration = durationTime
            fillAfter=false
            animationSet.addAnimation(this)
        }
        view.startAnimation(animationSet)
    }
}