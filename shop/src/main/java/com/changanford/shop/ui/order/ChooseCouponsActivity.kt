package com.changanford.shop.ui.order

import android.os.Bundle
import com.alibaba.android.arouter.facade.annotation.Route
import com.changanford.common.basic.BaseActivity
import com.changanford.common.bean.CouponsItemBean
import com.changanford.common.bean.CreateOrderBean
import com.changanford.common.bean.OrderSkuItem
import com.changanford.common.router.path.ARouterShopPath
import com.changanford.common.router.startARouter
import com.changanford.shop.databinding.ActChooseCouponsBinding
import com.changanford.shop.ui.compose.ChooseCouponsCompose
import com.changanford.shop.viewmodel.OrderViewModel
import com.google.gson.Gson

/**
 * @Author : wenke
 * @Time : 2022/4/1 0001
 * @Description : 选择优惠券
 */
@Route(path = ARouterShopPath.ChooseCouponsActivity)
class ChooseCouponsActivity:BaseActivity<ActChooseCouponsBinding,OrderViewModel>() {
    companion object{
        /**
         * [defaultItem]默认选中("couponId_couponRecordId")
        * */
        fun start(defaultItem:String?=null, infoBean: CreateOrderBean?) {
            val bundle = Bundle()
            bundle.putString("defaultItem",defaultItem)
            bundle.putString("infoBean", Gson().toJson(infoBean))
            startARouter(ARouterShopPath.ChooseCouponsActivity,bundle)
        }
    }
    private lateinit var infoBean: CreateOrderBean
    private lateinit var skuItems:ArrayList<OrderSkuItem>
    private lateinit var couponListBean:ArrayList<CouponsItemBean>
    override fun initView() {
        binding.topBar.setActivity(this)
    }

    override fun initData() {
        intent.getStringExtra("infoBean")?.let {
            infoBean=Gson().fromJson(it,CreateOrderBean::class.java)
            couponListBean=infoBean.coupons?: arrayListOf()
            skuItems=infoBean.skuItems?: arrayListOf()
            formattingData()
        }
    }
    private fun formattingData(){
        //判断每个优惠券是否可用
        for ((i,item)in couponListBean.withIndex()){
            item.isAvailable=false
            item.mallMallSkuIds?.forEach{skuId->
                //查询skuId是否在订单中
                skuItems.find { skuId== it.skuId }?.apply {
                    couponListBean[i].isAvailable=true
                }
            }
        }
        //默认选中
        val defaultItem=intent.getStringExtra("defaultItem")
        val defaultItemBean=couponListBean.find { "${it.couponId}_${it.couponRecordId}"==defaultItem }
        binding.composeView.setContent {
            ChooseCouponsCompose(this,defaultItemBean,couponListBean)
        }
    }
}