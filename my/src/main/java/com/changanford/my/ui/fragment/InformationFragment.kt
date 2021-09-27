package com.changanford.my.ui.fragment

import android.os.Bundle
import com.changanford.common.basic.EmptyViewModel
import com.changanford.common.manger.RouterManger
import com.changanford.my.BaseMineFM
import com.changanford.my.databinding.FragmentCollectBinding

/**
 *  文件名：InformationFragment
 *  创建者: zcy
 *  创建日期：2021/9/26 18:12
 *  描述: TODO
 *  修改描述：TODO
 */
class InformationFragment : BaseMineFM<FragmentCollectBinding, EmptyViewModel>() {

    companion object {
        fun newInstance(value: String): InformationFragment {
            var bundle: Bundle = Bundle()
            bundle.putString(RouterManger.KEY_TO_OBJ, value)
            var medalFragment = InformationFragment()
            medalFragment.arguments = bundle
            return medalFragment
        }
    }

    override fun initView() {

    }
}