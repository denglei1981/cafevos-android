package com.changanford.shop.ui

import android.content.Context
import android.content.Intent
import com.alibaba.android.arouter.facade.annotation.Route
import com.changanford.common.basic.BaseActivity
import com.changanford.common.router.path.ARouterShopPath
import com.changanford.shop.viewmodel.ShopViewModel
import com.changanford.shop.adapter.IntegralDetailsAdapter
import com.changanford.shop.databinding.ActIntegralDetailsBinding
import com.changanford.shop.view.TopBar

/**
 * @Author : wenke
 * @Time : 2021/9/9
 * @Description : 积分明细
 */
@Deprecated("废弃")
@Route(path = ARouterShopPath.IntegralDetailsActivity)
class IntegralDetailsActivity:BaseActivity<ActIntegralDetailsBinding, ShopViewModel> (), TopBar.OnRightClickListener {
    companion object{
        fun start(context: Context) {
            context.startActivity(Intent(context, IntegralDetailsActivity::class.java))
        }
    }
    private val mAdapter by lazy { IntegralDetailsAdapter() }
    override fun initView() {
        binding.recyclerView.adapter=mAdapter
        binding.topBar.setActivity(this)
        binding.topBar.setOnRightClickListener(this)
    }

    override fun initData() {
        mAdapter.setList(arrayListOf("","","","","","","","",""))
    }
    /**
     * 搜索点击
    * */
    override fun onRightClick() {

    }
}