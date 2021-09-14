package com.changanford.my.ui

import com.alibaba.android.arouter.facade.annotation.Route
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseDataBindingHolder
import com.changanford.common.basic.EmptyViewModel
import com.changanford.common.router.path.ARouterMyPath
import com.changanford.my.BaseMineUI
import com.changanford.my.R
import com.changanford.my.bean.TaskBean
import com.changanford.my.bean.getTaskBean
import com.changanford.my.databinding.ItemJifenBinding
import com.changanford.my.databinding.UiJifenBinding
import com.changanford.my.databinding.ViewTaskHead1Binding

/**
 *  文件名：JiFenList
 *  创建者: zcy
 *  创建日期：2021/9/13 17:27
 *  描述: TODO
 *  修改描述：TODO
 */
@Route(path = ARouterMyPath.MineIntegralUI)
class JiFenList : BaseMineUI<UiJifenBinding, EmptyViewModel>() {

    val jfAdapter: JifenAdapter by lazy {
        JifenAdapter()
    }

    override fun initView() {

        var headView = ViewTaskHead1Binding.inflate(layoutInflater)
        jfAdapter.addHeaderView(headView.root)

        binding.rcyJifen.rcyCommonView.adapter = jfAdapter.apply {
            addData(getTaskBean)
            addData(getTaskBean)
            addData(getTaskBean)
        }
    }

    override fun initData() {

    }

    inner class JifenAdapter :
        BaseQuickAdapter<TaskBean, BaseDataBindingHolder<ItemJifenBinding>>(R.layout.item_jifen) {
        override fun convert(holder: BaseDataBindingHolder<ItemJifenBinding>, item: TaskBean) {

        }
    }
}