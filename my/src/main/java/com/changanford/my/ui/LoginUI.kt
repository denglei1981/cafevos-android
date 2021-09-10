package com.changanford.my.ui

import com.alibaba.android.arouter.facade.annotation.Route
import com.changanford.common.basic.BaseActivity
import com.changanford.common.basic.EmptyViewModel
import com.changanford.common.router.path.ARouterMyPath
import com.changanford.my.BaseMineUI
import com.changanford.my.databinding.UiLoginBinding
import com.changanford.my.viewmodel.SignViewModel

/**
 *  文件名：LoginUI
 *  创建者: zcy
 *  创建日期：2021/9/9 10:01
 *  描述: TODO
 *  修改描述：TODO
 */
@Route(path = ARouterMyPath.SignUI)
class LoginUI : BaseMineUI<UiLoginBinding, SignViewModel>() {

    override fun initView() {


    }

    override fun initData() {

    }
}