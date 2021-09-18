package com.changanford.my.ui

import androidx.lifecycle.lifecycleScope
import com.alibaba.android.arouter.facade.annotation.Route
import com.changanford.common.router.path.ARouterMyPath
import com.changanford.common.util.AuthCarStatus
import com.changanford.my.BaseMineUI
import com.changanford.my.databinding.UiCarCrmAuthBinding
import com.changanford.my.viewmodel.CarViewModel
import kotlinx.coroutines.launch

/**
 *  文件名：CarCrmAuthUI
 *  创建者: zcy
 *  创建日期：2021/9/15 9:43
 *  描述: TODO
 *  修改描述：TODO
 */
@Route(path = ARouterMyPath.MineLoveCarListUI)
class CarCrmAuthUI : BaseMineUI<UiCarCrmAuthBinding, CarViewModel>() {

    override fun initView() {


    }

    override fun initRefreshData(pageSize: Int) {
        super.initRefreshData(pageSize)
        lifecycleScope.launch {
            viewModel.queryAuthCarAndIncallList(AuthCarStatus.ALL)
        }
    }

//    override fun bindSmartLayout(): SmartRefreshLayout? {
//        return binding.rcyAuth.smartCommonLayout
//    }
}