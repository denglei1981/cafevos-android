package com.changanford.car.ui.fragment

import android.os.Handler
import android.os.Looper
import androidx.core.view.isVisible
import com.changanford.car.CarViewModel
import com.changanford.car.adapter.NewCarTopBannerAdapter
import com.changanford.car.databinding.CarFragmentTopBinding
import com.changanford.common.basic.BaseFragment
import com.changanford.common.bean.NewCarBannerBean
import com.changanford.common.util.FastClickUtils
import com.changanford.common.util.JumpUtils
import com.changanford.common.util.MConstant
import com.changanford.common.util.bus.LiveDataBus
import com.changanford.common.util.gio.GIOUtils
import com.changanford.common.util.gio.GioPageConstant
import com.changanford.common.utilext.toIntPx
import com.changanford.common.wutil.wLogE
import com.dueeeke.videoplayer.player.VideoView

/**
 *Author lcw
 *Time on 2024/1/22
 *Purpose
 */
class CarTopFragment : BaseFragment<CarFragmentTopBinding, CarViewModel>() {

    var carTopBinding: CarFragmentTopBinding? = null

    override fun initView() {
        carTopBinding = binding
        LiveDataBus.get().with("carTop").postValue(binding)
//        initBanner()
    }

    override fun initData() {
//        viewModel.getTopBanner()
    }


}