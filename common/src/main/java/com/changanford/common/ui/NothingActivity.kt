package com.changanford.common.ui

import android.annotation.SuppressLint
import android.os.Bundle
import android.os.CountDownTimer
import android.widget.TextView
import androidx.lifecycle.lifecycleScope
import com.alibaba.android.arouter.facade.annotation.Route
import com.changanford.common.R
import com.changanford.common.basic.BaseActivity
import com.changanford.common.basic.EmptyViewModel
import com.changanford.common.databinding.ActivityNothingBinding
import com.changanford.common.router.path.ARouterCarControlPath
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 * @Author: lcw
 * @Date: 2020/8/11
 * @Des:
 */
@Route(path = ARouterCarControlPath.NothingActivity)
class NothingActivity : BaseActivity<ActivityNothingBinding, EmptyViewModel>() {



    override fun initView() {
        countDownTimer(binding.tvDownNum).start()
        binding.leftImg.setOnClickListener { finish() }
        overridePendingTransition(0, 0)
    }

    override fun initData() {

    }

    /**
     * 倒计时
     */
    private fun countDownTimer(dateBtn: TextView, t: Long = 4 * 1000): CountDownTimer {
        return object : CountDownTimer(t, 1000) {
            override fun onFinish() {
            }

            @SuppressLint("SetTextI18n")
            override fun onTick(millisUntilFinished: Long) {
                val second = millisUntilFinished / 1000 % 60
                if (second.toInt() == 1) {
                    finishActivity()
                }
                if (second.toInt() > 0) {
                    dateBtn.text = "${second}s"
                }
            }
        }
    }

    private fun finishActivity() {
        lifecycleScope.launch {
            delay(700)
            finish()
        }
    }
}