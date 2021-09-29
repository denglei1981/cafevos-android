package com.changanford.my

import android.os.Bundle
import com.changanford.common.manger.RouterManger
import com.changanford.my.databinding.FragmentCollectBinding
import com.changanford.my.viewmodel.ActViewModel
import com.scwang.smart.refresh.layout.SmartRefreshLayout

/**
 *  文件名：CollectFragment
 *  创建者: zcy
 *  创建日期：2021/9/26 17:03
 *  描述: TODO
 *  修改描述：TODO
 */

class ActFragment : BaseMineFM<FragmentCollectBinding, ActViewModel>() {
    var type: String = ""

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
        arguments?.getString(RouterManger.KEY_TO_OBJ)?.let {
            type = it
        }
    }

    override fun bindSmartLayout(): SmartRefreshLayout? {
        return binding.rcyCollect.smartCommonLayout
    }

    override fun initRefreshData(pageSize: Int) {
        super.initRefreshData(pageSize)
        var total: Int = 0

        when (type) {
            "collectAct" -> {
                viewModel.queryMineCollectAc(pageSize) { reponse ->
                    reponse?.data?.total?.let {
                        total = it
                    }
//                    completeRefresh()
                }
            }
            "footAct" -> {
                viewModel.queryMineFootAc(pageSize) { reponse ->
                    reponse?.data?.total?.let {
                        total = it
                    }
                }
            }
        }
    }
}