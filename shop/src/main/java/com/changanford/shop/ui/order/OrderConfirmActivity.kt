package com.changanford.shop.ui.order

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.text.TextUtils
import android.view.View
import com.alibaba.android.arouter.facade.annotation.Route
import com.changanford.common.basic.BaseActivity
import com.changanford.common.bean.AddressBeanItem
import com.changanford.common.bean.GoodsDetailBean
import com.changanford.common.router.path.ARouterShopPath
import com.changanford.common.util.JumpUtils
import com.changanford.common.util.MConstant
import com.changanford.common.util.bus.LiveDataBus
import com.changanford.common.util.bus.LiveDataBusKey
import com.changanford.common.util.toast.ToastUtils
import com.changanford.common.utilext.GlideUtils
import com.changanford.shop.R
import com.changanford.shop.adapter.FlowLayoutManager
import com.changanford.shop.adapter.goods.OrderGoodsAttributeAdapter
import com.changanford.shop.databinding.ActOrderConfirmBinding
import com.changanford.shop.viewmodel.OrderViewModel
import com.google.gson.Gson
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 * @Author : wenke
 * @Time : 2021/9/9
 * @Description : 订单确认
 */
@Route(path = ARouterShopPath.OrderConfirmActivity)
class OrderConfirmActivity:BaseActivity<ActOrderConfirmBinding, OrderViewModel>(){
    companion object{
        fun start(context: Context, goodsInfo:String) {
            if(MConstant.token.isEmpty())JumpUtils.instans?.jump(100)
            else context.startActivity(Intent(context, OrderConfirmActivity::class.java).putExtra("goodsInfo",goodsInfo))
        }
    }
    private lateinit var dataBean:GoodsDetailBean
    private var isClickSubmit=false
    override fun initView() {
        binding.topBar.setActivity(this)
        val goodsInfo=intent.getStringExtra("goodsInfo")
        if(TextUtils.isEmpty(goodsInfo)){
            ToastUtils.showLongToast(getString(R.string.str_parameterIllegal),this)
            this.finish()
            return
        }
        dataBean=Gson().fromJson(goodsInfo,GoodsDetailBean::class.java)
        initLiveDataBus()
    }

    override fun initData() {
        binding.inGoodsInfo.addSubtractView.apply {
            setMax(dataBean.stock)
            setNumber(dataBean.buyNum,false)
            numberLiveData.observe(this@OrderConfirmActivity,{
                dataBean.buyNum= it
                bindingBaseData()
            })
        }
        viewModel.addressList.observe(this,{ addressList ->
            //默认获取地址列表的默认收货地址
            val item:AddressBeanItem?=addressList?.find { it.isDefault==1 }
            bindingAddress(item)
        })
        val addressInfo=dataBean.addressInfo
        if(TextUtils.isEmpty(addressInfo))viewModel.getAddressList()
        else viewModel.addressList.postValue(arrayListOf(Gson().fromJson(addressInfo,AddressBeanItem::class.java)))
        viewModel.orderInfoLiveData.observe(this,{
            isClickSubmit=false
            it.accountFb=dataBean.acountFb.toString()
            PayConfirmActivity.start(this,Gson().toJson(it))
            this.finish()
        })
        bindingBaseData()
    }
    @SuppressLint("StringFormatMatches")
    private fun bindingBaseData(){
        //购买数量
        val buyNum=dataBean.buyNum
        //运费 1元=100积分
        val freightPrice=(dataBean.freightPrice.toFloat()*100).toInt()
        //单价
        val fbPrice=dataBean.fbPrice.toInt()
        //总商品价 单价*购买数量
        val totalFb=fbPrice*buyNum
        //总共支付 (商品金额+运费)
        val totalPayFb:Int=totalFb+freightPrice
        dataBean.totalPayFb="$totalPayFb"
        binding.inOrderInfo.apply {
            if(dataBean.freightPrice=="0")dataBean.freightPrice="0.00"
            tvAmountValue.setText("$totalFb")
            tvTotal.setHtmlTxt(getString(R.string.str_Xfb,"$totalPayFb"),"#00095B")
            //会员折扣
            if("MEMBER_DISCOUNT"==dataBean.spuPageType){
                //会员优惠=原价-现价 会员折扣的时候才显示
                val memberDiscount=(dataBean.orginPrice?:"0").toInt()*buyNum-totalFb
                dataBean.preferentialFb="$memberDiscount"
                tvMemberDiscountValue.visibility=View.VISIBLE
                tvMemberDiscount.visibility=View.VISIBLE
            }
            model=dataBean
        }
        binding.inGoodsInfo.apply {
            model=dataBean
            OrderGoodsAttributeAdapter().apply {
                rvGoodsProperty.layoutManager= FlowLayoutManager(this@OrderConfirmActivity,false)
                rvGoodsProperty.adapter= this
                setList(dataBean.skuCodeTxts)
            }
//            val skuItem=dataBean.skuVos.find { it.skuId==dataBean.skuId }?:dataBean.skuVos[0]
            GlideUtils.loadBD(GlideUtils.handleImgUrl(dataBean.skuImg),imgGoodsCover)
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
            R.id.in_address->JumpUtils.instans?.jump(20,"1")
        }
    }
    @DelicateCoroutinesApi
    private fun submitOrder(){
        if(!isClickSubmit){
            isClickSubmit=true
            val consumerMsg=binding.inGoodsInfo.edtLeaveMsg.text.toString()
            dataBean.apply {
                viewModel.orderCreate(skuId,addressId,spuPageType,buyNum,consumerMsg,mallMallSkuSpuSeckillRangeId)
            }
        }
        GlobalScope.launch {
            delay(3000L)
            isClickSubmit=false
        }
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
    private fun initLiveDataBus(){
        //地址下列表点击后回调
        LiveDataBus.get().with(LiveDataBusKey.MINE_CHOOSE_ADDRESS_SUCCESS, String::class.java).observe(this, {
            it?.let {
                bindingAddress(Gson().fromJson(it,AddressBeanItem::class.java))
            }
        })
    }
}