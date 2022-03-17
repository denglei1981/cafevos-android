package com.changanford.car.control

import android.view.View
import android.view.animation.AlphaAnimation
import android.view.animation.AnimationSet
import android.view.animation.DecelerateInterpolator
import android.view.animation.TranslateAnimation
import org.jetbrains.anko.doAsync

/**
 * @Author : wenke
 * @Time : 2022/1/18 0018
 * @Description : CarControl
 */
class AnimationControl {
    //动画持续时间
    private val durationTime=1000L
    //位移大小
    private val displacement=100f
    /**
     * [module]1向下 2向上 3向左 4向右
    * */
    fun startAnimation(view: View,module:Int?=1){
        var fromYDelta=0f
        var fromXDelta=0f
        when(module){
            1->{
                fromYDelta=-displacement
                fromXDelta=0f
            }
            2->{
                fromYDelta=displacement
                fromXDelta=0f
            }
            3->{
                fromYDelta=0f
                fromXDelta=displacement
            }
            4->{
                fromYDelta=0f
                fromXDelta=-displacement
            }
        }
        //不使用动画
        if(fromYDelta==0f&&fromXDelta==0f){
            return
        }
        doAsync {
            val animationSet = AnimationSet(true)
//        animationSet.duration = durationTime
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
//      设置动画为先加速在减速(开始速度最快 逐渐减慢)：
//        animationSet.interpolator = AccelerateDecelerateInterpolator()
//        设置动画为减速动画(动画播放中越来越慢)
            animationSet.interpolator = DecelerateInterpolator()
            view.startAnimation(animationSet)
        }
    }
}