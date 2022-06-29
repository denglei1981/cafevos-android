package com.changanford.my.ui

import com.alibaba.android.arouter.facade.annotation.Route
import com.alibaba.fastjson.JSON
import com.changanford.common.bean.CarItemBean
import com.changanford.common.manger.RouterManger
import com.changanford.common.router.path.ARouterMyPath
import com.changanford.common.util.JumpUtils
import com.changanford.common.util.bus.LiveDataBus
import com.changanford.common.util.bus.LiveDataBusKey
import com.changanford.my.BaseMineUI
import com.changanford.my.databinding.UiCarAuthSuccessBinding
import com.changanford.my.viewmodel.SignViewModel

/**
 *  文件名：CarAuthSuccessUI
 *  创建者: zcy
 *  创建日期：2021/11/22 9:44
 *  描述: TODO
 *  修改描述：TODO
 */
@Route(path = ARouterMyPath.CarAuthSuccessUI)
class CarAuthSuccessUI : BaseMineUI<UiCarAuthSuccessBinding, SignViewModel>() {


    var carItemBean: CarItemBean? = null

    override fun initView() {

        intent?.getSerializableExtra(RouterManger.KEY_TO_ITEM)?.let {
            carItemBean = it as CarItemBean
        }

        binding.btnFinish.setOnClickListener {
            JumpUtils.instans?.jump(103)
            back()
        }

        binding.btnLookInfo.setOnClickListener {
            //审核状态 1:待审核 2：换绑审核中 3:认证成功(审核通过) 4:审核失败(审核未通过) 5:解绑
            carItemBean?.let {
                JumpUtils.instans?.jump(
                    50,
                    JSON.toJSONString(
                        mapOf(
                            "vin" to it.vin,
                            "status" to it.authStatus,
                            "isNeedChangeBind" to it.isNeedChangeBind,
                            "carSalesInfoId" to it.carSalesInfoId
                        )
                    )
                )
            }
            back()
        }
    }

    override fun back() {
        LiveDataBus.get().with(LiveDataBusKey.MINE_ADD_CAR_SUCCESS).postValue(true)
        super.back()
    }
}