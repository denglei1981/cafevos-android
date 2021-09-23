package com.changanford.shop.ui

import android.content.Context
import android.content.Intent
import com.changanford.common.basic.BaseActivity
import com.changanford.shop.ShopViewModel
import com.changanford.shop.adapter.IntegralDetailsAdapter
import com.changanford.shop.databinding.ActIntegralDetailsBinding

/**
 * @Author : wenke
 * @Time : 2021/9/9
 * @Description : 积分明细
 */
class IntegralDetailsActivity:BaseActivity<ActIntegralDetailsBinding,ShopViewModel> (){
    companion object{
        fun start(context: Context) {
            context.startActivity(Intent(context, IntegralDetailsActivity::class.java))
        }
    }
    private val mAdapter by lazy { IntegralDetailsAdapter() }
    override fun initView() {
        binding.recyclerView.adapter=mAdapter
    }

    override fun initData() {

    }
}