package com.changanford.common.util

import android.view.View
import android.view.animation.AlphaAnimation
import android.view.animation.Animation
import android.view.animation.AnimationSet
import android.view.animation.ScaleAnimation
import android.view.animation.TranslateAnimation




/**
 *Author lcw
 *Time on 2021/10/9
 *Purpose
 */
object AnimScaleInUtil {
    fun animScaleIn(view: View?) {
        //缩放动画
        val animation = ScaleAnimation(
            1f,
            0.8f,
            1f,
            0.8f,
            Animation.RELATIVE_TO_SELF,
            0.5f,
            Animation.RELATIVE_TO_SELF,
            0.5f
        )
        animation.duration = 200
        animation.fillAfter = true
        animation.repeatMode = Animation.REVERSE
        animation.repeatCount = 1

        //透明度动画
        val animation1 = AlphaAnimation(1f, 0.9f)
        animation1.duration = 200
        animation1.repeatCount = 1
        animation1.repeatMode = Animation.REVERSE
        animation1.fillAfter = true
        //装入AnimationSet中
        val set = AnimationSet(true)

        set.addAnimation(animation)
        set.addAnimation(animation1)
        view?.startAnimation(set)
    }


   fun animMove(sTouchView:View?){
//       val touchPivotX: Float = sTouchView.getLeft().toFloat()
//       val touchPivotY: Float = sTouchView.getTop().toFloat()
//       val indexPivotX: Float = sTargetView.getLeft().toFloat()
//       val indexPivotY: Float = sTargetView.getTop().toFloat()
//
//       val translateAnimation =
//           TranslateAnimation(-touchPivotX + indexPivotX, 0, indexPivotY - touchPivotY, 0)
//       translateAnimation.repeatCount = Animation.INFINITE
//       translateAnimation.repeatMode = Animation.REVERSE
//       view?.startAnimation(translateAnimation)
   }


}