package com.changanford.my.ui

import com.alibaba.android.arouter.facade.annotation.Route
import com.changanford.common.basic.EmptyViewModel
import com.changanford.common.bean.MedalListBeanItem
import com.changanford.common.manger.RouterManger
import com.changanford.common.router.path.ARouterMyPath
import com.changanford.common.utilext.load
import com.changanford.my.BaseMineUI
import com.changanford.my.databinding.UiMedalDetailBinding

/**
 *  文件名：MedalDetailUI
 *  创建者: zcy
 *  创建日期：2021/9/14 16:52
 *  描述: TODO
 *  修改描述：TODO
 */
@Route(path = ARouterMyPath.MedalDetailUI)
class MedalDetailUI : BaseMineUI<UiMedalDetailBinding, EmptyViewModel>() {

    override fun initView() {
        intent?.extras?.getSerializable(RouterManger.KEY_TO_OBJ)?.let {
            var medal = it as MedalListBeanItem
            binding.imMedalIcon.load(medal.medalImage)
            binding.tvMedalName.text = medal.medalName
            binding.tvCon.text = medal.fillCondition
        }
    }
}