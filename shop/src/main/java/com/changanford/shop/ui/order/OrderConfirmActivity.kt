package com.changanford.shop.ui.order

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.View
import com.alibaba.android.arouter.facade.annotation.Route
import com.changanford.common.basic.BaseActivity
import com.changanford.common.bean.AddressBeanItem
import com.changanford.common.router.path.ARouterShopPath
import com.changanford.common.util.JumpUtils
import com.changanford.common.util.bus.LiveDataBus
import com.changanford.common.util.bus.LiveDataBusKey
import com.changanford.shop.R
import com.changanford.shop.databinding.ActOrderConfirmBinding
import com.changanford.shop.viewmodel.OrderViewModel
import com.google.gson.Gson

/**
 * @Author : wenke
 * @Time : 2021/9/9
 * @Description : 订单确认
 */
@Route(path = ARouterShopPath.OrderConfirmActivity)
class OrderConfirmActivity:BaseActivity<ActOrderConfirmBinding, OrderViewModel>(){
    companion object{
        fun start(context: Context, goodsInfo:String) {
            context.startActivity(Intent(context, OrderConfirmActivity::class.java).putExtra("goodsInfo",goodsInfo))
        }
    }
    override fun initView() {
        val goodsInfo=intent.getStringExtra("goodsInfo")
        Log.e("okhttp","goodsInfo:$goodsInfo")
        binding.topBar.setActivity(this)
    }

    override fun initData() {
        viewModel.addressList.observe(this,{ addressList ->
            var item:AddressBeanItem?=null
            if(null!=addressList&&addressList.isNotEmpty()){
                val items=addressList.filter { it.isDefault==1 }
                item=if(items.isNotEmpty())items[0]
                else addressList[0]
            }
            bindingAddress(item)
        })
        viewModel.getAddressList()
    }
    fun onClick(v:View){
        when(v.id){
            //提交订单
            R.id.btn_submit->PayConfirmActivity.start(this,"orderInfo")
            //选择地址
            R.id.in_address->selectAddress()
        }
    }
    @SuppressLint("SetTextI18n")
    private fun bindingAddress(item:AddressBeanItem?){
        if(null==item){//未绑定地址
            binding.inAddress.tvAddress.setText(R.string.str_pleaseAddShippingAddress)
            binding.inAddress.tvAddressRemark.visibility=View.GONE
        }else{
            binding.inAddress.tvAddressRemark.visibility=View.VISIBLE
            binding.inAddress.tvAddress.text="${item.provinceName}${item.cityName}${item.districtName}${item.addressName}"
            binding.inAddress.tvAddressRemark.text="${item.consignee}   ${item.phone}"
        }
    }
    private fun selectAddress(){
        JumpUtils.instans?.jump(20,"1")
        //地址下列表点击后回调
        LiveDataBus.get().with(LiveDataBusKey.MINE_CHOOSE_ADDRESS_SUCCESS, String::class.java).observe(this, {
                    Log.e("wenke","所选地址为:$it")
                    it?.let {bindingAddress(Gson().fromJson(it,AddressBeanItem::class.java))}
                })
    }
}