package com.changanford.my.ui

import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import com.alibaba.android.arouter.facade.annotation.Route
import com.changanford.common.bean.CarItemBean
import com.changanford.common.buried.BuriedUtil
import com.changanford.common.manger.RouterManger
import com.changanford.common.net.onFailure
import com.changanford.common.net.onSuccess
import com.changanford.common.net.onWithMsgFailure
import com.changanford.common.router.path.ARouterMyPath
import com.changanford.common.util.TimeUtils
import com.changanford.common.util.bus.LiveDataBus
import com.changanford.common.util.bus.LiveDataBusKey
import com.changanford.common.utilext.load
import com.changanford.common.utilext.toast
import com.changanford.my.BaseMineUI
import com.changanford.my.R
import com.changanford.my.databinding.UiLoveCarInfoBinding
import com.changanford.my.viewmodel.CarAuthViewModel
import com.changanford.my.widget.DeleteCarPop

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

    var removeCarNotice: String? = ""
    override fun initView() {
        binding.carToolbar.toolbarTitle.text = "我的爱车"



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
                    binding.deleteCar.text = it.removeCarNotice
                    removeCarNotice = it.removeCarNotice
                }
            }
        }

        binding.tvAuth.setOnClickListener {
            // 设置为默认车辆
            if (auth.isDefault == 0) {
                viewModel.setDefalutCar(auth.carSalesInfoId) {
                    it.onSuccess {
                        auth.isDefault = 1
                        setDefalut()
                        "设置成功".toast()
                    }.onFailure {
                        it?.toast()
                    }
                }
            } else {
                "已经是默认车辆".toast()
            }
        }

        LiveDataBus.get().with(LiveDataBusKey.MINE_CAR_CARD_NUM, String::class.java)
            .observe(this, Observer {
                initData()
            })


        binding.deleteCar.setOnClickListener {
            if (!TextUtils.isEmpty(auth.vin)) {

                deleteCar()
            }
        }
    }

    fun deleteCar() {
        removeCarNotice?.let { tips ->
            val deleteCarPop = DeleteCarPop(this, object : DeleteCarPop.deleteCar {
                override fun cancle() {

                }

                override fun delete() {
                    viewModel.deleteCar(auth.vin,id=auth.authId) {
                        it.onSuccess {
                            try {
                                LiveDataBus.get().with(LiveDataBusKey.REMOVE_CAR).postValue(true)
                                BuriedUtil.instant?.carDelete(auth.phone)
                            }catch (e:Exception){
                                e.printStackTrace()
                            }
                            finish()

                        }.onWithMsgFailure {
                            it?.toast()
                        }

                    }

                }
            }, tips)
            deleteCarPop.showPopupWindow()
        }


    }

    override fun initData() {
        super.initData()
        auth.let {
            viewModel.queryAuthCarDetail(auth.vin,auth.authId) {
                it.onSuccess {
                    it?.let {
                        auth = it
                    }
                    setAuthInfo()
                    setDefalut()
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

        if(auth.saleDate==null||auth.saleDate==0L){
            binding.cardTime.text = "--"
        }else{
            binding.cardTime.text = "${TimeUtils.MillisToDayStr(auth.saleDate) ?: 0L}"
        }





        binding.cardDealer.text = "${auth.dealerName ?: ""}"
        binding.cardDealerPhone.text = "${auth.dealerPhone ?: ""}"
        binding.carPic.load(auth.modelUrl, R.mipmap.ic_car_auth_ex)

        setCarNum()
    }

    private fun setDefalut() {
        var d = binding.tvAuth.background as GradientDrawable
        if (auth.isDefault == 0) {
            binding.tvAuth.text = "设为默认"
            d.setColor(Color.parseColor("#691700f4"))
        } else {
            binding.tvAuth.text = "默认"
            d.setColor(Color.parseColor("#b31700f4"))
            binding.tvAuth.setTextColor(ContextCompat.getColor(this, R.color.white))
        }

    }

    private fun setCarNum() {
        if (TextUtils.isEmpty(auth.plateNum)) {
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
            RouterManger.param("value", auth.carSalesInfoId)
                .param("plateNum", auth.plateNum ?: "")
                .param("authId",auth.authId)
                .startARouter(ARouterMyPath.AddCardNumTransparentUI)
        }
    }
}