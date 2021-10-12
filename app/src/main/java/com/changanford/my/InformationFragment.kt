package com.changanford.my

import android.os.Bundle
import com.changanford.common.manger.RouterManger
import com.changanford.common.manger.UserManger
import com.changanford.home.news.adapter.NewsListAdapter
import com.changanford.my.databinding.FragmentActBinding
import com.changanford.my.viewmodel.ActViewModel
import com.scwang.smart.refresh.layout.SmartRefreshLayout

/**
 *  文件名：InformationFragment
 *  创建者: zcy
 *  创建日期：2021/9/26 18:12
 *  描述: TODO
 *  修改描述：TODO
 */
class InformationFragment : BaseMineFM<FragmentActBinding, ActViewModel>() {
    var type: String = ""
    var userId: String = ""

    val infoAdapter: NewsListAdapter by lazy {
        NewsListAdapter(this)
    }

    companion object {
        fun newInstance(value: String, userId: String = ""): InformationFragment {
            var bundle: Bundle = Bundle()
            bundle.putString(RouterManger.KEY_TO_OBJ, value)
            bundle.putString(RouterManger.KEY_TO_ID, userId)
            var medalFragment = InformationFragment()
            medalFragment.arguments = bundle
            return medalFragment
        }
    }

    override fun initView() {
        arguments?.getString(RouterManger.KEY_TO_OBJ)?.let {
            type = it
        }
        userId = UserManger.getSysUserInfo().uid
        arguments?.getString(RouterManger.KEY_TO_ID)?.let {
            userId = it
        }
        binding.rcyAct.rcyCommonView.adapter = infoAdapter
    }

    override fun bindSmartLayout(): SmartRefreshLayout? {
        return binding.rcyAct.smartCommonLayout
    }

    override fun initRefreshData(pageSize: Int) {
        super.initRefreshData(pageSize)
        var total: Int = 0

        when (type) {
            "collectInformation" -> {
                viewModel.queryMineCollectInfo(pageSize) { reponse ->
                    reponse?.data?.total?.let {
                        total = it
                    }
                    completeRefresh(reponse?.data?.dataList, infoAdapter, total)
                }
            }
            "footInformation" -> {
                viewModel.queryMineFootInfo(pageSize) { reponse ->
                    reponse?.data?.total?.let {
                        total = it
                    }
                    completeRefresh(reponse?.data?.dataList, infoAdapter, total)
                }
            }
            "centerInformation" -> {
                viewModel.queryMineSendInfoList(userId, pageSize) { reponse ->
                    reponse?.data?.total?.let {
                        total = it
                    }
                    completeRefresh(reponse?.data?.dataList, infoAdapter, total)
                }
            }
        }
    }
}