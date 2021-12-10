package com.changanford.my.ui

import android.view.View
import androidx.lifecycle.Observer
import com.alibaba.android.arouter.facade.annotation.Route
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseDataBindingHolder
import com.changanford.common.bean.CarItemBean
import com.changanford.common.manger.RouterManger
import com.changanford.common.net.onSuccess
import com.changanford.common.router.path.ARouterMyPath
import com.changanford.common.util.AuthCarStatus
import com.changanford.common.util.JumpUtils
import com.changanford.common.util.MConstant
import com.changanford.common.util.bus.LiveDataBus
import com.changanford.common.util.bus.LiveDataBusKey
import com.changanford.common.utilext.logE
import com.changanford.my.BaseMineUI
import com.changanford.my.R
import com.changanford.my.adapter.CarAuthHolder
import com.changanford.my.databinding.ItemCarAuthBinding
import com.changanford.my.databinding.UiCarCrmAuthBinding
import com.changanford.my.databinding.ViewHeadCarAuthBinding
import com.changanford.my.viewmodel.CarAuthViewModel
import com.scwang.smart.refresh.layout.SmartRefreshLayout

/**
 *  文件名：CarCrmAuthUI
 *  创建者: zcy
 *  创建日期：2021/9/15 9:43
 *  描述: TODO
 *  修改描述：TODO
 */
@Route(path = ARouterMyPath.MineLoveCarListUI)
class CarCrmAuthUI : BaseMineUI<UiCarCrmAuthBinding, CarAuthViewModel>() {

    var isRefresh: Boolean = false

    private var isCarOwner: Int = 0
    val headView: ViewHeadCarAuthBinding by lazy {
        ViewHeadCarAuthBinding.inflate(layoutInflater)
    }
    val carAdapter: AuthCarAdapter by lazy {
        AuthCarAdapter()
    }

    override fun initView() {
        binding.carToolbar.toolbarTitle.text = "我的爱车"

        headView.look.setOnClickListener {
            JumpUtils.instans?.jump(1, MConstant.H5_CAR_QY)
        }
        carAdapter.addHeaderView(headView.root)
        binding.rcyCarAuth.rcyCommonView.adapter = carAdapter

        viewModel.carAuth.observe(this, Observer {
            it?.let {
                isCarOwner = it.isCarOwner
            }
            completeRefresh(it?.carList, carAdapter, 0)
        })

        binding.btnAddCar.setOnClickListener {
            RouterManger.startARouter(ARouterMyPath.UniCarAuthUI)
        }

        LiveDataBus.get().with(LiveDataBusKey.MINE_CAR_CARD_NUM, String::class.java)
            .observe(this, Observer {
                "输入车牌--$it".logE()
            })
        LiveDataBus.get().with(LiveDataBusKey.MINE_ADD_CAR_SUCCESS).observe(this, Observer {
        })

    }

    override fun onPause() {
        super.onPause()
        isRefresh = true
    }

    override fun onResume() {
        super.onResume()
        if (isRefresh) {
            initRefreshData(1)
            isRefresh = false
        }
    }

    override fun initRefreshData(pageSize: Int) {
        super.initRefreshData(pageSize)
        viewModel.queryAuthCarAndIncallList(AuthCarStatus.ALL)

        viewModel.carAuthQY {
            it.onSuccess {
                it?.let {
                    headView.layout.visibility =
                        if (it.carListRightsIsShow) View.VISIBLE else View.GONE
                    headView.content.text = when (isCarOwner) {
                        1 -> {
                            it.carListRightsContentY
                        }
                        else -> {
                            it.carListRightsContentN
                        }
                    }
                }
            }
        }
    }

    override fun bindSmartLayout(): SmartRefreshLayout? {
        return binding.rcyCarAuth.smartCommonLayout
    }

    inner class AuthCarAdapter :
        BaseQuickAdapter<CarItemBean, BaseDataBindingHolder<ItemCarAuthBinding>>(
            R.layout.item_car_auth
        ) {
        override fun convert(holder: BaseDataBindingHolder<ItemCarAuthBinding>, item: CarItemBean) {
            CarAuthHolder(holder, item)
        }
    }
}