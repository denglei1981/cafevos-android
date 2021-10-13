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
import com.changanford.common.utilext.GlideUtils
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
        binding.inGoodsInfo.addSubtractView.numberLiveData.observe(this,{
            dataBean.buyNum= it
            bindingBaseData()
        })
        viewModel.getAddressList()
        bindingBaseData()
        binding.inGoodsInfo.addSubtractView.setNumber(dataBean.buyNum)
        viewModel.orderInfoLiveData.observe(this,{
            PayConfirmActivity.start(this,"orderInfo")
        })
    }
    @SuppressLint("StringFormatMatches")
    private fun bindingBaseData(){
        //购买数量
        val buyNum=dataBean.buyNum
        //运费
        val freightPrice=dataBean.freightPrice.toInt()
        //单价
        val fbPrice=dataBean.fbPrice.toInt()
        //总商品价 单价*购买数量
        val totalFb=fbPrice*buyNum
        //总共支付 (商品金额+运费)
        val totalPayFb=totalFb+freightPrice
        dataBean.totalPayFb="$totalPayFb"
        binding.inOrderInfo.apply {
            model=dataBean
            tvAmountValue.setText("$totalFb")
            tvTotal.setHtmlTxt(getString(R.string.str_Xfb,"$totalPayFb"),"#00095B")
            //会员优惠
//          val memberDiscount=dataBean.fbLine.toInt()*buyNum-totalFb
//          tvMemberDiscountValue.setText("$memberDiscount")
        }
        binding.inGoodsInfo.apply {
            model=dataBean
            val skuItem=dataBean.skuVos.find { it.skuId==dataBean.skuId }?:dataBean.skuVos[0]
            GlideUtils.loadBD(GlideUtils.handleImgUrl(skuItem.skuImg),imgGoodsCover)
            //if(freightPrice!=0)tvDistributionType
        }
        binding.inBottom.apply {
            model=dataBean
            tvAcountFb.setText("${dataBean.acountFb}")
            btnSubmit.updateEnabled(null!=dataBean.addressId&&dataBean.totalPayFb.toInt()<=dataBean.acountFb)
        }
    }
    fun onClick(v:View){
        when(v.id){
            //提交订单
            R.id.btn_submit->submitOrder()
            //选择地址
            R.id.in_address->selectAddress()
        }
    }
    private fun submitOrder(){
        val consumerMsg=binding.inGoodsInfo.edtLeaveMsg.text.toString()
        viewModel.orderCreate(dataBean.skuId,dataBean.addressId,dataBean.spuPageType,dataBean.buyNum,consumerMsg)
    }
    @SuppressLint("SetTextI18n")
    private fun bindingAddress(item:AddressBeanItem?){
        if(null==item){//未绑定地址
            dataBean.addressId=null
            binding.inAddress.tvAddress.setText(R.string.str_pleaseAddShippingAddress)
            binding.inAddress.tvAddressRemark.visibility=View.GONE
            binding.inBottom.btnSubmit.updateEnabled(false)
        }else{
            dataBean.addressId=item.addressId
            binding.inBottom.btnSubmit.updateEnabled(dataBean.totalPayFb.toInt()<=dataBean.acountFb)
            binding.inAddress.tvAddressRemark.visibility=View.VISIBLE
            binding.inAddress.tvAddress.text="${item.provinceName}${item.cityName}${item.districtName}${item.addressName}"
            binding.inAddress.tvAddressRemark.text="${item.consignee}   ${item.phone}"
        }
    }
    private fun selectAddress(){
        JumpUtils.instans?.jump(20,"1")
        //地址下列表点击后回调
        LiveDataBus.get().with(LiveDataBusKey.MINE_CHOOSE_ADDRESS_SUCCESS, String::class.java).observe(this, {
                    Log.e("okhttp","所选地址为:$it")
                    it?.let {bindingAddress(Gson().fromJson(it,AddressBeanItem::class.java))}
                })
    }
}