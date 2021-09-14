package com.changanford.my.ui

import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.alibaba.android.arouter.facade.annotation.Route
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseDataBindingHolder
import com.changanford.common.router.path.ARouterMyPath
import com.changanford.my.BaseMineUI
import com.changanford.my.R
import com.changanford.my.bean.GrowUpItem
import com.changanford.my.databinding.ItemJifenBinding
import com.changanford.my.databinding.UiJifenBinding
import com.changanford.my.databinding.ViewTaskHead1Binding
import com.changanford.my.viewmodel.SignViewModel
import com.scwang.smart.refresh.layout.SmartRefreshLayout
import kotlinx.coroutines.launch

/**
 *  文件名：JiFenList
 *  创建者: zcy
 *  创建日期：2021/9/13 17:27
 *  描述: TODO
 *  修改描述：TODO
 */
@Route(path = ARouterMyPath.MineIntegralUI)
class JiFenList : BaseMineUI<UiJifenBinding, SignViewModel>() {

    val jfAdapter: JifenAdapter by lazy {
        JifenAdapter()
    }

    override fun initView() {

        var headView = ViewTaskHead1Binding.inflate(layoutInflater)
        jfAdapter.addHeaderView(headView.root)

        binding.rcyJifen.rcyCommonView.layoutManager = LinearLayoutManager(this)
        binding.rcyJifen.rcyCommonView.adapter = jfAdapter
        viewModel.jifenBean.observe(this, Observer {
            if (null == it) {
                showEmpty()?.let { empty ->
                    jfAdapter.setEmptyView(empty)
                }
            } else {
                completeRefresh(it.dataList, jfAdapter, it.total)
            }
        })
    }

    override fun bindSmartLayout(): SmartRefreshLayout? {
        return binding.rcyJifen.smartCommonLayout
    }


    override fun initRefreshData(pageSize: Int) {
        lifecycleScope.launch {
            task(pageSize)
        }
    }

    private suspend fun task(pageSize: Int) {
        viewModel.mineGrowUp(pageSize, "1")
    }

    inner class JifenAdapter :
        BaseQuickAdapter<GrowUpItem, BaseDataBindingHolder<ItemJifenBinding>>(R.layout.item_jifen) {
        override fun convert(holder: BaseDataBindingHolder<ItemJifenBinding>, item: GrowUpItem) {

        }
    }
}