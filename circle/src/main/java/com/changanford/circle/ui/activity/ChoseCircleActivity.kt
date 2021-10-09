package com.changanford.circle.ui.activity

import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.alibaba.android.arouter.facade.annotation.Route
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.listener.OnItemClickListener
import com.changanford.circle.R
import com.changanford.circle.adapter.ChoseCircleAdapter
import com.changanford.circle.bean.ChooseCircleBean
import com.changanford.circle.databinding.ChooseCircleBinding
import com.changanford.common.basic.BaseActivity
import com.changanford.common.basic.EmptyViewModel
import com.changanford.common.router.path.ARouterCirclePath
import com.changanford.common.util.AppUtils
import com.changanford.common.util.bus.LiveDataBus
import com.changanford.common.util.bus.LiveDataBusKey

@Route(path = ARouterCirclePath.ChoseCircleActivity)
class ChoseCircleActivity : BaseActivity<ChooseCircleBinding, EmptyViewModel>() {
    val adapter by lazy {
        ChoseCircleAdapter()
    }

    override fun initView() {
        AppUtils.setStatusBarPaddingTop(binding.title.commTitleBar, this)
        binding.title.barTvTitle.text = "选择圈子"
        binding.rec.layoutManager = LinearLayoutManager(this)
        binding.rec.adapter = adapter
    }

    override fun initData() {
        binding.tvNocy.setOnClickListener {
            LiveDataBus.get().with(LiveDataBusKey.CIRCLECHOOSE)
                .postValue(binding.tvNocy.text.toString())
            finish()
        }
        var cbean = ChooseCircleBean(ItemType = 1, title = "我创建的")
        var cbean11 = ChooseCircleBean(name = "ljljaf", url = "dasfas")
        var cbean111 = ChooseCircleBean(name = "ljljaf", url = "dasfas")
        var cbean1 = ChooseCircleBean(name = "ljljaf", url = "dasfas")
        var cbean2 = ChooseCircleBean(ItemType = 1, title = "我加入的")
        var cbean1122 = ChooseCircleBean(name = "ljljaf", url = "dasfas")
        var cbean1112 = ChooseCircleBean(name = "ljljaf", url = "dasfas")
        var cbean12 = ChooseCircleBean(name = "ljljaf", url = "dasfas")
        val arrayListOf = arrayListOf<ChooseCircleBean>()
        arrayListOf.add(cbean)
        arrayListOf.add(cbean11)
        arrayListOf.add(cbean111)
        arrayListOf.add(cbean1)
        arrayListOf.add(cbean2)
        arrayListOf.add(cbean1122)
        arrayListOf.add(cbean1112)
        arrayListOf.add(cbean12)
        adapter.addData(arrayListOf)
        adapter.setEmptyView(R.layout.view_empty)
        adapter.notifyDataSetChanged()
        adapter.setOnItemClickListener { madapter, view, position ->
            if (adapter.getItem(position).itemType == 2) {
                LiveDataBus.get().with(LiveDataBusKey.CIRCLECHOOSE)
                    .postValue(adapter.getItem(position).name)
                finish()
            }
        }
    }
}