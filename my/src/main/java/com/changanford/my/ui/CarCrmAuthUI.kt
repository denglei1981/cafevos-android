package com.changanford.my.ui

import androidx.lifecycle.Observer
import com.alibaba.android.arouter.facade.annotation.Route
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseDataBindingHolder
import com.changanford.common.bean.CarItemBean
import com.changanford.common.manger.RouterManger
import com.changanford.common.router.path.ARouterMyPath
import com.changanford.common.util.AuthCarStatus
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

    val carAdapter: AuthCarAdapter by lazy {
        AuthCarAdapter()
    }

    override fun initView() {
        binding.carToolbar.toolbarTitle.text = "我的爱车"

        var headView: ViewHeadCarAuthBinding = ViewHeadCarAuthBinding.inflate(layoutInflater)
        carAdapter.addHeaderView(headView.root)
        binding.rcyCarAuth.rcyCommonView.adapter = carAdapter

        viewModel.carAuth.observe(this, Observer {
            completeRefresh(it, carAdapter, 0)
        })

        binding.btnAddCar.setOnClickListener {
            RouterManger.startARouter(ARouterMyPath.UniCarAuthUI)
        }
    }

    override fun initRefreshData(pageSize: Int) {
        super.initRefreshData(pageSize)
        viewModel.queryAuthCarAndIncallList(AuthCarStatus.ALL)
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