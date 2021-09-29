package com.changanford.my

import android.os.Bundle
import androidx.databinding.DataBindingUtil
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.changanford.common.bean.ActDataBean
import com.changanford.common.manger.RouterManger
import com.changanford.home.databinding.ItemHomeActsBinding
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

    val actAdapter: ActAdapter by lazy {
        ActAdapter()
    }

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

        binding.rcyCollect.rcyCommonView.adapter = actAdapter
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
        }
    }

    inner class ActAdapter :
        BaseQuickAdapter<ActDataBean, BaseViewHolder>(com.changanford.home.R.layout.item_home_acts) {
        override fun convert(holder: BaseViewHolder, item: ActDataBean) {
            var itemBinding = DataBindingUtil.bind<ItemHomeActsBinding>(holder.itemView)

        }
    }
}