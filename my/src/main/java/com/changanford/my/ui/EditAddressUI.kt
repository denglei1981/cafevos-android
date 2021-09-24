package com.changanford.my.ui

import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import com.alibaba.android.arouter.facade.annotation.Route
import com.changanford.common.router.path.ARouterMyPath
import com.changanford.my.BaseMineUI
import com.changanford.my.databinding.UiEditAddressBinding
import com.changanford.my.viewmodel.SignViewModel
import kotlinx.coroutines.launch

/**
 *  文件名：EditAddressUI
 *  创建者: zcy
 *  创建日期：2021/9/24 10:23
 *  描述: TODO
 *  修改描述：TODO
 */

@Route(path = ARouterMyPath.EditAddressUI)
class EditAddressUI : BaseMineUI<UiEditAddressBinding, SignViewModel>() {

    override fun initView() {
        viewModel.allCity.observe(this, Observer {

        })
    }

    override fun initData() {
        lifecycleScope.launch {
            viewModel.getAllCity()
        }
    }
}