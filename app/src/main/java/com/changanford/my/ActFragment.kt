package com.changanford.my

import android.os.Bundle
import androidx.databinding.DataBindingUtil
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.changanford.common.bean.ActDataBean
import com.changanford.common.manger.RouterManger
import com.changanford.common.manger.UserManger
import com.changanford.home.databinding.ItemMyActsBinding
import com.changanford.my.databinding.FragmentActBinding
import com.changanford.my.viewmodel.ActViewModel
import com.scwang.smart.refresh.layout.SmartRefreshLayout

/**
 *  文件名：CollectFragment
 *  创建者: zcy
 *  创建日期：2021/9/26 17:03
 *  描述: TODO
 *  修改描述：TODO
 */

class ActFragment : BaseMineFM<FragmentActBinding, ActViewModel>() {
    var type: String = ""
    var userId: String = ""

    val actAdapter: ActAdapter by lazy {
        ActAdapter()
    }

    companion object {
        fun newInstance(value: String, userId: String = ""): ActFragment {
            var bundle: Bundle = Bundle()
            bundle.putString(RouterManger.KEY_TO_OBJ, value)
            bundle.putString(RouterManger.KEY_TO_ID, userId)
            var medalFragment = ActFragment()
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

        binding.rcyAct.rcyCommonView.adapter = actAdapter
    }

    override fun bindSmartLayout(): SmartRefreshLayout? {
        return binding.rcyAct.smartCommonLayout
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
                    completeRefresh(reponse?.data?.dataList, actAdapter, total)
                }
            }
            "footAct" -> {
                viewModel.queryMineFootAc(pageSize) { reponse ->
                    reponse?.data?.total?.let {
                        total = it
                    }
                    completeRefresh(reponse?.data?.dataList, actAdapter, total)
                }
            }
            "actMyCreate" -> {
                viewModel.queryMineSendAc(userId, pageSize) { reponse ->
                    reponse?.data?.total?.let {
                        total = it
                    }
                    completeRefresh(reponse?.data?.dataList, actAdapter, total)
                }
            }
            "actMyJoin" -> {
                viewModel.queryMineJoinAc(pageSize) { reponse ->
                    reponse?.data?.total?.let {
                        total = it
                    }
                    completeRefresh(reponse?.data?.dataList, actAdapter, total)
                }
            }
        }
    }

    inner class ActAdapter :
        BaseQuickAdapter<ActDataBean, BaseViewHolder>(com.changanford.home.R.layout.item_my_acts) {
        override fun convert(holder: BaseViewHolder, item: ActDataBean) {
            var itemBinding = DataBindingUtil.bind<ItemMyActsBinding>(holder.itemView)

        }
    }
}