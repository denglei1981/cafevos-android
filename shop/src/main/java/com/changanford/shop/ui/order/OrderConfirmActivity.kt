package com.changanford.shop.ui.order

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.View
import com.alibaba.android.arouter.facade.annotation.Route
import com.changanford.common.basic.BaseActivity
import com.changanford.common.bean.AddressBeanItem
import com.changanford.common.bean.GoodsDetailBean
import com.changanford.common.router.path.ARouterShopPath
import com.changanford.common.util.JumpUtils
import com.changanford.common.util.bus.LiveDataBus
import com.changanford.common.util.bus.LiveDataBusKey
import com.changanford.common.util.toast.ToastUtils
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
    private lateinit var dataBean:GoodsDetailBean
    override fun initView() {
        binding.topBar.setActivity(this)
        val goodsInfo=intent.getStringExtra("goodsInfo")
        if(null==goodsInfo){
            ToastUtils.showLongToast(getString(R.string.str_parameterIllegal),this)
            this.finish()
            return
        }
        dataBean=Gson().fromJson(goodsInfo,GoodsDetailBean::class.java)
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
        bindingBaseData()
    }
    private fun bindingBaseData(){
        //购买数量
        val buyNum=dataBean.buyNum
        //运费
        val freightPrice=dataBean.freightPrice.toInt()
        //单价
        val fbPrice=dataBean.fbPrice.toInt()
        //总商品价 单价*购买数量
        val totalFb=fbPrice*buyNum
        binding.inOrderInfo.tvAmountValue.setText("$totalFb")
        //会员优惠
//        val memberDiscount=dataBean.fbLine.toInt()*buyNum-totalFb
//        binding.inOrderInfo.tvMemberDiscountValue.setText("$memberDiscount")
        //总共支付 (商品金额+运费)
        val totalPayFb=totalFb+freightPrice
        dataBean.totalPayFb="$totalPayFb"
        binding.inOrderInfo.tvTotal.setHtmlTxt("$totalPayFb","#00095B")

        val goodsInfo=binding.inGoodsInfo
        goodsInfo.addSubtractView.setNumber(buyNum)
//        if(freightPrice!=0)goodsInfo.tvDistributionType
        binding.inOrderInfo.model=dataBean
        goodsInfo.model=dataBean
        binding.inBottom.model=dataBean
        binding.inBottom.tvAcountFb.setText("${dataBean.acountFb}")
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