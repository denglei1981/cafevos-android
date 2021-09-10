package com.changanford.my

import androidx.lifecycle.lifecycleScope
import com.changanford.common.basic.BaseFragment
import com.changanford.common.basic.EmptyViewModel
import com.changanford.common.databinding.AboutFragmentBinding
import com.changanford.common.manger.RouterManger
import com.changanford.common.router.path.ARouterMyPath

class AboutFragment : BaseFragment<AboutFragmentBinding, EmptyViewModel>() {

    override fun initView() {
        binding.hello.text = "Arouter和MotionLayout"
        binding.hello.setOnClickListener {
            RouterManger.param(RouterManger.KEY_TO_OBJ, "我是传过来的数据")
                .startARouter(ARouterMyPath.SignUI)
        }
    }

    override fun initData() {
        lifecycleScope.launchWhenStarted {

        }
    }

}