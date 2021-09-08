package com.changanford.evos

import android.os.Build
import android.os.SystemClock
import android.view.View
import androidx.lifecycle.lifecycleScope
import com.changanford.common.basic.BaseFragment
import com.changanford.common.util.MConstant
import com.changanford.common.util.room.Db
import com.changanford.evos.databinding.FragmentSplashBinding
import kotlinx.coroutines.launch
import kotlin.math.ceil

class SplashFragment : BaseFragment<FragmentSplashBinding, SplashViewModel>() {
    override fun initView() {
        binding.chronometer.setOnChronometerTickListener {
            binding.counter.visibility = View.VISIBLE
            binding.counter.text =
                "跳过 ${ceil(((ceil((it.base - SystemClock.elapsedRealtime()).toDouble())) / 1000)).toInt()}"
            if (it.base <= SystemClock.elapsedRealtime()) {
                binding.counter.text = "跳过 ${0}"
                it.stop()
                navFinishActivityTo(R.id.action_splashFragment_to_mainActivity)
            }
        }
        binding.counter.setOnClickListener {
            navFinishActivityTo(R.id.action_splashFragment_to_mainActivity)
        }
    }

    override fun initData() {
        lifecycleScope.launch {
            MConstant.pubKey = Db.myDb.getData("pubKey")?.storeValue ?: ""
            MConstant.imgcdn = Db.myDb.getData("imgCdn")?.storeValue ?: ""
            if (MConstant.pubKey.isNullOrEmpty()) {
                viewModel.getKey()
            }
            showCounter()
        }
        viewModel.key.observe(this) {
            if (it.isNullOrEmpty()) {
                lifecycleScope.launch {
                    viewModel.getKey()
                }
            } else {
                MConstant.pubKey = it
                lifecycleScope.launch {
                    Db.myDb.saveData("pubKey", it)
                    viewModel.getConfig()
                }
            }
        }
    }

    private fun showCounter() {
        binding.chronometer.apply {
            base = SystemClock.elapsedRealtime() + 5 * 1000
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                isCountDown = true
            }
        }.start()
    }
}