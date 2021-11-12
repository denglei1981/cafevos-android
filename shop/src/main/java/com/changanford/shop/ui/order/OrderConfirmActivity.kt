package com.changanford.shop.ui.order

import android.annotation.SuppressLint
import android.text.TextUtils
import android.util.Log
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
import com.changanford.common.utilext.load
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
        fun start(goodsInfo:String) {
            JumpUtils.instans?.jump(109,goodsInfo)
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
        if(MConstant.isShowLog)Log.e("okhttp","goodsInfo:$goodsInfo")
        dataBean=Gson().fromJson(goodsInfo,GoodsDetailBean::class.java)
        dataBean.isAgree=false
        initLiveDataBus()
    }

    override fun initData() {
        binding.inGoodsInfo.addSubtractView.apply {
            val limitBuyNum=(dataBean.limitBuyNum?:"0").toInt()
            setMax(if(0==limitBuyNum)dataBean.stock else limitBuyNum)
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
            val source=it.source
            if(source!="0")it.source=if(dataBean.spuPageType=="2") "2" else dataBean.source
            PayConfirmActivity.start(Gson().toJson(it))
            LiveDataBus.get().with(LiveDataBusKey.SHOP_CREATE_ORDER_BACK, String::class.java).postValue(it.source)
            this.finish()
        })
        bindingBaseData()
    }
    @SuppressLint("StringFormatMatches")
    private fun bindingBaseData(){
        //秒杀情况下 原价=现价
        if("SECKILL"==dataBean.spuPageType){
            dataBean.orginPrice=dataBean.fbPrice
        }
        //购买数量
        val buyNum=dataBean.buyNum
        //运费 1元=100积分
        val freightPrice=((dataBean.freightPrice?:"0.00").toFloat()*100).toInt()
        //单价（现价）
        val fbPrice=dataBean.fbPrice.toInt()
        //总商品价 单价*购买数量
        val totalFb=fbPrice*buyNum
        //总共支付 (商品金额+运费)
        val totalPayFb:Int=totalFb+freightPrice
        dataBean.totalPayFb="$totalPayFb"
        binding.inOrderInfo.apply {
            if(dataBean.freightPrice=="0")dataBean.freightPrice="0.00"
            //单价（原价）
            val originalPrice=(dataBean.orginPrice?:dataBean.fbPrice).toInt()
            //原总商品价 单价*购买数量
            val totalOriginalFb=originalPrice*buyNum
            tvAmountValue.setText("$totalOriginalFb")
            tvTotal.setHtmlTxt(getString(R.string.str_Xfb,"$totalPayFb"),"#00095B")
            val spuPageType=dataBean.spuPageType
            //会员折扣
            if("MEMBER_DISCOUNT"==spuPageType||"MEMBER_DISCOUNT"==dataBean.secondarySpuPageTagType){
                //会员优惠=原总价-现总价
                (totalOriginalFb-totalFb).apply {
                    if(this>0){
                        dataBean.preferentialFb="$this"
                        tvMemberDiscountValue.visibility=View.VISIBLE
                        tvMemberDiscount.visibility=View.VISIBLE
                    }
                }
            }
            model=dataBean
        }
        binding.inGoodsInfo.apply {
            OrderGoodsAttributeAdapter().apply {
                rvGoodsProperty.layoutManager= FlowLayoutManager(this@OrderConfirmActivity,false,true)
                rvGoodsProperty.adapter= this
                setList(dataBean.skuCodeTxts?.filter { ""!=it })
            }
            imgGoodsCover.load(dataBean.skuImg)
//            if(freightPrice!=0)tvDistributionType
            model=dataBean
        }
        binding.inBottom.apply {
            model=dataBean
            tvAcountFb.setText("${dataBean.acountFb}")
            updateBtnUi()
        }
    }
    private fun updateBtnUi(){
        dataBean.apply {
            binding.inBottom.btnSubmit.updateEnabled(isAgree&&null!=addressId&&totalPayFb.toInt()<=acountFb)
        }
    }
    fun onClick(v:View){
        when(v.id){
            //提交订单
            R.id.btn_submit->submitOrder()
            //选择地址
            R.id.in_address->JumpUtils.instans?.jump(20,"1")
            //服务协议
            R.id.tv_agreement->JumpUtils.instans?.jump(1,MConstant.H5_SHOP_AGREEMENT)
            //协议勾选
            R.id.checkBox->{
                dataBean.isAgree=binding.checkBox.isChecked
                updateBtnUi()
            }
        }
    }
    @DelicateCoroutinesApi
    private fun submitOrder(){
        if(!isClickSubmit){
            isClickSubmit=true
            val consumerMsg=binding.inGoodsInfo.edtLeaveMsg.text.toString()
            dataBean.apply {
                viewModel.orderCreate(skuId,addressId,spuPageType,buyNum,consumerMsg,mallMallSkuSpuSeckillRangeId,mallMallHaggleUserGoodsId)
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
            updateBtnUi()
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