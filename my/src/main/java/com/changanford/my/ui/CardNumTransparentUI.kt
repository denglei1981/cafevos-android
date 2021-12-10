package com.changanford.my.ui

import androidx.lifecycle.lifecycleScope
import com.alibaba.android.arouter.facade.annotation.Route
import com.changanford.common.net.*
import com.changanford.common.router.path.ARouterMyPath
import com.changanford.common.util.bus.LiveDataBus
import com.changanford.common.util.bus.LiveDataBusKey.MINE_CAR_CARD_NUM
import com.changanford.my.BaseMineUI
import com.changanford.my.databinding.UiAddCardNumTransparentV2Binding
import com.changanford.my.viewmodel.SignViewModel
import com.eaves.plate.InputListener
import kotlinx.coroutines.launch

/**
 * 添加车牌 自定义键盘
 *
 * @author zcy
 *
 */
@Route(path = ARouterMyPath.AddCardNumTransparentUI)
class CardNumTransparentUI : BaseMineUI<UiAddCardNumTransparentV2Binding, SignViewModel>(),
    InputListener {
    private var inputContent: String = ""


    override fun initView() {
        var bundle = intent.extras

        binding.activityLpv.apply {
            setInputListener(this@CardNumTransparentUI)
            setKeyboardContainerLayout(binding.mainRlContainer)
            showLastView()
        }

        var plateNum = bundle?.getString("plateNum")
        if (!plateNum.isNullOrEmpty()) {
            inputContent = plateNum
            binding.activityLpv.initPlateInputText(plateNum)
        }

        binding.cancel.setOnClickListener {
            finish()
        }

        binding.submit.setOnClickListener {
            if (inputContent.isNullOrEmpty()) {
                showToast("请输入正确的车牌")
                return@setOnClickListener
            }
            if (bundle?.getString("value").isNullOrEmpty()) {//没有传认证车辆id 回传参数
                LiveDataBus.get().with(MINE_CAR_CARD_NUM, String::class.java)
                    .postValue(inputContent)
                finish()
            } else {
                bundle?.getString("value")?.let {
                    if (bundle?.getBoolean("isUni", false)) {
                        addCarUni(it, inputContent)
                    } else {
                        addCar(it, inputContent)
                    }
                }
            }
        }
    }

    /**
     * 车主认证添加/修改车牌
     */
    fun addCar(authCarId: String, cardNum: String) {

        lifecycleScope.launch {
            fetchRequest {
                var body = HashMap<String, String>()
                body["vin"] = authCarId
                body["plateNum"] = cardNum
                var rkey = getRandomKey()
                apiService.addCarCardNum(body.header(rkey), body.body(rkey))
            }.onSuccess {
                LiveDataBus.get().with(MINE_CAR_CARD_NUM, String::class.java)
                    .postValue(inputContent)
                finish()
            }.onWithMsgFailure {
                it?.let {
                    showToast(it)
                }
            }
        }
    }

    /**
     * U享卡添加车牌
     */
    fun addCarUni(authCarId: String, cardNum: String) {
        lifecycleScope.launch {
            fetchRequest {
                var body = HashMap<String, String>()
                body["orderId"] = authCarId
                body["plateNum"] = cardNum
                var rkey = getRandomKey()
                apiService.addCarCardNumUni(body.header(rkey), body.body(rkey))
            }
        }
        //ToastUtils.showLong("车牌添加成功")
        //                    LiveDataBus.get().with(MINE_CAR_CARD_NUM, String::class.java)
        //                        .postValue(inputContent)
        //                    finish()

    }

    override fun initData() {

    }

    override fun inputComplete(p0: String?) {
        p0?.let {
            inputContent = it
        }
    }

    override fun deleteContent() {
        inputContent = ""
    }
}