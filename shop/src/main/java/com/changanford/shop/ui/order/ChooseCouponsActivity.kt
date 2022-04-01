package com.changanford.shop.ui.order

import android.os.Bundle
import com.alibaba.android.arouter.facade.annotation.Route
import com.changanford.common.basic.BaseActivity
import com.changanford.common.bean.CouponsItemBean
import com.changanford.common.router.path.ARouterShopPath
import com.changanford.common.router.startARouter
import com.changanford.shop.databinding.ActChooseCouponsBinding
import com.changanford.shop.ui.compose.ChooseCouponsCompose
import com.changanford.shop.viewmodel.OrderViewModel
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

/**
 * @Author : wenke
 * @Time : 2022/4/1 0001
 * @Description : 选择优惠券
 */
@Route(path = ARouterShopPath.ChooseCouponsActivity)
class ChooseCouponsActivity:BaseActivity<ActChooseCouponsBinding,OrderViewModel>() {
    companion object{
        fun start(skuIds:ArrayList<String>?,coupons: ArrayList<CouponsItemBean>?) {
            val bundle = Bundle()
            bundle.putStringArrayList("skuIds", skuIds)
            bundle.putString("dataList", Gson().toJson(coupons))
            startARouter(ARouterShopPath.ChooseCouponsActivity,bundle)
        }
    }
    private var skuIds:ArrayList<String>?=null
    private var dataListBean:ArrayList<CouponsItemBean>?=null
    override fun initView() {
        binding.topBar.setActivity(this)
    }

    override fun initData() {
        intent.getStringExtra("dataList")?.let {
            val gson=Gson()
            dataListBean = gson.fromJson(it, object : TypeToken<ArrayList<CouponsItemBean?>?>() {}.type)
            skuIds=gson.fromJson("skuIds", object : TypeToken<ArrayList<String?>?>() {}.type)
        }
        binding.composeView.setContent {
            ChooseCouponsCompose(dataListBean)
        }
    }
}