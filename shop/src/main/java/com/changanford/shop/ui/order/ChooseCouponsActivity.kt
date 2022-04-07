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
        intent.getStringExtra("infoBean")?.let {infoItem->
            infoBean=Gson().fromJson(infoItem,CreateOrderBean::class.java)
            couponListBean=infoBean.coupons?: arrayListOf()
            skuItems=infoBean.skuItems?: arrayListOf()
//            formattingData()
            //默认选中
            val defaultItem=intent.getStringExtra("defaultItem")
            val defaultItemBean=couponListBean.find { "${it.couponId}_${it.couponRecordId}"==defaultItem }
            binding.composeView.setContent {
                ChooseCouponsCompose(this,defaultItemBean,couponListBean)
            }
        }
    }
    private fun formattingData(){
        //判断每个优惠券是否可用
        for ((i,item)in couponListBean.withIndex()){
            item.isAvailable=false
            var totalPrice:Long=0//满足优惠券的总价
            item.mallMallSkuIds?.forEach{skuId->
                //查询skuId是否在订单中
                skuItems.find { skuId== it.skuId }?.apply {
                    //计算总价=单价福币*数量
                    totalPrice+=((unitPriceFb?:"0").toLong()*num)
                }
            }
            //该券满足优惠条件
            if(totalPrice>=item.conditionMoney){
                //标注该券可用
                item.isAvailable=true
                //计算实际优惠
                val discountsFb:Long=when(item.discountType){
                    //折扣
                    "DISCOUNT"->{
                        //折扣金额
                        val discountAmount=item.discountAmount(totalPrice)
                        //最大折扣
                        if(discountAmount<=item.couponMoney)discountAmount else item.couponMoney
                    }
                    //满减和立减
                    else -> item.couponMoney
                }
                item.discountsFb=discountsFb
            }
            couponListBean[i]=item
        }
        //排序从小到大 然后倒叙（则结果是从大到小）
        val sortList=couponListBean.sortedWith(compareBy { it.discountsFb}).reversed()
        //根据条件将优惠分成两份
        val (match, rest) = sortList.partition { it.isAvailable }
        val newList= arrayListOf<CouponsItemBean>()
        newList.addAll(match)
        newList.addAll(rest)
        //默认选中
        val defaultItem=intent.getStringExtra("defaultItem")
        val defaultItemBean=newList.find { "${it.couponId}_${it.couponRecordId}"==defaultItem }
        binding.composeView.setContent {
            ChooseCouponsCompose(this,defaultItemBean,newList)
        }
    }
}