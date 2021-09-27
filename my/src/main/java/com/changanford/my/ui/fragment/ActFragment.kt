package com.changanford.my.ui.fragment

import android.os.Bundle
import com.changanford.common.basic.EmptyViewModel
import com.changanford.common.manger.RouterManger
import com.changanford.my.BaseMineFM
import com.changanford.my.databinding.FragmentCollectBinding
import com.scwang.smart.refresh.layout.SmartRefreshLayout

/**
 *  文件名：CollectFragment
 *  创建者: zcy
 *  创建日期：2021/9/26 17:03
 *  描述: TODO
 *  修改描述：TODO
 */

class ActFragment : BaseMineFM<FragmentCollectBinding, EmptyViewModel>() {

    companion object {
        fun newInstance(value: String): ActFragment {
            var bundle: Bundle = Bundle()
            bundle.putString(RouterManger.KEY_TO_OBJ, value)
            var medalFragment = ActFragment()
            medalFragment.arguments = bundle
            return medalFragment
        }
    }

    override fun initView() {

    }

    override fun bindSmartLayout(): SmartRefreshLayout? {
        return binding.rcyCollect.smartCommonLayout
    }
}