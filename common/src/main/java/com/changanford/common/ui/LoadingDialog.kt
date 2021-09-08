package com.changanford.common.ui

import android.content.Context
import androidx.constraintlayout.motion.widget.MotionLayout
import com.changanford.common.R
import com.changanford.common.basic.BaseDialog
import com.changanford.common.databinding.DialogLoadingBinding

/**********************************************************************************
 * @Copyright (C), 2020-2021.
 * @FileName: com.changanford.common.ui.LoadingDialog
 * @Author:　 　
 * @Version : V1.0
 * @Date: 2021/7/2 17:54
 * @Description: 　
 * *********************************************************************************
 */
class LoadingDialog(context: Context) : BaseDialog<DialogLoadingBinding>(context) {

    override fun initView() {
        binding.loadingM.transitionToState(R.id.end)
        binding.loadingM.addTransitionListener(object : MotionLayout.TransitionListener {
            override fun onTransitionStarted(p0: MotionLayout?, p1: Int, p2: Int) {
            }

            override fun onTransitionChange(p0: MotionLayout?, p1: Int, p2: Int, p3: Float) {
            }

            override fun onTransitionCompleted(p0: MotionLayout?, p1: Int) {
                if (p0?.currentState == R.id.end) {
                    binding.loadingM.transitionToState(R.id.start)
                } else {
                    binding.loadingM.transitionToState(R.id.end)
                }
            }

            override fun onTransitionTrigger(p0: MotionLayout?, p1: Int, p2: Boolean, p3: Float) {
            }

        })
    }

    override fun initData() {
    }
}