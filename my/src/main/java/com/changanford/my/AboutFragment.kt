package com.changanford.my

import androidx.lifecycle.lifecycleScope
import com.changanford.common.basic.BaseFragment
import com.changanford.common.basic.EmptyViewModel
import com.changanford.common.databinding.AboutFragmentBinding
import com.changanford.common.router.path.ARouterMyPath
import com.changanford.common.router.startARouter

class AboutFragment : BaseFragment<AboutFragmentBinding, EmptyViewModel>() {

    override fun initView() {
        binding.hello.text = "Arouter和MotionLayout"
        binding.hello.setOnClickListener {
            startARouter(ARouterMyPath.AddCardNumUI)
        }
    }

    override fun initData() {
        lifecycleScope.launchWhenStarted {

        }
    }

}