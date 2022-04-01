package com.changanford.shop.ui.order

import android.os.Bundle
import com.alibaba.android.arouter.facade.annotation.Route
import com.changanford.common.basic.BaseActivity
import com.changanford.common.bean.CouponsItemBean
import com.changanford.common.bean.OrderSkuItem
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
        fun start(skuItems:ArrayList<OrderSkuItem>?, coupons: ArrayList<CouponsItemBean>?) {
            val bundle = Bundle()
            bundle.putString("skuItems", Gson().toJson(skuItems))
            bundle.putString("dataList", Gson().toJson(coupons))
            startARouter(ARouterShopPath.ChooseCouponsActivity,bundle)
        }
    }
    private lateinit var skuItems:ArrayList<OrderSkuItem>
    private lateinit var dataListBean:ArrayList<CouponsItemBean>
    override fun initView() {
        binding.topBar.setActivity(this)
    }

    override fun initData() {
        intent.getStringExtra("dataList")?.let {
            val gson=Gson()
            dataListBean = gson.fromJson(it, object : TypeToken<ArrayList<CouponsItemBean?>?>() {}.type)
            skuItems=gson.fromJson(intent.getStringExtra("skuItems"), object : TypeToken<ArrayList<OrderSkuItem?>?>() {}.type)
            formattingData()
        }
    }
    private fun formattingData(){
        //判断每个优惠券是否可用
        for ((i,item)in dataListBean.withIndex()){
            item.isAvailable=false
            item.mallMallSkuIds?.forEach{skuId->
                //查询skuId是否在订单中
                skuItems.find { skuId==it.skuId }?.apply {
                    dataListBean[i].isAvailable=true
                }
            }
        }
        binding.composeView.setContent {
            ChooseCouponsCompose(dataListBean)
        }
    }
}