package com.changanford.my.ui

import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.view.View
import androidx.lifecycle.Observer
import com.alibaba.android.arouter.facade.annotation.Route
import com.changanford.common.bean.CarItemBean
import com.changanford.common.manger.RouterManger
import com.changanford.common.net.onSuccess
import com.changanford.common.net.onWithMsgFailure
import com.changanford.common.router.path.ARouterMyPath
import com.changanford.common.util.TimeUtils
import com.changanford.common.util.bus.LiveDataBus
import com.changanford.common.util.bus.LiveDataBusKey
import com.changanford.common.utilext.load
import com.changanford.my.BaseMineUI
import com.changanford.my.R
import com.changanford.my.databinding.UiLoveCarInfoBinding
import com.changanford.my.viewmodel.CarAuthViewModel

/**
 *  文件名：LoveCarInfoUI
 *  创建者: zcy
 *  创建日期：2021/9/22 17:40
 *  描述: TODO
 *  修改描述：TODO
 */
@Route(path = ARouterMyPath.MineLoveCarInfoUI)
class LoveCarInfoUI : BaseMineUI<UiLoveCarInfoBinding, CarAuthViewModel>() {

    lateinit var auth: CarItemBean

    override fun initView() {
        binding.carToolbar.toolbarTitle.text = "我的爱车"
        var d = binding.tvAuth.background as GradientDrawable
        d.setColor(Color.parseColor("#6900095B"))

        auth = CarItemBean()
        intent.extras?.getSerializable(RouterManger.KEY_TO_OBJ)?.let {
            auth = it as CarItemBean
        }

        viewModel.carAuthQY() {
            it.onSuccess {
                it?.let {
                    binding.group.visibility =
                        if (it.authDetailRightsIsShow) View.VISIBLE else View.GONE
                    binding.carContent.text = it.authDetailRightsContent
                }
            }
        }

        LiveDataBus.get().with(LiveDataBusKey.MINE_CAR_CARD_NUM, String::class.java)
            .observe(this, Observer {
                initData()
            })
    }

    override fun initData() {
        super.initData()
        auth?.let {
            viewModel.queryAuthCarDetail(auth.vin) {
                it.onSuccess {
                    it?.let {
                        auth = it
                    }
                    setAuthInfo()
                }
                it.onWithMsgFailure {
                    it?.let {
                        showToast(it)
                    }
                    setAuthInfo()
                }
            }
        }
    }

    private fun setAuthInfo() {
        binding.cardVin.text = "${auth.vin ?: ""}"
        binding.cardModel.text = "${auth.modelName ?: ""}"
        binding.cardTime.text = "${TimeUtils.MillisToDayStr(auth.saleDate) ?: 0L}"
        binding.cardDealer.text = "${auth.dealerName ?: ""}"
        binding.cardDealerPhone.text = "${auth.dealerPhone ?: ""}"
        binding.carPic.load(auth.modelUrl, R.mipmap.ic_car_auth_ex)
        setCarNum()
    }

    private fun setCarNum() {
        if (auth.plateNum?.isNullOrEmpty()) {
            binding.btnAddCarNum.apply {
                visibility = View.VISIBLE
                setOnClickListener(editPlateNum)
            }
        } else {
            binding.btnAddCarNum.apply {
                visibility = View.GONE
                setOnClickListener(null)
            }
            binding.cardNum.setOnClickListener(editPlateNum)
            binding.cardNum.text = "${auth.plateNum ?: ""}"
        }
    }

    private var editPlateNum = View.OnClickListener {
        auth?.let {
            RouterManger.param("value", auth.vin)
                .param("plateNum", auth.plateNum ?: "")
                .startARouter(ARouterMyPath.AddCardNumTransparentUI)
        }
    }
}